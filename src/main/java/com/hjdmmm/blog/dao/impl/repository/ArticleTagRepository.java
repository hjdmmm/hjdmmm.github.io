package com.hjdmmm.blog.dao.impl.repository;

import com.hjdmmm.blog.domain.entity.ArticleTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ArticleTagRepository extends JpaRepository<ArticleTag, Long>, JpaSpecificationExecutor<ArticleTag> {
    void deleteByArticleId(Long articleId) throws Exception;

    List<ArticleTag> searchByArticleId(Long articleId) throws Exception;
}