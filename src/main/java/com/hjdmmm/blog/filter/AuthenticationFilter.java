package com.hjdmmm.blog.filter;

import com.hjdmmm.blog.context.UserIdHolder;
import com.hjdmmm.blog.controller.NonControllerErrorController;
import com.hjdmmm.blog.domain.ResponseResult;
import com.hjdmmm.blog.enums.UserOpCodeEnum;
import com.hjdmmm.blog.service.TokenHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@AllArgsConstructor
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class AuthenticationFilter extends OncePerRequestFilter {
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final TokenHandler tokenHandler;

    private final NonControllerErrorController nonControllerErrorController;

    private final UserIdHolder userIdHolder;

    private static String getTokenFromHeader(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            return authorizationHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromHeader(request);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Long userId;
        try {
            userId = tokenHandler.parseToken(token, request);
        } catch (Exception e) {
            log.error("AuthenticationFilter解析token时出错", e);
            ResponseResult<Void> result = ResponseResult.errorResult(UserOpCodeEnum.BAD_TOKEN);
            nonControllerErrorController.respondExceptionTip(request, response, result);
            return;
        }

        if (userId == null) {
            log.warn("{} 提供了过时的token", request.getRemoteAddr());
            ResponseResult<Void> result = ResponseResult.errorResult(UserOpCodeEnum.BAD_TOKEN);
            nonControllerErrorController.respondExceptionTip(request, response, result);
            return;
        }

        try (UserIdHolder.Session ignored = userIdHolder.newSession(userId)) {
            filterChain.doFilter(request, response);
        }
    }
}
