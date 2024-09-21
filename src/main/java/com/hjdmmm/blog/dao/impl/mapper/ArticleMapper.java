package com.hjdmmm.blog.dao.impl.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hjdmmm.blog.domain.entity.Article;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
}
