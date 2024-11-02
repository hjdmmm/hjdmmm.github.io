package com.hjdmmm.blog.context.impl;

import com.hjdmmm.blog.annotation.Auth;
import com.hjdmmm.blog.context.PathAuthProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

import java.util.List;

@Component
public class PathAuthProviderImpl implements PathAuthProvider {

    private final DispatcherServlet dispatcherServlet;

    public PathAuthProviderImpl(DispatcherServlet dispatcherServlet) {
        this.dispatcherServlet = dispatcherServlet;
    }

    @Override
    public Auth match(HttpServletRequest request) {
        List<HandlerMapping> handlerMappings = dispatcherServlet.getHandlerMappings();
        if (handlerMappings == null) {
            return null;
        }

        for (HandlerMapping mapping : handlerMappings) {
            HandlerExecutionChain handler;
            try {
                handler = mapping.getHandler(request);
            } catch (Exception e) {
                throw new RuntimeException(String.format("地址 %s 匹配路径 %s 权限时错误", request.getRemoteAddr(), request.getRequestURL()), e);
            }

            if (handler == null) {
                continue;
            }

            Object handlerObject = handler.getHandler();
            if (!(handlerObject instanceof HandlerMethod handlerMethod)) {
                continue;
            }

            Auth methodAuth = AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getMethod(), Auth.class);
            if (methodAuth != null) {
                return methodAuth;
            }

            return AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getBeanType(), Auth.class);
        }

        return null;
    }
}
