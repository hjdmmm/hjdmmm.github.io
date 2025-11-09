package com.hjdmmm.blog.domain.vo;

import lombok.Builder;

import java.util.List;

@Builder
public record PageVO<T>(
    List<T> rows,
    long total
) {
}
