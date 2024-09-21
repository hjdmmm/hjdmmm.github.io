package com.hjdmmm.blog.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreviewImageVO {
    private MediaType contentType;
    private Resource image;
}
