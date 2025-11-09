package com.hjdmmm.blog.service;

import java.nio.file.Path;

public interface VirusScanner {
    boolean scan(Path file) throws Exception;
}
