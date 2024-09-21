package com.hjdmmm.blog.dao.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjdmmm.blog.dao.CategoryDAO;
import com.hjdmmm.blog.dao.impl.mapper.CategoryMapper;
import com.hjdmmm.blog.domain.entity.Category;
import com.hjdmmm.blog.domain.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Repository
public class MybatisPlusCategoryDAO implements CategoryDAO {
    @Autowired
    private MybatisPlusServiceImpl mybatisPlusService;

    @Override
    public void insert(Category category) {
        mybatisPlusService.save(category);
    }

    @Override
    public void delete(long id) {
        mybatisPlusService.removeById(id);
    }

    @Override
    public void updateById(Category category) {
        mybatisPlusService.updateById(category);
    }

    @Override
    public void update(long id, Integer status) {
        mybatisPlusService.lambdaUpdate().eq(Category::getId, id).set(Category::getStatus, status).update();
    }

    @Override
    public Category select(long id) {
        return mybatisPlusService.getById(id);
    }

    @Override
    public List<Category> selectNormal() {
        return mybatisPlusService.lambdaQuery()
                .eq(Category::getStatus, Category.STATUS_NORMAL)
                .list();
    }

    @Override
    public List<Category> selectNormal(List<Long> ids) {
        return mybatisPlusService
                .lambdaQuery()
                .in(Category::getId, ids)
                .eq(Category::getStatus, Category.STATUS_NORMAL)
                .list();
    }

    @Override
    public PageVO<Category> pageSelect(int pageNum, int pageSize, Integer status, String name) {
        Page<Category> page = mybatisPlusService.lambdaQuery()
                .like(StringUtils.hasText(name), Category::getName, name)
                .eq(Objects.nonNull(status), Category::getStatus, status)
                .page(new Page<>(pageNum, pageSize));

        return new PageVO<>(page.getRecords(), page.getTotal());
    }

    @Component
    public static class MybatisPlusServiceImpl extends ServiceImpl<CategoryMapper, Category> {
    }
}
