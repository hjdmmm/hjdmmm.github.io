package com.hjdmmm.blog.service;

import com.hjdmmm.blog.domain.entity.Comment;
import com.hjdmmm.blog.domain.vo.BlogCommentVO;
import com.hjdmmm.blog.domain.vo.PageVO;
import com.hjdmmm.blog.exception.IllegalArticleCommentException;

public interface CommentService {
    void add(Comment comment) throws IllegalArticleCommentException;

    PageVO<BlogCommentVO> getArticleComments(int pageNum, int pageSize, long articleId);

    boolean canComment(String ip);
}

