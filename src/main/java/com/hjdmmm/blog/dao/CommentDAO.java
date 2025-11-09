package com.hjdmmm.blog.dao;

import com.hjdmmm.blog.domain.entity.Comment;
import com.hjdmmm.blog.domain.vo.PageVO;

import java.util.List;

public interface CommentDAO {
    void insert(Comment comment) throws Exception;

    void delete(long id) throws Exception;

    Comment select(long id) throws Exception;

    List<Comment> selectByPids(List<Long> pids) throws Exception;

    PageVO<Comment> pageSelectRootByArticleId(int pageNum, int pageSize, long articleId) throws Exception;
}
