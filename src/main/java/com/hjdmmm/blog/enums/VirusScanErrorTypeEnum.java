package com.hjdmmm.blog.enums;

public enum VirusScanErrorTypeEnum {
    NO_SCANNER_CONFIG("未配置病毒扫描器配置"),
    THIRD_PARTY_FAIL("调用病毒扫描器失败"),
    THIRD_PARTY_RESULT_ERROR("病毒扫描器返回结果异常"),
    THIRD_PARTY_TIMEOUT("病毒扫描器超时"),
    ;

    public final String defaultMessage;

    VirusScanErrorTypeEnum(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }
}
