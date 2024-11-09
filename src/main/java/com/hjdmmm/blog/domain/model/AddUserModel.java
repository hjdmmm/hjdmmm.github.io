package com.hjdmmm.blog.domain.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;


@NotNull
public record AddUserModel(
        @NotNull
        String userName,
        String nickName,
        @NotEmpty
        String password,
        Integer type,
        Integer status
) {
}
