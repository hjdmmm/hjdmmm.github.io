package com.hjdmmm.blog.exception;

import com.hjdmmm.blog.enums.ServiceCodeEnum;

public class ServiceException extends RuntimeException {
    public final int code;
    public final String msg;

    public ServiceException(ServiceCodeEnum serviceCode, String msg, Throwable cause) {
        super(String.format("[业务代码异常] 错误码：%d，错误信息：%s", serviceCode.code, msg), cause);
        this.code = serviceCode.code;
        this.msg = msg;
    }

    public ServiceException(ServiceCodeEnum serviceCode, Throwable cause) {
        this(serviceCode, serviceCode.defaultMessage, cause);
    }

    public ServiceException(ServiceCodeEnum serviceCode, String msg) {
        super(String.format("[业务代码异常] 错误码：%d，错误信息：%s", serviceCode.code, msg));
        this.code = serviceCode.code;
        this.msg = msg;
    }

    public ServiceException(ServiceCodeEnum serviceCode) {
        this(serviceCode, serviceCode.defaultMessage);
    }
}
