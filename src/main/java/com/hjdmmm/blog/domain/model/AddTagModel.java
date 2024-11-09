package com.hjdmmm.blog.domain.model;

import jakarta.validation.constraints.NotNull;


@NotNull
public record AddTagModel(
        String name,
        String remark
) {
}
