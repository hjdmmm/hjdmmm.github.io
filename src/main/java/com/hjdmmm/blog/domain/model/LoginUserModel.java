package com.hjdmmm.blog.domain.model;

import jakarta.validation.constraints.NotNull;

public record LoginUserModel(
    @NotNull String username,
    @NotNull String password
) {
}
