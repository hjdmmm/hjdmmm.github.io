package com.hjdmmm.blog.domain.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@NotNull
public record EditDrawioNameModel(
        @NotNull
        @Min(1)
        Long id,
        String name
) {
}
