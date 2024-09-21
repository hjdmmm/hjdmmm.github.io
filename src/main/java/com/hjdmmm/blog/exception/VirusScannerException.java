package com.hjdmmm.blog.exception;

import com.hjdmmm.blog.enums.VirusScanErrorTypeEnum;

public class VirusScannerException extends Exception {
    public final VirusScanErrorTypeEnum errorType;

    public VirusScannerException(VirusScanErrorTypeEnum errorType) {
        super(errorType.defaultMessage);
        this.errorType = errorType;
    }

    public VirusScannerException(VirusScanErrorTypeEnum errorType, String message) {
        super(message);
        this.errorType = errorType;
    }

    public VirusScannerException(VirusScanErrorTypeEnum errorType, Throwable cause) {
        super(errorType.defaultMessage, cause);
        this.errorType = errorType;
    }

    public VirusScannerException(VirusScanErrorTypeEnum errorType, String message, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
    }
}
