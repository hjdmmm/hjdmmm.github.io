package com.hjdmmm.blog.domain.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record UserDTO(
    long id,
    String username,
    int type,
    int status,
    Instant createTime
) {
}
