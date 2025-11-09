package com.hjdmmm.blog.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.MediaType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AttachmentConstants {
    public static final MediaType IMAGE_MEDIA_TYPE = MediaType.parseMediaType("image/*");
}
