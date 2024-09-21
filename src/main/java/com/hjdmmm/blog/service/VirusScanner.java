package com.hjdmmm.blog.service;

import com.hjdmmm.blog.exception.VirusScannerException;

import java.io.File;

public interface VirusScanner {
    boolean scan(File file) throws VirusScannerException;
}
