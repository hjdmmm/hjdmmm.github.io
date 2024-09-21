package com.hjdmmm.blog.service;

public interface HashEncoder {
    String encode(String text);

    boolean verify(String input, String hash);
}
