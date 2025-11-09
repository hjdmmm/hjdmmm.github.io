package com.hjdmmm.blog.domain.dto;

import com.hjdmmm.blog.domain.entity.Article;
import com.hjdmmm.blog.domain.entity.ArticleTag;
import lombok.Builder;

import java.util.List;

@Builder
public record ArticleDetailDTO(
    Article article,
    List<ArticleTag> articleTags
) {
}
