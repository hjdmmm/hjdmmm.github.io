package com.hjdmmm.blog.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleVO {
    private Long id;
    private String title;
    private String summary;
    private String content;
    private Long categoryId;
    private boolean top;
    private boolean comment;
    private boolean draft;
    private List<Long> tags;
}
