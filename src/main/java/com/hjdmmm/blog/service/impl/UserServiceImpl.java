package com.hjdmmm.blog.service.impl;

import com.hjdmmm.blog.dao.UserDAO;
import com.hjdmmm.blog.domain.entity.User;
import com.hjdmmm.blog.domain.vo.BlogUserInfoVO;
import com.hjdmmm.blog.domain.vo.PageVO;
import com.hjdmmm.blog.domain.vo.UserGetVO;
import com.hjdmmm.blog.domain.vo.UserListVO;
import com.hjdmmm.blog.exception.UserNameExistException;
import com.hjdmmm.blog.service.HashEncoder;
import com.hjdmmm.blog.service.UserService;
import com.hjdmmm.blog.util.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final HashEncoder hashEncoder;

    private final UserDAO userDAO;

    public UserServiceImpl(HashEncoder hashEncoder, UserDAO userDAO) {
        this.hashEncoder = hashEncoder;
        this.userDAO = userDAO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(String userName, String nickName, String password, Integer type, Integer status) throws UserNameExistException {
        boolean userNameExist = userDAO.countByUserName(userName) > 0L;
        if (userNameExist) {
            throw new UserNameExistException();
        }

        User user = new User();
        user.setUserName(userName);
        user.setNickName(nickName);
        user.setPassword(hashEncoder.encode(password));
        user.setType(type);
        user.setStatus(status);
        userDAO.insert(user);
    }

    @Override
    public void delete(long id) {
        userDAO.delete(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(long id, String userName, String nickName, String password, Integer type, Integer status) throws UserNameExistException {
        boolean userNameExist = userDAO.countByUserName(userName) > 0L;
        if (userNameExist) {
            throw new UserNameExistException();
        }

        User user = new User();
        user.setId(id);
        user.setUserName(userName);
        user.setNickName(nickName);
        user.setPassword(hashEncoder.encode(password));
        user.setType(type);
        user.setStatus(status);
        userDAO.updateById(user);
    }

    @Override
    public void changeStatus(long id, Integer status) {
        userDAO.update(id, status);
    }

    @Override
    public UserGetVO get(long id) {
        User user = userDAO.select(id);
        if (user == null) {
            return null;
        }

        return BeanUtils.copyBean(user, UserGetVO.class);
    }

    @Override
    public PageVO<UserListVO> list(int pageNum, int pageSize, String userName, Integer type, Integer status) {
        PageVO<User> userPageVO = userDAO.pageSelect(pageNum, pageSize, userName, type, status);

        List<UserListVO> userListVOList = BeanUtils.copyBeanList(userPageVO.getRows(), UserListVO.class);
        return userPageVO.convertType(userListVOList);
    }

    @Override
    public BlogUserInfoVO getUserInfo(long id) {
        User user = userDAO.select(id);
        if (user == null) {
            return null;
        }

        return BeanUtils.copyBean(user, BlogUserInfoVO.class);
    }
}

