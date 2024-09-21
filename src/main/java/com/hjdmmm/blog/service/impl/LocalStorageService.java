package com.hjdmmm.blog.service.impl;

import com.hjdmmm.blog.service.StorageService;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class LocalStorageService implements StorageService {

    @Override
    public String upload(File tmpFile, long id) {
        // 直接使用临时文件
        return tmpFile.getPath();
    }

    @Override
    public File download(String url) {
        return new File(url);
    }
}
