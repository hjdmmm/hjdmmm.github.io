package com.hjdmmm.blog.service;

import java.io.File;

public interface StorageService {
    String upload(File tmpFile, long id);

    File download(String url);
}
