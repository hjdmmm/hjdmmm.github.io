package com.hjdmmm.blog.domain;

import com.hjdmmm.blog.enums.UserOpCodeEnum;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter
@Setter(AccessLevel.PRIVATE)
public class ResponseResult<T> {
    private int code;
    private String msg;
    private T data;

    private ResponseResult() {
    }

    public static ResponseResult<Void> errorResult(int code, String msg) {
        return new ResponseResult<Void>().setCode(code).setMsg(msg);
    }

    public static ResponseResult<Void> errorResult(UserOpCodeEnum userOpCode) {
        return errorResult(userOpCode.code, userOpCode.defaultTip);
    }

    public static <T> ResponseResult<T> okResult() {
        return new ResponseResult<T>().setCode(UserOpCodeEnum.SUCCESS.code).setMsg(UserOpCodeEnum.SUCCESS.defaultTip);
    }

    public static <T> ResponseResult<T> okResult(T data) {
        return ResponseResult.<T>okResult().setData(data);
    }
}
