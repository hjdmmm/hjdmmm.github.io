package com.hjdmmm.blog.dao;

import com.hjdmmm.blog.domain.entity.Article;
import com.hjdmmm.blog.domain.vo.PageVO;

import java.util.List;

public interface ArticleDAO {
    void insert(Article article);

    void delete(long id);

    void updateById(Article article);

    void increaseViewCount(long id);

    Article select(long id);

    List<Article> selectNormal();

    PageVO<Article> pageSelect(int pageNum, int pageSize, String title, String summary);

    PageVO<Article> pageSelectNormal(int pageNum, int pageSize, Long categoryId);

    PageVO<Article> pageSelectNormalOrderByViewCountDesc(int pageNum, int pageSize);

    PageVO<Article> pageSelectNormalOrderByCreateTimeDesc(int pageNum, int pageSize);
}
