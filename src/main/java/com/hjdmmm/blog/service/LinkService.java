package com.hjdmmm.blog.service;

import com.hjdmmm.blog.domain.entity.Link;
import com.hjdmmm.blog.domain.vo.BlogLinkListVO;
import com.hjdmmm.blog.domain.vo.PageVO;

import java.util.List;

public interface LinkService {
    void add(Link link);

    void delete(long id);

    void edit(Link link);

    void changeStatus(long id, Integer status);

    Link get(long id);

    PageVO<Link> list(int pageNum, int pageSize, String name, Integer status);

    List<BlogLinkListVO> blogListAll();
}
