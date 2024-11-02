package com.hjdmmm.blog.enums;

public enum UserOpCodeEnum {
    SUCCESS(200, "操作成功"),
    METHOD_ARGUMENT_TYPE_MISMATCH(400, "请求传递参数不匹配"),
    METHOD_ARGUMENT_NOT_VALID(401, "请求传递参数不合法"),
    NEED_LOGIN(402, "需要登录后操作"),
    NO_AUTH(403, "无权限操作"),
    NOT_FOUND(404, "未找到内容"),
    BAD_TOKEN(405, "登录状态错误"),
    NEED_ANONYMOUS(406, "需要匿名用户"),
    IP_NOT_FOUND(407, "不能获取到用户IP"),
    BAD_ACCESS(408, "访问不存在的内容"),
    SERVER_ERROR(500, "服务器错误"),
    USERNAME_EXIST(501, "用户名已存在"),
    EMAIL_EXIST(502, "邮箱已存在"),
    REQUIRE_USERNAME(503, "必须填写用户名"),
    LOGIN_ERROR(504, "用户名或密码错误"),
    USER_CHANGE_ERROR(505, "调整用户操作错误"),
    USER_NOT_LOGIN_ERROR(506, "未登录用户"),
    FILE_ERROR(600, "文件错误"),
    FILE_SIZE_ERROR(601, "上传单个文件过大"),
    FILE_TOTAL_SIZE_ERROR(602, "上传文件总大小过大"),
    FILE_HAS_VIRUS(603, "文件有病毒"),
    FILE_NOT_SUPPORT(604, "不支持该类型文件"),
    FILE_NOT_EXIST(605, "文件不存在"),
    TOO_MANY_COMMENTS(701, "短时间内评论次数过多"),
    ILLEGAL_ARTICLE_COMMENT(702, "该文章不能评论"),
    ;

    public final int code;
    public final String defaultTip;

    UserOpCodeEnum(int code, String defaultTip) {
        this.code = code;
        this.defaultTip = defaultTip;
    }
}
