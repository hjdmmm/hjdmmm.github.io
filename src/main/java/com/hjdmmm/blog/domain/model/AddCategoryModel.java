package com.hjdmmm.blog.domain.model;

import jakarta.validation.constraints.NotNull;

@NotNull
public record AddCategoryModel(
        String name,
        Long pid,
        String description,
        Integer status
) {
}
