package com.hjdmmm.blog.exception;

import com.hjdmmm.blog.enums.PreviewImageErrorTypeEnum;

public class PreviewImageException extends Exception {
    public final PreviewImageErrorTypeEnum errorType;

    public PreviewImageException(PreviewImageErrorTypeEnum errorType) {
        this.errorType = errorType;
    }
}
