package com.hjdmmm.blog.context;

import com.hjdmmm.blog.annotation.Auth;

import javax.servlet.http.HttpServletRequest;

public interface PathAuthProvider {
    Auth match(HttpServletRequest request);
}
