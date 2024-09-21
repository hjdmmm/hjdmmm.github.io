package com.hjdmmm.blog.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageListVO {
    private Long id;
    private String originalName;
    private String mimeType;
    private String url;
    private LocalDateTime createTime;
    private String thumbnail;
}

