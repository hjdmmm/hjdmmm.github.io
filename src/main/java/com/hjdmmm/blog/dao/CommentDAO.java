package com.hjdmmm.blog.dao;

import com.hjdmmm.blog.domain.entity.Comment;
import com.hjdmmm.blog.domain.vo.PageVO;

import java.util.Collection;
import java.util.List;

public interface CommentDAO {
    void insert(Comment comment);

    void delete(long id);

    List<Comment> selectByPids(Collection<Long> pids);

    PageVO<Comment> pageSelectRootByArticleId(int pageNum, int pageSize, long articleId);
}
