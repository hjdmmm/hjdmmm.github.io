package com.hjdmmm.blog.service;

import com.hjdmmm.blog.domain.entity.Tag;
import com.hjdmmm.blog.domain.model.AddOrEditTagModel;
import com.hjdmmm.blog.domain.vo.PageVO;

public interface TagService {
    void add(AddOrEditTagModel model, long modifier) throws Exception;

    void delete(long id, long modifier) throws Exception;

    void edit(long id, AddOrEditTagModel model, long modifier) throws Exception;

    Tag get(long id) throws Exception;

    PageVO<Tag> list(int pageNum, int pageSize, String keyword) throws Exception;
}
