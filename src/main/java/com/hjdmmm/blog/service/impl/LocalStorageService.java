package com.hjdmmm.blog.service.impl;

import com.hjdmmm.blog.service.StorageService;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class LocalStorageService implements StorageService {

    @Override
    public String upload(Path tmpFile, long id) throws Exception {
        // 直接使用临时文件
        return tmpFile.toString();
    }

    @Override
    public Path download(String url) throws Exception {
        return Path.of(url);
    }

    @Override
    public void delete(String url) throws Exception {
        Files.delete(Path.of(url));
    }
}
