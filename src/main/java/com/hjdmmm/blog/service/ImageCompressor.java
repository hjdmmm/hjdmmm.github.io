package com.hjdmmm.blog.service;

import com.hjdmmm.blog.exception.ImageCompressException;
import org.springframework.http.MediaType;

import java.io.File;

public interface ImageCompressor {
    boolean support(MediaType mediaType);

    void compress(File originFile, int maxWidth) throws ImageCompressException;
}
