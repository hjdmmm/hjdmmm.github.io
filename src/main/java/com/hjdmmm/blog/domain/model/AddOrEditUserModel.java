package com.hjdmmm.blog.domain.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AddOrEditUserModel(
    @NotNull String username,
    @NotEmpty String password,
    @NotNull Integer type,
    @NotNull Integer status
) {
}
