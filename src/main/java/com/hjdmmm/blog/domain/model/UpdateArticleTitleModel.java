package com.hjdmmm.blog.domain.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UpdateArticleTitleModel(
    @NotNull String title
) {
}
