package com.hjdmmm.blog.dao.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjdmmm.blog.dao.DrawioDAO;
import com.hjdmmm.blog.dao.impl.mapper.DrawioMapper;
import com.hjdmmm.blog.domain.entity.Drawio;
import com.hjdmmm.blog.domain.vo.PageVO;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class MybatisPlusDrawioDAOImpl implements DrawioDAO {
    private final MybatisPlusServiceImpl mybatisPlusService;

    public MybatisPlusDrawioDAOImpl(MybatisPlusServiceImpl mybatisPlusService) {
        this.mybatisPlusService = mybatisPlusService;
    }

    @Override
    public void insert(Drawio drawio) {
        mybatisPlusService.save(drawio);
    }

    @Override
    public void delete(long id) {
        mybatisPlusService.removeById(id);
    }

    @Override
    public void updateName(long id, String name) {
        mybatisPlusService.lambdaUpdate().eq(Drawio::getId, id).set(Drawio::getName, name).update();
    }

    @Override
    public Drawio select(long id) {
        return mybatisPlusService.getById(id);
    }

    @Override
    public PageVO<Drawio> pageSelect(int pageNum, int pageSize, String name) {
        Page<Drawio> page = mybatisPlusService.lambdaQuery()
                .like(StringUtils.hasText(name), Drawio::getName, name)
                .page(new Page<>(pageNum, pageSize));

        return new PageVO<>(page.getRecords(), page.getTotal());
    }

    @Component
    public static class MybatisPlusServiceImpl extends ServiceImpl<DrawioMapper, Drawio> {
    }
}
