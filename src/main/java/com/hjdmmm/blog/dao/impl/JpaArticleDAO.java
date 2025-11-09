package com.hjdmmm.blog.dao.impl;

import com.hjdmmm.blog.dao.ArticleDAO;
import com.hjdmmm.blog.dao.impl.repository.ArticleRepository;
import com.hjdmmm.blog.dao.impl.repository.ArticleTagRepository;
import com.hjdmmm.blog.domain.dto.ArticleDTO;
import com.hjdmmm.blog.domain.dto.ArticleDetailDTO;
import com.hjdmmm.blog.domain.entity.Article;
import com.hjdmmm.blog.domain.entity.ArticleTag;
import com.hjdmmm.blog.domain.entity.ArticleTag_;
import com.hjdmmm.blog.domain.entity.Article_;
import com.hjdmmm.blog.domain.vo.PageVO;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Repository
public class JpaArticleDAO implements ArticleDAO {
    private final ArticleRepository articleRepository;

    private final ArticleTagRepository articleTagRepository;

    private static Predicate getTagPredicate(long tagId, Root<Article> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Subquery<Long> articleTagQuery = query.subquery(Long.class);
        Root<ArticleTag> articleTagRoot = articleTagQuery.from(ArticleTag.class);
        articleTagQuery.select(articleTagRoot.get(ArticleTag_.ARTICLE_ID)).where(cb.equal(articleTagRoot.get(ArticleTag_.TAG_ID), tagId));
        return root.get(Article_.id).in(articleTagQuery);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insert(Article article) throws Exception {
        articleRepository.saveAndFlush(article);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(long id) throws Exception {
        articleRepository.deleteById(id);
        articleTagRepository.deleteByArticleId(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTitleById(long id, String title, long modifier) throws Exception {
        articleRepository.updateTitleById(title, modifier, id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateContentById(long id, String content, long modifier) throws Exception {
        articleRepository.updateContentById(content, modifier, id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateParentById(long id, long parentId, long modifier) throws Exception {
        articleRepository.updateParentById(parentId, modifier, id);
    }

    @Override
    public void updateTagIds(long id, List<Long> tagIds, long modifier) throws Exception {
        boolean articleExisted = articleRepository.existsForUpdateById(id);
        if (!articleExisted) {
            return;
        }

        List<ArticleTag> existedArticleTags = articleTagRepository.searchByArticleId(id);

        List<Long> needDeleteArticleTagIds = new ArrayList<>();
        List<Long> existedTagIds = new ArrayList<>();
        for (ArticleTag articleTag : existedArticleTags) {
            if (tagIds.contains(articleTag.getTagId())) {
                existedTagIds.add(articleTag.getTagId());
            } else {
                needDeleteArticleTagIds.add(articleTag.getId());
            }
        }
        List<ArticleTag> needInsertArticleTags = new ArrayList<>();
        for (Long tagId : tagIds) {
            if (!existedTagIds.contains(tagId)) {
                ArticleTag newArticleTag = ArticleTag.builder().articleId(id).tagId(tagId).createBy(modifier).updateBy(modifier).build();
                needInsertArticleTags.add(newArticleTag);
            }
        }
        if (!needDeleteArticleTagIds.isEmpty()) {
            articleTagRepository.deleteAllById(needDeleteArticleTagIds);
        }
        if (!needInsertArticleTags.isEmpty()) {
            articleTagRepository.saveAllAndFlush(needInsertArticleTags);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void increaseViewCount(long id) throws Exception {
        articleRepository.increaseViewCount(id);
    }

    @Override
    public PageVO<ArticleDTO> pageSelectOrderByViewCountDescWithTagId(int status, int pageNum, int pageSize, Long tagId) throws Exception {
        Specification<Article> spec = (Root<Article> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate statusPredicate = cb.equal(root.get(Article_.status), status);
            if (tagId != null) {
                Predicate tagPredicate = getTagPredicate(tagId, root, Objects.requireNonNull(query), cb);
                return cb.and(statusPredicate, tagPredicate);
            }
            return statusPredicate;
        };
        Sort sort = Sort.by(Sort.Direction.DESC, Article_.VIEW_COUNT);
        return pageSelect(pageNum, pageSize, spec, sort);
    }

    @Override
    public PageVO<ArticleDTO> pageSelectOrderByCreateTimeDescWithTagId(int status, int pageNum, int pageSize, Long tagId) throws Exception {
        Specification<Article> spec = (Root<Article> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate statusPredicate = cb.equal(root.get(Article_.status), status);
            if (tagId != null) {
                Predicate tagPredicate = getTagPredicate(tagId, root, Objects.requireNonNull(query), cb);
                return cb.and(statusPredicate, tagPredicate);
            }
            return statusPredicate;
        };
        Sort sort = Sort.by(Sort.Direction.DESC, Article_.CREATE_TIME);
        return pageSelect(pageNum, pageSize, spec, sort);
    }

    @Override
    public PageVO<ArticleDTO> pageSelect(long parentId, int status, int pageNum, int pageSize) throws Exception {
        Specification<Article> spec = (Root<Article> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->
            cb.and(cb.equal(root.get(Article_.parentArticleId), parentId), cb.equal(root.get(Article_.status), status));
        Sort sort = Sort.by(Sort.Direction.DESC, Article_.ID);
        return pageSelect(pageNum, pageSize, spec, sort);
    }

    @Override
    public PageVO<ArticleDTO> pageSelect(int pageNum, int pageSize, String title, String summary) throws Exception {
        Specification<Article> spec = (Root<Article> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(title)) {
                predicates.add(cb.like(root.get(Article_.title), "%" + title + "%"));
            }

            if (StringUtils.hasText(title)) {
                predicates.add(cb.like(root.get(Article_.summary), "%" + summary + "%"));
            }

            if (predicates.isEmpty()) {
                return null;
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Sort sort = Sort.by(Sort.Direction.DESC, Article_.ID);
        return pageSelect(pageNum, pageSize, spec, sort);
    }

    @Override
    public List<ArticleDTO> selectByStatusAndIdsOrParentIds(int status, List<Long> articleIds, List<Long> parentIds) throws Exception {
        Specification<Article> spec = (Root<Article> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            CriteriaBuilder.In<Long> idInClause = cb.in(root.get(Article_.id));
            for (Long articleId : articleIds) {
                idInClause.value(articleId);
            }
            CriteriaBuilder.In<Long> parentIdInClause = cb.in(root.get(Article_.parentArticleId));
            for (Long parentId : parentIds) {
                parentIdInClause.value(parentId);
            }
            Predicate predicate = cb.or(idInClause, parentIdInClause);
            return cb.and(cb.equal(root.get(Article_.status), status), predicate);
        };
        return articleRepository.findBy(spec, fetchableFluentQuery ->
            fetchableFluentQuery.as(ArticleDTO.class).all());
    }

    @Override
    public ArticleDetailDTO selectDetail(long id) throws Exception {
        Article article = articleRepository.findById(id).orElse(null);
        if (article == null) {
            return null;
        }
        List<ArticleTag> articleTags = articleTagRepository.searchByArticleId(id);
        return ArticleDetailDTO.builder().article(article).articleTags(articleTags).build();
    }

    private PageVO<ArticleDTO> pageSelect(int pageNum, int pageSize, Specification<Article> spec, Sort sort) throws Exception {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
        Page<ArticleDTO> pageResult = articleRepository.findBy(spec, fetchableFluentQuery ->
            fetchableFluentQuery.as(ArticleDTO.class).page(pageable));
        return new PageVO<>(pageResult.getContent(), pageResult.getTotalElements());
    }
}
