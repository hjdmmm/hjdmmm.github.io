package com.hjdmmm.blog.service;

public interface HashEncoder {
    String encode(String text) throws Exception;

    boolean verify(String input, String hash) throws Exception;
}
