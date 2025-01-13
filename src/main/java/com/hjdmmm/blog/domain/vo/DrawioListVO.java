package com.hjdmmm.blog.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DrawioListVO {
    private Long id;
    private String name;
    private LocalDateTime createTime;
    private Long pictureId;
    private String thumbnail;
}
