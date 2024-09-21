package com.hjdmmm.blog.dao.impl;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjdmmm.blog.dao.ArticleDAO;
import com.hjdmmm.blog.dao.impl.mapper.ArticleMapper;
import com.hjdmmm.blog.domain.entity.Article;
import com.hjdmmm.blog.domain.vo.PageVO;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Repository
public class MybatisPlusArticleDAO implements ArticleDAO {
    private final MybatisPlusServiceImpl mybatisPlusService;

    public MybatisPlusArticleDAO(MybatisPlusServiceImpl mybatisPlusService) {
        this.mybatisPlusService = mybatisPlusService;
    }

    @Override
    public void insert(Article article) {
        mybatisPlusService.save(article);
    }

    @Override
    public void delete(long id) {
        mybatisPlusService.removeById(id);
    }

    @Override
    public void updateById(Article article) {
        mybatisPlusService.updateById(article);
    }

    @Override
    public void increaseViewCount(long id) {
        mybatisPlusService.lambdaUpdate()
                .eq(Article::getId, id)
                .setSql("view_count = view_count + 1")
                .update();
    }

    @Override
    public Article select(long id) {
        return mybatisPlusService.getById(id);
    }

    @Override
    public List<Article> selectNormal() {
        return mybatisPlusService.lambdaQuery().eq(Article::getStatus, Article.ARTICLE_STATUS_NORMAL).list();
    }

    @Override
    public PageVO<Article> pageSelect(int pageNum, int pageSize, String title, String summary) {
        Page<Article> page = mybatisPlusService.lambdaQuery()
                .like(StringUtils.hasText(title), Article::getTitle, title)
                .like(StringUtils.hasText(summary), Article::getSummary, summary)
                .page(new Page<>(pageNum, pageSize));

        return new PageVO<>(page.getRecords(), page.getTotal());
    }

    @Override
    public PageVO<Article> pageSelectNormal(int pageNum, int pageSize, Long categoryId) {
        return pageSelectNormal(pageNum, pageSize, categoryId, Article::getTop);
    }

    @Override
    public PageVO<Article> pageSelectNormalOrderByViewCountDesc(int pageNum, int pageSize) {
        return pageSelectNormal(pageNum, pageSize, null, Article::getViewCount);
    }

    @Override
    public PageVO<Article> pageSelectNormalOrderByCreateTimeDesc(int pageNum, int pageSize) {
        return pageSelectNormal(pageNum, pageSize, null, Article::getCreateTime);
    }

    private PageVO<Article> pageSelectNormal(int pageNum, int pageSize, Long categoryId, SFunction<Article, Object> orderByDesc) {
        LambdaQueryChainWrapper<Article> wrapper = mybatisPlusService.lambdaQuery()
                .eq(Article::getStatus, Article.ARTICLE_STATUS_NORMAL)
                .eq(Objects.nonNull(categoryId), Article::getCategoryId, categoryId);

        if (orderByDesc != null) {
            wrapper.orderByDesc(orderByDesc);
        }

        Page<Article> page = wrapper.page(new Page<>(pageNum, pageSize));

        return new PageVO<>(page.getRecords(), page.getTotal());
    }

    @Component
    public static class MybatisPlusServiceImpl extends ServiceImpl<ArticleMapper, Article> {
    }
}
