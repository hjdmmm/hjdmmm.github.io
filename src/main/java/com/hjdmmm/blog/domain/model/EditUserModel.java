package com.hjdmmm.blog.domain.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@NotNull
public record EditUserModel(
        @NotNull
        @Min(1)
        Long id,
        String userName,
        String nickName,
        String password,
        Integer type,
        Integer status
) {
}
