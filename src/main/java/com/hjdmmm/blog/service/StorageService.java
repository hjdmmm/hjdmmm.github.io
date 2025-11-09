package com.hjdmmm.blog.service;

import java.nio.file.Path;

public interface StorageService {
    String upload(Path tmpFile, long id) throws Exception;

    Path download(String url) throws Exception;

    void delete(String url) throws Exception;
}
