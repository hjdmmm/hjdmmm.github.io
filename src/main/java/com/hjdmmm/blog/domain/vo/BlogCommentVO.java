package com.hjdmmm.blog.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogCommentVO {
    private Long id;
    private Long articleId;
    private Long pid;
    private String content;
    private Long toCommentUserId;
    private String toCommentUserNickName;
    private Long toCommentId;
    private Long createBy;
    private LocalDateTime createTime;
    private String nickName;
    private List<BlogCommentVO> children;
}
