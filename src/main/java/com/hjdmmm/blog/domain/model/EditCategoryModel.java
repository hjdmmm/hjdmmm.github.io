package com.hjdmmm.blog.domain.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;


@NotNull
public record EditCategoryModel(
        @NotNull
        @Min(1)
        Long id,
        String name,
        Long pid,
        String description,
        Integer status
) {
}
