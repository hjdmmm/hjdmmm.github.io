package com.hjdmmm.blog.exception;

public class AttachmentNotExistException extends Exception {

    public AttachmentNotExistException(String message) {
        super(message);
    }

    public AttachmentNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
