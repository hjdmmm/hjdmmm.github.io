package com.hjdmmm.blog.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;

public interface TokenHandler {
    String createToken(String userId, HttpHeaders httpHeaders) throws Exception;

    Long parseToken(String encryptedToken, HttpServletRequest request) throws Exception;
}
