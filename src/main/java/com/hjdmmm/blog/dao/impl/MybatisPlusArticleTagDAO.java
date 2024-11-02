package com.hjdmmm.blog.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjdmmm.blog.dao.ArticleTagDAO;
import com.hjdmmm.blog.dao.impl.mapper.ArticleTagMapper;
import com.hjdmmm.blog.domain.entity.ArticleTag;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MybatisPlusArticleTagDAO implements ArticleTagDAO {
    private final MybatisPlusServiceImpl mybatisPlusService;

    public MybatisPlusArticleTagDAO(MybatisPlusServiceImpl mybatisPlusService) {
        this.mybatisPlusService = mybatisPlusService;
    }

    @Override
    public void insert(List<ArticleTag> list) {
        mybatisPlusService.saveBatch(list);
    }

    @Override
    public void deleteByArticleId(long articleId) {
        mybatisPlusService.remove(new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getArticleId, articleId));
    }

    @Override
    public List<ArticleTag> selectByArticleId(long articleId) {
        return mybatisPlusService.list(new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getArticleId, articleId));
    }

    @Component
    public static class MybatisPlusServiceImpl extends ServiceImpl<ArticleTagMapper, ArticleTag> {
    }
}
