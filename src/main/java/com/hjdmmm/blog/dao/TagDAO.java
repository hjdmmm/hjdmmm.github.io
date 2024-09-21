package com.hjdmmm.blog.dao;

import com.hjdmmm.blog.domain.entity.Tag;
import com.hjdmmm.blog.domain.vo.PageVO;

import java.util.List;

public interface TagDAO {
    void insert(Tag tag);

    void delete(long id);

    void updateById(Tag tag);

    Tag select(long id);

    List<Tag> selectAll();

    PageVO<Tag> pageSelect(int pageNum, int pageSize, String name, String remark);
}
