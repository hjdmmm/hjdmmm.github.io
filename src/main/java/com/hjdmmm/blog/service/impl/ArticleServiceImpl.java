package com.hjdmmm.blog.service.impl;

import com.hjdmmm.blog.dao.ArticleDAO;
import com.hjdmmm.blog.dao.ArticleTagDAO;
import com.hjdmmm.blog.dao.CategoryDAO;
import com.hjdmmm.blog.dao.UserDAO;
import com.hjdmmm.blog.domain.entity.Article;
import com.hjdmmm.blog.domain.entity.ArticleTag;
import com.hjdmmm.blog.domain.entity.Category;
import com.hjdmmm.blog.domain.entity.User;
import com.hjdmmm.blog.domain.vo.*;
import com.hjdmmm.blog.service.ArticleService;
import com.hjdmmm.blog.util.BeanUtils;
import com.hjdmmm.blog.util.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl implements ArticleService {
    private final ArticleDAO articleDAO;

    private final ArticleTagDAO articleTagDAO;

    private final CategoryDAO categoryDAO;

    private final UserDAO userDAO;

    public ArticleServiceImpl(ArticleDAO articleDAO, ArticleTagDAO articleTagDAO, CategoryDAO categoryDAO, UserDAO userDAO) {
        this.articleDAO = articleDAO;
        this.articleTagDAO = articleTagDAO;
        this.categoryDAO = categoryDAO;
        this.userDAO = userDAO;
    }

    private static ArticleVO buildArticleVO(Article article, List<Long> tags) {
        ArticleVO articleVO = new ArticleVO();
        articleVO.setId(article.getId());
        articleVO.setTitle(article.getTitle());
        articleVO.setSummary(article.getSummary());
        articleVO.setContent(article.getContent());
        articleVO.setCategoryId(article.getCategoryId());
        articleVO.setTop(BooleanUtils.toBoolean(article.getTop()));
        articleVO.setComment(BooleanUtils.toBoolean(article.getComment()));
        articleVO.setDraft(BooleanUtils.toBoolean(article.getStatus()));

        articleVO.setTags(tags);
        return articleVO;
    }

    private static BlogArticleVO buildBlogArticleVO(Article article, List<User> users) {
        BlogArticleVO blogArticleVO = BeanUtils.copyBean(article, BlogArticleVO.class);

        users.stream().filter(e -> e.getId().equals(article.getCreateBy()))
                .findFirst()
                .ifPresent(value -> blogArticleVO.setNickName(value.getNickName()));

        return blogArticleVO;
    }

    private static String getCategoryName(Category category) {
        if (category != null && StringUtils.hasText(category.getName())) {
            return category.getName();
        } else {
            return "未知分类";
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(Article article, List<Long> tagList) {
        articleDAO.insert(article);

        List<ArticleTag> articleTagList = tagList.stream()
                .filter(Objects::nonNull)
                .map(tagId -> new ArticleTag(article.getId(), tagId))
                .collect(Collectors.toList());

        articleTagDAO.insert(articleTagList);
    }

    @Override
    public void delete(long id) {
        articleDAO.delete(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(Article article, List<Long> tagList) {
        articleDAO.updateById(article);

        articleTagDAO.deleteByArticleId(article.getId());

        List<ArticleTag> articleTagList = tagList.stream()
                .map(tagId -> new ArticleTag(article.getId(), tagId))
                .collect(Collectors.toList());

        articleTagDAO.insert(articleTagList);
    }

    @Override
    public ArticleVO get(long id) {
        Article article = articleDAO.select(id);
        if (article == null) {
            return null;
        }

        List<Long> tags = articleTagDAO.selectByArticleId(id).stream().map(ArticleTag::getTagId).collect(Collectors.toList());

        return buildArticleVO(article, tags);
    }

    @Override
    public BlogArticleDetailVO getArticleDetail(long id) {
        Article article = articleDAO.select(id);
        if (article == null) {
            return null;
        }

        BlogArticleDetailVO blogArticleDetailVO = BeanUtils.copyBean(article, BlogArticleDetailVO.class);

        Category category = categoryDAO.select(article.getCategoryId());
        blogArticleDetailVO.setCategoryName(getCategoryName(category));

        User user = userDAO.select(article.getCreateBy());
        if (user != null) {
            blogArticleDetailVO.setNickName(user.getNickName());
        } else {
            blogArticleDetailVO.setNickName("未知作者");
        }

        return blogArticleDetailVO;
    }

    @Override
    public PageVO<ArticleListVO> list(int pageNum, int pageSize, String title, String summary) {
        PageVO<Article> articlePageVO = articleDAO.pageSelect(pageNum, pageSize, title, summary);
        List<ArticleListVO> articleListVOList = BeanUtils.copyBeanList(articlePageVO.getRows(), ArticleListVO.class);
        return articlePageVO.convertType(articleListVOList);
    }

    @Override
    public PageVO<BlogArticleListVO> getBlogList(int pageNum, int pageSize, Long categoryId) {
        PageVO<Article> articlePageVO = articleDAO.pageSelectNormal(pageNum, pageSize, categoryId);

        List<BlogArticleListVO> blogArticleListVOList = BeanUtils.copyBeanList(articlePageVO.getRows(), BlogArticleListVO.class);
        for (BlogArticleListVO blogArticleListVO : blogArticleListVOList) {
            Category category = categoryDAO.select(blogArticleListVO.getCategoryId());
            blogArticleListVO.setCategoryName(getCategoryName(category));
        }

        return articlePageVO.convertType(blogArticleListVOList);
    }

    @Override
    public List<BlogArticleVO> getHotArticleList() {
        List<Article> articles = articleDAO.pageSelectNormalOrderByViewCountDesc(1, 10).getRows();
        return getBlogArticleVOList(articles);
    }

    @Override
    public List<BlogArticleVO> getLatestArticleList() {
        List<Article> articles = articleDAO.pageSelectNormalOrderByCreateTimeDesc(1, 10).getRows();
        return getBlogArticleVOList(articles);
    }

    @Override
    public void increaseViewCount(long id) {
        articleDAO.increaseViewCount(id);
    }

    private List<BlogArticleVO> getBlogArticleVOList(List<Article> articles) {
        List<Long> creatorIds = articles.stream().map(Article::getCreateBy).distinct().collect(Collectors.toList());
        List<User> users = userDAO.select(creatorIds);

        return articles.stream().map(article -> buildBlogArticleVO(article, users)).collect(Collectors.toList());
    }
}
