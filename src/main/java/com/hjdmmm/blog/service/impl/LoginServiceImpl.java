package com.hjdmmm.blog.service.impl;

import com.hjdmmm.blog.config.LoginConfig;
import com.hjdmmm.blog.dao.UserDAO;
import com.hjdmmm.blog.domain.entity.User;
import com.hjdmmm.blog.domain.vo.LoginVO;
import com.hjdmmm.blog.enums.UserStatusEnum;
import com.hjdmmm.blog.service.HashEncoder;
import com.hjdmmm.blog.service.LoginService;
import com.hjdmmm.blog.service.TokenHandler;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class LoginServiceImpl implements LoginService {
    private final HashEncoder hashEncoder;

    private final LoginConfig loginConfig;

    private final TokenHandler tokenHandler;

    private final UserDAO userDAO;

    @Override
    public LoginVO login(String username, String password, HttpHeaders httpHeaders) throws Exception {
        List<User> users = userDAO.selectByUsernameAndStatus(username, UserStatusEnum.NORMAL.number);
        if (users.isEmpty()) {
            return null;
        }

        User user = users.get(0);

        if (!hashEncoder.verify(password, user.getPassword())) {
            return null;
        }

        String token = tokenHandler.createToken(String.valueOf(user.getId()), httpHeaders);
        return LoginVO.builder()
            .token(token)
            .tokenMaxAgeSeconds(Math.toIntExact(loginConfig.getExpireSeconds().getSeconds()))
            .build();
    }

    @Override
    public void logout() {
        // 使用JWT，因此什么也不做
    }
}
