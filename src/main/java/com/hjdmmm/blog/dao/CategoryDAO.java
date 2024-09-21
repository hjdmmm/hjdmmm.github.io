package com.hjdmmm.blog.dao;

import com.hjdmmm.blog.domain.entity.Category;
import com.hjdmmm.blog.domain.vo.PageVO;

import java.util.List;

public interface CategoryDAO {
    void insert(Category category);

    void delete(long id);

    void updateById(Category category);

    void update(long id, Integer status);

    Category select(long id);

    List<Category> selectNormal();

    List<Category> selectNormal(List<Long> ids);

    PageVO<Category> pageSelect(int pageNum, int pageSize, Integer status, String name);
}
