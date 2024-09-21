package com.hjdmmm.blog.exception;

import com.hjdmmm.blog.enums.UserOpCodeEnum;

public class UserOpException extends RuntimeException {
    public final int code;
    public final String tip;

    public UserOpException(UserOpCodeEnum userOpCodeEnum, String tip) {
        super(String.format("[用户操作异常] 错误码：%d，提示信息：%s", userOpCodeEnum.code, tip));
        this.code = userOpCodeEnum.code;
        this.tip = tip;
    }

    public UserOpException(UserOpCodeEnum userOpCodeEnum) {
        this(userOpCodeEnum, userOpCodeEnum.defaultTip);
    }
}
