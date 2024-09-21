package com.hjdmmm.blog.service;

import com.hjdmmm.blog.domain.entity.Category;
import com.hjdmmm.blog.domain.vo.BlogCategoryListVO;
import com.hjdmmm.blog.domain.vo.CategoryListAllVO;
import com.hjdmmm.blog.domain.vo.PageVO;

import java.util.List;

public interface CategoryService {
    void add(Category category);

    void delete(long id);

    void edit(Category category);

    void changeStatus(long id, Integer status);

    Category get(long id);

    PageVO<CategoryListAllVO> list(int pageNum, int pageSize, Integer status, String name);

    List<CategoryListAllVO> listAll();

    List<BlogCategoryListVO> blogListAll();
}
