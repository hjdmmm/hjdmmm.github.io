package com.hjdmmm.blog.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("virus-total")
@Data
@NoArgsConstructor
public class VirusTotalConfig {
    private String key;
    private String url;
}
