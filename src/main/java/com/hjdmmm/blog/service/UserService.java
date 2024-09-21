package com.hjdmmm.blog.service;

import com.hjdmmm.blog.domain.vo.BlogUserInfoVO;
import com.hjdmmm.blog.domain.vo.PageVO;
import com.hjdmmm.blog.domain.vo.UserGetVO;
import com.hjdmmm.blog.domain.vo.UserListVO;
import com.hjdmmm.blog.exception.UserNameExistException;

public interface UserService {

    void add(String userName, String nickName, String password, Integer type, Integer status) throws UserNameExistException;

    void delete(long id);

    void edit(long id, String userName, String nickName, String password, Integer type, Integer status) throws UserNameExistException;

    void changeStatus(long id, Integer status);

    UserGetVO get(long id);

    PageVO<UserListVO> list(int pageNum, int pageSize, String userName, Integer type, Integer status);

    BlogUserInfoVO getUserInfo(long id);
}
