package com.hjdmmm.blog.service;

import com.hjdmmm.blog.domain.entity.Tag;
import com.hjdmmm.blog.domain.vo.LinkListAllVO;
import com.hjdmmm.blog.domain.vo.PageVO;

import java.util.List;

public interface TagService {
    void add(Tag tag);

    void delete(long id);

    void edit(Tag tag);

    Tag get(long id);

    PageVO<Tag> list(int pageNum, int pageSize, String name, String remark);

    List<LinkListAllVO> listAll();
}

