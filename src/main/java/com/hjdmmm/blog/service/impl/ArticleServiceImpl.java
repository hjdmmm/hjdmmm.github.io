package com.hjdmmm.blog.service.impl;

import com.hjdmmm.blog.config.ArticleConfig;
import com.hjdmmm.blog.constant.ArticleConstants;
import com.hjdmmm.blog.dao.ArticleDAO;
import com.hjdmmm.blog.dao.TagDAO;
import com.hjdmmm.blog.dao.UserDAO;
import com.hjdmmm.blog.domain.dto.ArticleDTO;
import com.hjdmmm.blog.domain.dto.ArticleDetailDTO;
import com.hjdmmm.blog.domain.dto.UserDTO;
import com.hjdmmm.blog.domain.entity.Article;
import com.hjdmmm.blog.domain.entity.ArticleTag;
import com.hjdmmm.blog.domain.entity.Tag;
import com.hjdmmm.blog.domain.model.AddArticleModel;
import com.hjdmmm.blog.domain.vo.ArticleDetailVO;
import com.hjdmmm.blog.domain.vo.ArticleNodeVO;
import com.hjdmmm.blog.domain.vo.PageVO;
import com.hjdmmm.blog.domain.vo.TagVO;
import com.hjdmmm.blog.enums.ArticleStatusEnum;
import com.hjdmmm.blog.service.ArticleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ArticleServiceImpl implements ArticleService {
    private final ArticleDAO articleDAO;

    private final TagDAO tagDAO;

    private final UserDAO userDAO;

    private final ArticleConfig articleConfig;

    private static String getNickName(UserDTO user) {
        if (user == null) {
            return "未知作者";
        }

        return user.username();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long add(AddArticleModel model, long operator) throws Exception {
        int status;
        if (model.draft()) {
            status = ArticleStatusEnum.DRAFT.number;
        } else {
            status = ArticleStatusEnum.PUBLISHED.number;
        }
        Article article = Article.builder()
            .status(status)
            .title(model.title())
            .content("")
            .summary("")
            .parentArticleId(Optional.ofNullable(model.parentArticleId()).orElse(ArticleConstants.ROOT_ARTICLE_PID))
            .viewCount(0L)
            .createBy(operator)
            .updateBy(operator)
            .build();
        articleDAO.insert(article);
        return article.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(long id, long modifier) throws Exception {
        articleDAO.delete(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTitle(long id, String title, long modifier) throws Exception {
        articleDAO.updateTitleById(id, title, modifier);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateContent(long id, String content, long modifier) throws Exception {
        articleDAO.updateContentById(id, content, modifier);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateParent(long id, Long parentId, long modifier) throws Exception {
        articleDAO.updateParentById(id, Optional.ofNullable(parentId).orElse(ArticleConstants.ROOT_ARTICLE_PID), modifier);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTagIds(long id, List<Long> tagIds, long modifier) throws Exception {
        articleDAO.updateTagIds(id, tagIds, modifier);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void increaseViewCount(long id) throws Exception {
        articleDAO.increaseViewCount(id);
    }

    @Override
    public List<Long> getHotArticleIds() throws Exception {
        List<ArticleDTO> articles = articleDAO.pageSelectOrderByViewCountDescWithTagId(ArticleStatusEnum.PUBLISHED.number, 1, 10, articleConfig.getNormalArticleTagId()).rows();
        return articles.stream().map(ArticleDTO::id).toList();
    }

    @Override
    public List<Long> getLatestArticleIds() throws Exception {
        List<ArticleDTO> articles = articleDAO.pageSelectOrderByCreateTimeDescWithTagId(ArticleStatusEnum.PUBLISHED.number, 1, 10, articleConfig.getNormalArticleTagId()).rows();
        return articles.stream().map(ArticleDTO::id).toList();
    }

    @Override
    public PageVO<Long> listCategoryArticleIds(int pageNum, int pageSize) throws Exception {
        PageVO<ArticleDTO> pageVO = articleDAO.pageSelect(ArticleConstants.ROOT_ARTICLE_PID, ArticleStatusEnum.PUBLISHED.number, pageNum, pageSize);
        return new PageVO<>(pageVO.rows().stream().map(ArticleDTO::id).toList(), pageVO.total());
    }

    @Override
    public List<ArticleNodeVO> getArticleNodes(List<Long> articleIds, Long userId) throws Exception {
        List<ArticleDTO> articles = articleDAO.selectByStatusAndIdsOrParentIds(ArticleStatusEnum.PUBLISHED.number, articleIds, articleIds);
        Map<Long, List<ArticleDTO>> parentId2ArticlesMap = articles.stream().filter(article -> articleIds.contains(article.parentArticleId())).collect(Collectors.groupingBy(ArticleDTO::parentArticleId));
        List<ArticleNodeVO> result = new ArrayList<>();
        for (ArticleDTO article : articles) {
            if (!articleIds.contains(article.id())) {
                continue;
            }
            Long parentId = article.parentArticleId() == ArticleConstants.ROOT_ARTICLE_PID ? null : article.parentArticleId();
            List<Long> children = parentId2ArticlesMap.getOrDefault(article.id(), Collections.emptyList()).stream().map(ArticleDTO::id).toList();
            ArticleNodeVO node = ArticleNodeVO.builder()
                .id(article.id())
                .title(article.title())
                .parentId(parentId)
                .canCreateChild(userId != null)
                .canDelete(userId != null)
                .canChangeParent(userId != null)
                .children(children)
                .build();
            result.add(node);
        }
        return result;
    }

    @Override
    public ArticleDetailVO getArticleDetail(long id, Long userId) throws Exception {
        ArticleDetailDTO articleDetailDTO = articleDAO.selectDetail(id);
        if (articleDetailDTO == null) {
            return null;
        }
        Article article = articleDetailDTO.article();
        List<Long> tagIds = articleDetailDTO.articleTags().stream().map(ArticleTag::getTagId).toList();
        Map<Long, Tag> tagId2TagMap = tagDAO.selectAll(tagIds).stream().collect(Collectors.toMap(Tag::getId, Function.identity()));
        List<TagVO> tags = tagIds.stream().map(tagId -> TagVO.builder().id(tagId).name(Optional.ofNullable(tagId2TagMap.get(tagId)).map(Tag::getName).orElse(null)).build()).toList();
        UserDTO user = userDAO.select(article.getCreateBy());
        return ArticleDetailVO.builder()
            .id(article.getId())
            .title(article.getTitle())
            .content(article.getContent())
            .viewCount(article.getViewCount())
            .createTime(article.getCreateTime().toEpochMilli())
            .updateTime(article.getUpdateTime().toEpochMilli())
            .author(getNickName(user))
            .canEdit(userId != null)
            .tags(tags)
            .build();
    }

    @Override
    public PageVO<ArticleDTO> list(int pageNum, int pageSize, String title, String summary) throws Exception {
        return articleDAO.pageSelect(pageNum, pageSize, title, summary);
    }

}
