package com.hjdmmm.blog.domain.vo;

import lombok.Builder;

import java.util.List;

@Builder
public record ArticleNodeVO(
    long id,
    String title,
    Long parentId,
    boolean canCreateChild,
    boolean canDelete,
    boolean canChangeParent,
    List<Long> children
) {
}
