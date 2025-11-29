package com.hjdmmm.blog.domain.vo;

import lombok.Builder;

import java.util.List;

@Builder
public record FeedArticleInfoVO(
    List<Long> hotArticleIds,
    List<Long> latestArticleIds,
    PageVO<Long> categoryArticleIdPage
) {
}
