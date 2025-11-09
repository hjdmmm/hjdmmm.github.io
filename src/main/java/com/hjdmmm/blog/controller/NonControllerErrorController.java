package com.hjdmmm.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hjdmmm.blog.domain.ResponseResult;
import com.hjdmmm.blog.enums.UserOpCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.ReflectionUtils;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class NonControllerErrorController extends BasicErrorController {
    private final ObjectMapper objectMapper;

    public NonControllerErrorController(ErrorAttributes errorAttributes, ServerProperties serverProperties, ObjectProvider<ErrorViewResolver> errorViewResolvers, ObjectMapper objectMapper) {
        super(errorAttributes, serverProperties.getError(), errorViewResolvers.orderedStream().collect(Collectors.toList()));
        this.objectMapper = objectMapper;
    }

    @Override
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        Map<String, Object> resultMap = getResultMap(request);
        return ResponseEntity.ok(resultMap);
    }

    public void respondExceptionTip(HttpServletRequest request, HttpServletResponse response, ResponseResult<Void> result) {
        String json;
        try {
            json = objectMapper.writeValueAsString(result);
        } catch (Exception e) {
            log.error("返回异常提示时异常", e);
            return;
        }

        // 跨域
        response.addHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.addHeader("Access-Control-Allow-Credentials", "true");

        response.setStatus(200);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        try (PrintWriter printWriter = response.getWriter()) {
            printWriter.print(json);
            printWriter.flush();
        } catch (Exception e) {
            log.error("返回异常提示时异常", e);
        }
    }

    private Map<String, Object> getResultMap(HttpServletRequest request) {
        HttpStatus status = getStatus(request);
        UserOpCodeEnum userOpCode;
        if (status == HttpStatus.NOT_FOUND) {
            userOpCode = UserOpCodeEnum.NOT_FOUND;
        } else {
            userOpCode = UserOpCodeEnum.BAD_ACCESS;
        }

        ResponseResult<Void> result = ResponseResult.errorResult(userOpCode);

        Map<String, Object> map = new HashMap<>();

        ReflectionUtils.doWithFields(result.getClass(), field -> {
            ReflectionUtils.makeAccessible(field);
            Object fieldValue = ReflectionUtils.getField(field, result);
            map.put(field.getName(), fieldValue);
        });

        return map;
    }
}
