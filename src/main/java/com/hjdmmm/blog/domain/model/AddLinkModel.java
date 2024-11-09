package com.hjdmmm.blog.domain.model;

import jakarta.validation.constraints.NotNull;

@NotNull
public record AddLinkModel(
        String name,
        String description,
        String url,
        Integer status
) {
}
