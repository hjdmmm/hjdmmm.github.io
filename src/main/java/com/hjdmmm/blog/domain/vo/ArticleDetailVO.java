package com.hjdmmm.blog.domain.vo;

import lombok.Builder;

import java.util.List;

@Builder
public record ArticleDetailVO(
    long id,
    String title,
    String content,
    long viewCount,
    long createTime,
    long updateTime,
    String author,
    boolean canEdit,
    List<TagVO> tags
) {
}
