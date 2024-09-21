package com.hjdmmm.blog.handler;

import com.hjdmmm.blog.domain.ResponseResult;
import com.hjdmmm.blog.enums.UserOpCodeEnum;
import com.hjdmmm.blog.exception.ServiceException;
import com.hjdmmm.blog.exception.UserOpException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;

import java.util.Optional;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UserOpException.class)
    public ResponseResult<Void> userOpExceptionHandler(UserOpException e) {
        log.warn("用户操作异常", e);
        return ResponseResult.errorResult(e.code, e.tip);
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseResult<Void> serviceExceptionHandler(ServiceException e) {
        log.error("业务代码异常", e);
        return ResponseResult.errorResult(UserOpCodeEnum.SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseResult<Void> methodArgumentTypeMismatchExceptionHandler(MethodArgumentTypeMismatchException e) {
        log.warn("请求传递参数不匹配异常", e);
        return ResponseResult.errorResult(UserOpCodeEnum.METHOD_ARGUMENT_TYPE_MISMATCH);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseResult<Void> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        log.warn("请求传递参数不合法异常", e);
        return ResponseResult.errorResult(UserOpCodeEnum.METHOD_ARGUMENT_NOT_VALID);
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseResult<Void> multipartExceptionHandler(MultipartException e) {
        log.warn("用户上传文件异常", e);

        ResponseResult<Void> result;
        Throwable tomcatFileException = Optional.of(e).map(Throwable::getCause).map(Throwable::getCause).orElse(null);
        if (tomcatFileException instanceof FileSizeLimitExceededException) {
            result = ResponseResult.errorResult(UserOpCodeEnum.FILE_SIZE_ERROR);
        } else if (tomcatFileException instanceof SizeLimitExceededException) {
            result = ResponseResult.errorResult(UserOpCodeEnum.FILE_TOTAL_SIZE_ERROR);
        } else {
            result = ResponseResult.errorResult(UserOpCodeEnum.FILE_ERROR);
        }
        return result;
    }

    @ExceptionHandler(Exception.class)
    public ResponseResult<Void> exceptionHandler(Exception e) {
        log.error("未捕获的其他异常", e);
        return ResponseResult.errorResult(UserOpCodeEnum.SERVER_ERROR);
    }
}
