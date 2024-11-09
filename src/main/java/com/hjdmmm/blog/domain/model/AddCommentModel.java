package com.hjdmmm.blog.domain.model;

import com.hjdmmm.blog.domain.entity.Comment;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

@NotNull
public record AddCommentModel(
        @NotNull
        Integer type,
        Long articleId,
        Long pid,
        String content,
        Long toCommentUserId,
        Long toCommentId
) {

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
