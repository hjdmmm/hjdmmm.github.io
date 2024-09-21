package com.hjdmmm.blog.service.impl;

import com.hjdmmm.blog.dao.ArticleDAO;
import com.hjdmmm.blog.dao.CategoryDAO;
import com.hjdmmm.blog.domain.entity.Article;
import com.hjdmmm.blog.domain.entity.Category;
import com.hjdmmm.blog.domain.vo.BlogCategoryListVO;
import com.hjdmmm.blog.domain.vo.CategoryListAllVO;
import com.hjdmmm.blog.domain.vo.PageVO;
import com.hjdmmm.blog.service.CategoryService;
import com.hjdmmm.blog.util.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final ArticleDAO articleDAO;

    private final CategoryDAO categoryDAO;

    public CategoryServiceImpl(ArticleDAO articleDAO, CategoryDAO categoryDAO) {
        this.articleDAO = articleDAO;
        this.categoryDAO = categoryDAO;
    }

    @Override
    public void add(Category category) {
        categoryDAO.insert(category);
    }

    @Override
    public void delete(long id) {
        categoryDAO.delete(id);
    }

    @Override
    public void edit(Category category) {
        categoryDAO.updateById(category);
    }

    @Override
    public void changeStatus(long id, Integer status) {
        categoryDAO.update(id, status);
    }

    @Override
    public Category get(long id) {
        return categoryDAO.select(id);
    }

    @Override
    public PageVO<CategoryListAllVO> list(int pageNum, int pageSize, Integer status, String name) {
        PageVO<Category> categoryPageVO = categoryDAO.pageSelect(pageNum, pageSize, status, name);
        List<CategoryListAllVO> categoryListAllVOList = BeanUtils.copyBeanList(categoryPageVO.getRows(), CategoryListAllVO.class);
        return categoryPageVO.convertType(categoryListAllVOList);
    }

    @Override
    public List<CategoryListAllVO> listAll() {
        List<Category> categoryList = categoryDAO.selectNormal();
        return BeanUtils.copyBeanList(categoryList, CategoryListAllVO.class);
    }

    @Override
    public List<BlogCategoryListVO> blogListAll() {
        List<Article> articleList = articleDAO.selectNormal();
        List<Long> categoryIdList = articleList.stream().map(Article::getCategoryId).collect(Collectors.toList());
        List<Category> categoryList = categoryDAO.selectNormal(categoryIdList);
        return BeanUtils.copyBeanList(categoryList, BlogCategoryListVO.class);
    }
}
