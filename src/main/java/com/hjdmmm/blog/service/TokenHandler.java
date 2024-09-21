package com.hjdmmm.blog.service;

import com.hjdmmm.blog.exception.BadTokenException;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import java.util.OptionalLong;

public interface TokenHandler {
    String createToken(String userId, HttpHeaders httpHeaders);

    OptionalLong parseToken(String encryptedToken, HttpServletRequest request) throws BadTokenException;
}
