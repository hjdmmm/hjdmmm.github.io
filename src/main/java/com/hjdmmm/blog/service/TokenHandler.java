package com.hjdmmm.blog.service;

import com.hjdmmm.blog.exception.BadTokenException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;

import java.util.OptionalLong;

public interface TokenHandler {
    String createToken(String userId, HttpHeaders httpHeaders);

    OptionalLong parseToken(String encryptedToken, HttpServletRequest request) throws BadTokenException;
}
