package com.hjdmmm.blog.domain.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;


@NotNull
public record EditTagModel(
        @NotNull
        @Min(1)
        Long id,
        String name,
        String remark
) {
}
