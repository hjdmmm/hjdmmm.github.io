package com.hjdmmm.blog.dao;

import com.hjdmmm.blog.domain.entity.User;
import com.hjdmmm.blog.domain.vo.PageVO;

import java.util.Collection;
import java.util.List;

public interface UserDAO {
    void insert(User user);

    void delete(long id);

    void updateById(User user);

    void update(long id, Integer status);

    User select(long id);

    List<User> select(Collection<Long> ids);

    PageVO<User> pageSelect(int pageNum, int pageSize, String userName, Integer type, Integer status);

    long countByUserNameForUpdate(String userName);
}
