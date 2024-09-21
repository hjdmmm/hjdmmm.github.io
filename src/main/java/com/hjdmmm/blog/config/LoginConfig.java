package com.hjdmmm.blog.config;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties("login")
@Setter
public class LoginConfig {
    private static final Duration DEFAULT_LOGIN_EXPIRE_TIMEOUT = Duration.ofHours(1);

    private long expireSeconds = 0L;

    public Duration getLoginExpireTimeout() {
        if (expireSeconds <= 0L) {
            return DEFAULT_LOGIN_EXPIRE_TIMEOUT;
        }

        return Duration.ofSeconds(expireSeconds);
    }
}
