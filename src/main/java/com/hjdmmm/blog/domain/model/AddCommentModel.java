package com.hjdmmm.blog.domain.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddCommentModel(
    @Min(1) long articleId,
    Long pid,
    @NotNull String content
) {
}
