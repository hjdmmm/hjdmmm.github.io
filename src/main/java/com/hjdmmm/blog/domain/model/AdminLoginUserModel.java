package com.hjdmmm.blog.domain.model;

import jakarta.validation.constraints.NotNull;


@NotNull
public record AdminLoginUserModel(
        @NotNull
        String userName,
        @NotNull
        String password
) {
}
