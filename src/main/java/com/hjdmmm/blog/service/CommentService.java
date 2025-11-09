package com.hjdmmm.blog.service;

import com.hjdmmm.blog.domain.model.AddCommentModel;
import com.hjdmmm.blog.domain.vo.BlogCommentVO;
import com.hjdmmm.blog.domain.vo.PageVO;

public interface CommentService {
    void add(AddCommentModel model, long modifier) throws Exception;

    PageVO<BlogCommentVO> getArticleComments(int pageNum, int pageSize, long articleId) throws Exception;

    boolean canComment(String ip) throws Exception;
}

