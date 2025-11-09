package com.hjdmmm.blog.domain.model;

import jakarta.validation.constraints.NotNull;

public record ChangeStatusModel(
    @NotNull Integer status
) {
}
