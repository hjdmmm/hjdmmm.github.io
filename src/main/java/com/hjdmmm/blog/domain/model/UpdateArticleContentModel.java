package com.hjdmmm.blog.domain.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UpdateArticleContentModel(
    @NotNull String content
) {
}
