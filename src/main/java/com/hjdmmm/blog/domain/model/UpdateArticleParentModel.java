package com.hjdmmm.blog.domain.model;

import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
public record UpdateArticleParentModel(
    @Min(1) Long parentId
) {
}
