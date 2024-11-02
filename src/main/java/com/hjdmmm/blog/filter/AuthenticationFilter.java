package com.hjdmmm.blog.filter;

import com.hjdmmm.blog.context.UserIdHolder;
import com.hjdmmm.blog.controller.NonControllerErrorController;
import com.hjdmmm.blog.domain.ResponseResult;
import com.hjdmmm.blog.enums.UserOpCodeEnum;
import com.hjdmmm.blog.service.TokenHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.OptionalLong;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class AuthenticationFilter extends OncePerRequestFilter {
    private static final String TOKEN_PREFIX = "token";

    private final TokenHandler tokenHandler;

    private final NonControllerErrorController nonControllerErrorController;

    private final UserIdHolder userIdHolder;

    public AuthenticationFilter(TokenHandler tokenHandler, NonControllerErrorController nonControllerErrorController, UserIdHolder userIdHolder) {
        this.tokenHandler = tokenHandler;
        this.nonControllerErrorController = nonControllerErrorController;
        this.userIdHolder = userIdHolder;
    }

    private static String getTokenFromCookie(Cookie[] cookies) {
        if (cookies == null) {
            return null;
        }

        return Arrays.stream(cookies)
                .filter(cookie -> TOKEN_PREFIX.equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromCookie(request.getCookies());
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 解析获取userId
        OptionalLong userId;
        try {
            userId = tokenHandler.parseToken(token, request);
        } catch (Exception e) {
            log.error("AuthenticationFilter解析token时出错", e);
            ResponseResult<Void> result = ResponseResult.errorResult(UserOpCodeEnum.BAD_TOKEN);
            nonControllerErrorController.respondExceptionTip(request, response, result);
            return;
        }

        if (!userId.isPresent()) {
            log.warn("{} 提供了过时的token", request.getRemoteAddr());
            ResponseResult<Void> result = ResponseResult.errorResult(UserOpCodeEnum.BAD_TOKEN);
            nonControllerErrorController.respondExceptionTip(request, response, result);
            return;
        }

        userIdHolder.set(userId.getAsLong());

        filterChain.doFilter(request, response);

        userIdHolder.clear();
    }
}
