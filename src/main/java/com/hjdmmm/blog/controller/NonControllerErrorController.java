package com.hjdmmm.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hjdmmm.blog.domain.ResponseResult;
import com.hjdmmm.blog.enums.UserOpCodeEnum;
import com.hjdmmm.blog.util.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
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
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    public void respondExceptionTip(HttpServletRequest request, HttpServletResponse response, ResponseResult<Void> result) {
        try {
            // 跨域
            response.addHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
            response.addHeader("Access-Control-Allow-Credentials", "true");

            response.setStatus(200);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            PrintWriter printWriter = response.getWriter();
            printWriter.print(objectMapper.writeValueAsString(result));
            printWriter.flush();
            printWriter.close();
        } catch (IOException e) {
            log.error("返回异常提示时异常", e);
        }
    }

    private Map<String, Object> getResultMap(HttpServletRequest request) {
        HttpStatus status = getStatus(request);
        ResponseResult<Void> result = ResponseResult.errorResult(status == HttpStatus.NOT_FOUND ? UserOpCodeEnum.NOT_FOUND : UserOpCodeEnum.SERVER_ERROR);
        return BeanUtils.toMap(result);
    }
}
