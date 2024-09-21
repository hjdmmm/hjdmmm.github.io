package com.hjdmmm.blog.dao.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjdmmm.blog.dao.UserDAO;
import com.hjdmmm.blog.dao.impl.mapper.UserMapper;
import com.hjdmmm.blog.domain.entity.User;
import com.hjdmmm.blog.domain.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Repository
public class MybatisPlusUserDAO implements UserDAO {
    @Autowired
    private MybatisPlusServiceImpl mybatisPlusService;

    @Override
    public void insert(User user) {
        mybatisPlusService.save(user);
    }

    @Override
    public void delete(long id) {
        mybatisPlusService.removeById(id);
    }

    @Override
    public void updateById(User user) {
        mybatisPlusService.updateById(user);
    }

    @Override
    public void update(long id, Integer status) {
        mybatisPlusService.lambdaUpdate().eq(User::getId, id).set(User::getStatus, status).update();
    }

    @Override
    public User select(long id) {
        return mybatisPlusService.getById(id);
    }

    @Override
    public List<User> select(Collection<Long> ids) {
        return mybatisPlusService.listByIds(ids);
    }

    @Override
    public PageVO<User> pageSelect(int pageNum, int pageSize, String userName, Integer type, Integer status) {
        Page<User> page = mybatisPlusService.lambdaQuery()
                .like(StringUtils.hasText(userName), User::getUserName, userName)
                .eq(Objects.nonNull(type), User::getType, type)
                .eq(Objects.nonNull(status), User::getStatus, status)
                .page(new Page<>(pageNum, pageSize));

        return new PageVO<>(page.getRecords(), page.getTotal());
    }

    @Override
    public long countByUserName(String userName) {
        return mybatisPlusService.lambdaQuery()
                .eq(User::getUserName, userName)
                .count();
    }

    @Component
    public static class MybatisPlusServiceImpl extends ServiceImpl<UserMapper, User> {
    }
}
