package com.hjdmmm.blog.dao;

import com.hjdmmm.blog.domain.dto.UserDTO;
import com.hjdmmm.blog.domain.entity.User;
import com.hjdmmm.blog.domain.vo.PageVO;

import java.util.List;

public interface UserDAO {
    void insert(User user) throws Exception;

    void delete(long id) throws Exception;

    void updateById(User user) throws Exception;

    UserDTO select(long id) throws Exception;

    List<UserDTO> selectAll(List<Long> ids) throws Exception;

    PageVO<UserDTO> pageSelect(int pageNum, int pageSize, String username, Integer type, Integer status) throws Exception;

    long countByUsernameForUpdate(String username) throws Exception;

    List<User> selectByUsernameAndStatus(String username, int status) throws Exception;
}
