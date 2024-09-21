package com.hjdmmm.blog.dao;

import com.hjdmmm.blog.domain.entity.ArticleTag;

import java.util.List;

public interface ArticleTagDAO {
    void insert(List<ArticleTag> list);

    void deleteByArticleId(long articleId);

    List<ArticleTag> selectByArticleId(long articleId);
}
