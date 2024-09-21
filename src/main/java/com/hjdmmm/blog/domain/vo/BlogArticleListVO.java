package com.hjdmmm.blog.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogArticleListVO {
    private Long id;
    private String title;
    private String summary;
    private Long categoryId;
    private String categoryName;
    private LocalDateTime createTime;
}
