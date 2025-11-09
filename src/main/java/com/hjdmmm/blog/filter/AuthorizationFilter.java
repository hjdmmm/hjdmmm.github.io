package com.hjdmmm.blog.filter;

import com.hjdmmm.blog.annotation.Auth;
import com.hjdmmm.blog.context.PathAuthProvider;
import com.hjdmmm.blog.context.UserIdHolder;
import com.hjdmmm.blog.controller.NonControllerErrorController;
import com.hjdmmm.blog.domain.ResponseResult;
import com.hjdmmm.blog.enums.AuthTypeEnum;
import com.hjdmmm.blog.enums.UserOpCodeEnum;
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
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@Slf4j
public class AuthorizationFilter extends OncePerRequestFilter {
    private final PathAuthProvider pathAuthProvider;

    private final NonControllerErrorController nonControllerErrorController;

    private final UserIdHolder userIdHolder;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Auth auth = pathAuthProvider.match(request);
        AuthTypeEnum authType = AuthTypeEnum.PERMIT_ALL;
        if (auth != null) {
            authType = auth.type();
        }

        switch (authType) {
            case ANONYMOUS:
                if (userIdHolder.exist()) {
                    log.warn("{} 已登录，无法访问匿名接口", request.getRemoteAddr());
                    ResponseResult<Void> result = ResponseResult.errorResult(UserOpCodeEnum.NEED_ANONYMOUS);
                    nonControllerErrorController.respondExceptionTip(request, response, result);
                    return;
                }
                break;
            case AUTHENTICATED:
                if (!userIdHolder.exist()) {
                    log.warn("{} 未登录，无法访问需要登录的接口", request.getRemoteAddr());
                    ResponseResult<Void> result = ResponseResult.errorResult(UserOpCodeEnum.NEED_LOGIN);
                    nonControllerErrorController.respondExceptionTip(request, response, result);
                    return;
                }
                break;
            case PERMIT_ALL:
                break;
            default:
                throw new IllegalArgumentException("未知认证类型");
        }

        filterChain.doFilter(request, response);
    }
}
