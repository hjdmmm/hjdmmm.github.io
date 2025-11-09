package com.hjdmmm.blog.service;

import org.springframework.http.MediaType;

import java.nio.file.Path;

public interface ImageCompressor {
    boolean support(MediaType mediaType) throws Exception;

    void compress(Path originFile, int maxWidth) throws Exception;
}
