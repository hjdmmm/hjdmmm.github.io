package com.hjdmmm.blog.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleListVO {
    private Long id;
    private String title;
    private String summary;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
