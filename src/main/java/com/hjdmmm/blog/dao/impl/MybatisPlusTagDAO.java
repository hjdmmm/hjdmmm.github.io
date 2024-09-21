package com.hjdmmm.blog.dao.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjdmmm.blog.dao.TagDAO;
import com.hjdmmm.blog.dao.impl.mapper.TagMapper;
import com.hjdmmm.blog.domain.entity.Tag;
import com.hjdmmm.blog.domain.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
public class MybatisPlusTagDAO implements TagDAO {
    @Autowired
    private MybatisPlusServiceImpl mybatisPlusService;

    @Override
    public void insert(Tag tag) {
        mybatisPlusService.save(tag);
    }

    @Override
    public void delete(long id) {
        mybatisPlusService.removeById(id);
    }

    @Override
    public void updateById(Tag tag) {
        mybatisPlusService.updateById(tag);
    }

    @Override
    public Tag select(long id) {
        return mybatisPlusService.getById(id);
    }

    @Override
    public List<Tag> selectAll() {
        return mybatisPlusService.list();
    }

    @Override
    public PageVO<Tag> pageSelect(int pageNum, int pageSize, String name, String remark) {
        Page<Tag> page = mybatisPlusService.lambdaQuery()
                .like(StringUtils.hasText(name), Tag::getName, name)
                .like(StringUtils.hasText(remark), Tag::getRemark, remark)
                .page(new Page<>(pageNum, pageSize));

        return new PageVO<>(page.getRecords(), page.getTotal());
    }

    @Component
    public static class MybatisPlusServiceImpl extends ServiceImpl<TagMapper, Tag> {
    }
}
