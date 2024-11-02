package com.hjdmmm.blog.dao.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjdmmm.blog.dao.LinkDAO;
import com.hjdmmm.blog.dao.impl.mapper.LinkMapper;
import com.hjdmmm.blog.domain.entity.Link;
import com.hjdmmm.blog.domain.vo.PageVO;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Repository
public class MybatisPlusLinkDAO implements LinkDAO {
    private final MybatisPlusServiceImpl mybatisPlusService;

    public MybatisPlusLinkDAO(MybatisPlusServiceImpl mybatisPlusService) {
        this.mybatisPlusService = mybatisPlusService;
    }

    @Override
    public void insert(Link link) {
        mybatisPlusService.save(link);
    }

    @Override
    public void delete(long id) {
        mybatisPlusService.removeById(id);
    }

    @Override
    public void updateById(Link link) {
        mybatisPlusService.updateById(link);
    }

    @Override
    public void update(long id, Integer status) {
        mybatisPlusService.lambdaUpdate().eq(Link::getId, id).set(Link::getStatus, status).update();
    }

    @Override
    public Link select(long id) {
        return mybatisPlusService.getById(id);
    }

    @Override
    public List<Link> selectNormal() {
        return mybatisPlusService.lambdaQuery()
                .eq(Link::getStatus, Link.LINK_STATUS_NORMAL)
                .list();
    }

    @Override
    public PageVO<Link> pageSelect(int pageNum, int pageSize, String name, Integer status) {
        Page<Link> page = mybatisPlusService.lambdaQuery()
                .like(StringUtils.hasText(name), Link::getName, name)
                .eq(Objects.nonNull(status), Link::getStatus, status)
                .page(new Page<>(pageNum, pageSize));

        return new PageVO<>(page.getRecords(), page.getTotal());
    }

    @Component
    public static class MybatisPlusServiceImpl extends ServiceImpl<LinkMapper, Link> {
    }
}
