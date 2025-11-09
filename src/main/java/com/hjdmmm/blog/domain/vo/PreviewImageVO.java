package com.hjdmmm.blog.domain.vo;

import lombok.Builder;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

@Builder
public record PreviewImageVO(
    MediaType contentType,
    Resource image
) {
}
