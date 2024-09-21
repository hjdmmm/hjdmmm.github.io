package com.hjdmmm.blog.filter;

import com.hjdmmm.blog.annotation.Auth;
import com.hjdmmm.blog.context.PathAuthProvider;
import com.hjdmmm.blog.context.UserIdHolder;
import com.hjdmmm.blog.controller.NonControllerErrorController;
import com.hjdmmm.blog.domain.ResponseResult;
import com.hjdmmm.blog.enums.AuthTypeEnum;
import com.hjdmmm.blog.enums.UserOpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@Slf4j
public class AuthorizationFilter extends OncePerRequestFilter {
    private final PathAuthProvider pathAuthProvider;

    private final NonControllerErrorController nonControllerErrorController;

    private final UserIdHolder userIdHolder;

    public AuthorizationFilter(PathAuthProvider pathAuthProvider, NonControllerErrorController nonControllerErrorController, UserIdHolder userIdHolder) {
        this.pathAuthProvider = pathAuthProvider;
        this.nonControllerErrorController = nonControllerErrorController;
        this.userIdHolder = userIdHolder;
    }

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
