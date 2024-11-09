package com.hjdmmm.blog.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("attachment")
@Getter
public class AttachmentConfig {
    private static final String OS_TEMP_DIR = System.getProperty("java.io.tmpdir");

    private final String localPath;

    public AttachmentConfig(String localPath) {
        if (localPath == null) {
            this.localPath = OS_TEMP_DIR;
        } else {
            this.localPath = localPath;
        }
    }
}
