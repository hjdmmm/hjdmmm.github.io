package com.hjdmmm.blog.domain.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AddArticleModel(
    @NotNull String title,
    boolean draft,
    @Min(1) Long parentArticleId
) {
}
