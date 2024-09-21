package com.hjdmmm.blog.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogArticleVO {
    private Long id;
    private String title;
    private Long viewCount;
    private LocalDateTime createTime;
    private String nickName;
}
