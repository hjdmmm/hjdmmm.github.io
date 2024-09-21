package com.hjdmmm.blog.exception;

public class FileHasVirusException extends Exception {
    public FileHasVirusException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileHasVirusException(String message) {
        super(message);
    }
}
