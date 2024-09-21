package com.hjdmmm.blog.config;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;

@Configuration
@Setter
public class WebConfig implements WebMvcConfigurer {
    private static final Duration REST_TEMPLATE_READ_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration REST_TEMPLATE_CONNECT_TIMEOUT = Duration.ofSeconds(15);

    @Value("${server.allowed-origin-patterns}")
    private String allowedOriginPatterns;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if (!StringUtils.hasText(allowedOriginPatterns)) {
            return;
        }

        registry.addMapping("/**") // 设置允许跨域的路径
                .allowedOriginPatterns(allowedOriginPatterns) // 设置允许跨域请求的域名
                .allowCredentials(true) // 是否允许Cookie
                .allowedMethods("*")
                .allowedHeaders("*");
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setReadTimeout(REST_TEMPLATE_READ_TIMEOUT)
                .setConnectTimeout(REST_TEMPLATE_CONNECT_TIMEOUT)
                .build();
    }
}
