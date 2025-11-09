package com.hjdmmm.blog.dao;

import com.hjdmmm.blog.domain.entity.Tag;
import com.hjdmmm.blog.domain.vo.PageVO;

import java.util.List;

public interface TagDAO {
    void insert(Tag tag) throws Exception;

    void delete(long id) throws Exception;

    void updateById(Tag tag) throws Exception;

    Tag select(long id) throws Exception;

    List<Tag> selectAll(List<Long> tagIds) throws Exception;

    PageVO<Tag> pageSelect(int pageNum, int pageSize, String nameLike, String remarkLike) throws Exception;
}
