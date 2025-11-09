package com.hjdmmm.blog.domain.vo;

import lombok.Builder;

@Builder
public record TagVO(
    long id,
    String name
) {
}
