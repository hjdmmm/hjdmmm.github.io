package com.hjdmmm.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hjdmmm.blog.config.LoginConfig;
import com.hjdmmm.blog.dao.impl.mapper.UserMapper;
import com.hjdmmm.blog.domain.entity.User;
import com.hjdmmm.blog.domain.vo.LoginVO;
import com.hjdmmm.blog.enums.UserOpCodeEnum;
import com.hjdmmm.blog.exception.UserOpException;
import com.hjdmmm.blog.service.HashEncoder;
import com.hjdmmm.blog.service.LoginService;
import com.hjdmmm.blog.service.TokenHandler;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {
    private final HashEncoder hashEncoder;

    private final LoginConfig loginConfig;

    private final TokenHandler tokenHandler;

    private final UserMapper userMapper;

    public LoginServiceImpl(HashEncoder hashEncoder, LoginConfig loginConfig, TokenHandler tokenHandler, UserMapper userMapper) {
        this.hashEncoder = hashEncoder;
        this.loginConfig = loginConfig;
        this.tokenHandler = tokenHandler;
        this.userMapper = userMapper;
    }

    @Override
    public LoginVO login(String username, String password, HttpHeaders httpHeaders) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>().eq(User::getUserName, username);
        User user;
        try {
            user = userMapper.selectOne(queryWrapper);
        } catch (TooManyResultsException exception) {
            throw new UserOpException(UserOpCodeEnum.LOGIN_ERROR);
        }

        if (user == null) {
            throw new UserOpException(UserOpCodeEnum.LOGIN_ERROR);
        }

        if (!hashEncoder.verify(password, user.getPassword())) {
            throw new UserOpException(UserOpCodeEnum.LOGIN_ERROR);
        }

        Long userId = user.getId();
        String token = tokenHandler.createToken(String.valueOf(userId), httpHeaders);
        return new LoginVO(token, (int) loginConfig.getLoginExpireTimeout().getSeconds());
    }

    @Override
    public void logout() {
        // 使用JWT，因此什么也不做
    }
}
