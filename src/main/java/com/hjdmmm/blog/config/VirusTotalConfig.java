package com.hjdmmm.blog.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("virus-total")
@Getter
public class VirusTotalConfig {
    private final String key;

    private final String url;

    public VirusTotalConfig(String key, String url) {
        this.key = key;
        this.url = url;
    }
}
