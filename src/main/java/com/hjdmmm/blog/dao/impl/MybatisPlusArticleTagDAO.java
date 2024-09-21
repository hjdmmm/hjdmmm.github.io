package com.hjdmmm.blog.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjdmmm.blog.dao.ArticleTagDAO;
import com.hjdmmm.blog.dao.impl.mapper.ArticleTagMapper;
import com.hjdmmm.blog.domain.entity.ArticleTag;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class MybatisPlusArticleTagDAO extends ServiceImpl<ArticleTagMapper, ArticleTag> implements ArticleTagDAO {
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insert(List<ArticleTag> list) {
        saveBatch(list);
    }

    @Override
    public void deleteByArticleId(long articleId) {
        remove(new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getArticleId, articleId));
    }

    @Override
    public List<ArticleTag> selectByArticleId(long articleId) {
        return list(new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getArticleId, articleId));
    }
}
