package com.hjdmmm.blog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("virus-total")
@Data
public class VirusTotalConfig {
    private String key;

    private String url;
}
