package com.hjdmmm.blog.service;

import com.hjdmmm.blog.domain.entity.Article;
import com.hjdmmm.blog.domain.vo.*;

import java.util.List;

public interface ArticleService {
    void add(Article article, List<Long> tagList);

    void delete(long id);

    void edit(Article article, List<Long> tagList);

    ArticleVO get(long id);

    PageVO<ArticleListVO> list(int pageNum, int pageSize, String title, String summary);

    BlogArticleDetailVO getArticleDetail(long id);

    PageVO<BlogArticleListVO> getBlogList(int pageNum, int pageSize, Long categoryId);

    List<BlogArticleVO> getHotArticleList();

    List<BlogArticleVO> getLatestArticleList();

    void increaseViewCount(long id);
}
