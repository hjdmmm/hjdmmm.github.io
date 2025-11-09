package com.hjdmmm.blog.domain.vo;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record BlogCommentVO(
    long id,
    long articleId,
    Long pid,
    String content,
    Long toCommentUserId,
    String toCommentUsername,
    Long toCommentId,
    Instant createTime,
    String username,
    List<BlogCommentVO> children
) {
}
