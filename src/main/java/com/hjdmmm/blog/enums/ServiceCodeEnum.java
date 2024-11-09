package com.hjdmmm.blog.enums;

public enum ServiceCodeEnum {
    SERVER_ERROR(1000, "服务器内部出错"),
    SERVER_FILE_ERROR(1001, "服务器内部文件出错"),
    SERVER_SQL_ERROR(1002, "服务器内部SQL执行出错"),
    THIRD_PARTY_ERROR(2000, "调用第三方接口异常"),
    THIRD_PARTY_RESULT_ERROR(2001, "第三方返回结果异常"),
    THIRD_PARTY_TIMEOUT_ERROR(2002, "第三方请求超时"),
    TOKEN_ERROR(3000, "与token相关的错误"),
    ;

    public final int code;
    public final String defaultMessage;

    ServiceCodeEnum(int code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
}
