package com.hjdmmm.blog.domain.model;

import com.hjdmmm.blog.domain.entity.Comment;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@NotNull
public class AddCommentModel {
    @NotNull
    private Integer type;
    private Long articleId;
    private Long pid;
    private String content;
    private Long toCommentUserId;
    private Long toCommentId;

    public Comment toComment() {
        Comment comment = new Comment();
        comment.setType(type);
        comment.setArticleId(articleId);
        comment.setPid(Optional.ofNullable(pid).orElse(Comment.ROOT_PID));
        comment.setContent(content);
        comment.setToCommentUserId(toCommentUserId);
        comment.setToCommentId(toCommentId);
        return comment;
    }
}
