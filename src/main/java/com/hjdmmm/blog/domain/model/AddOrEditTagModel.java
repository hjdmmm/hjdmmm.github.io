package com.hjdmmm.blog.domain.model;

import jakarta.validation.constraints.NotNull;

public record AddOrEditTagModel(
    @NotNull String name,
    @NotNull String remark
) {
}
