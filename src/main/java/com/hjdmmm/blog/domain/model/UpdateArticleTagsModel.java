package com.hjdmmm.blog.domain.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record UpdateArticleTagsModel(
    @Valid List<@NotNull @Min(1) Long> tagIds
) {
}
