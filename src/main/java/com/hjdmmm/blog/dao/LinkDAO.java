package com.hjdmmm.blog.dao;

import com.hjdmmm.blog.domain.entity.Link;
import com.hjdmmm.blog.domain.vo.PageVO;

import java.util.List;

public interface LinkDAO {
    void insert(Link link);

    void delete(long id);

    void updateById(Link link);

    void update(long id, Integer status);

    Link select(long id);

    List<Link> selectNormal();

    PageVO<Link> pageSelect(int pageNum, int pageSize, String name, Integer status);
}
