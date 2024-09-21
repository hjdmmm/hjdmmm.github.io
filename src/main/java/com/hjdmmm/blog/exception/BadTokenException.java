package com.hjdmmm.blog.exception;

public class BadTokenException extends Exception {

    public BadTokenException(String message) {
        super(message);
    }

    public BadTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
