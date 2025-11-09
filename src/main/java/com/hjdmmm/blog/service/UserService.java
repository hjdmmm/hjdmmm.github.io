package com.hjdmmm.blog.service;

import com.hjdmmm.blog.domain.dto.UserDTO;
import com.hjdmmm.blog.domain.model.AddOrEditUserModel;
import com.hjdmmm.blog.domain.vo.PageVO;

public interface UserService {
    boolean add(AddOrEditUserModel model, long modifier) throws Exception;

    void delete(long id, long modifier) throws Exception;

    boolean edit(long id, AddOrEditUserModel model, long modifier) throws Exception;

    void changeStatus(long id, int status, long modifier) throws Exception;

    UserDTO get(long id) throws Exception;

    PageVO<UserDTO> list(int pageNum, int pageSize, String username, Integer type, Integer status) throws Exception;
}
