package com.hjdmmm.blog.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("attachment")
@Data
@NoArgsConstructor
public class AttachmentConfig {
    private static final String OS_TEMP_DIR = System.getProperty("java.io.tmpdir");

    private String localPath;

    public String getLocalPath() {
        if (localPath == null) {
            return OS_TEMP_DIR;
        }
        return localPath;
    }
}
