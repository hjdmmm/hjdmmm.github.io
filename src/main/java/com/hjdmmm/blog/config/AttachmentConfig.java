package com.hjdmmm.blog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("attachment")
@Data
public class AttachmentConfig {
    private String localPath;
}
