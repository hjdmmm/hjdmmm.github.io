package com.hjdmmm.blog.domain.dto;

import lombok.Builder;

@Builder
public record ArticleDTO(
    long id,
    long parentArticleId,
    String title
) {
}
