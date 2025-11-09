package com.hjdmmm.blog.domain.vo;

import lombok.Builder;

@Builder
public record LoginVO(
    String token,
    int tokenMaxAgeSeconds
) {
}
