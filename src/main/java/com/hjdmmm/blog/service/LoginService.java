package com.hjdmmm.blog.service;

import com.hjdmmm.blog.domain.vo.LoginVO;
import org.springframework.http.HttpHeaders;

public interface LoginService {
    LoginVO login(String username, String password, HttpHeaders httpHeaders);

    void logout();
}
