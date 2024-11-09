package com.hjdmmm.blog.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@ConfigurationProperties("login")
@Getter
public class LoginConfig {
    private static final Duration DEFAULT_LOGIN_EXPIRE_TIMEOUT = Duration.ofHours(1);

    @DurationUnit(ChronoUnit.SECONDS)
    private final Duration expireSeconds;

    public LoginConfig(Duration expireSeconds) {
        if (expireSeconds == null || expireSeconds.isZero() || expireSeconds.isNegative()) {
            this.expireSeconds = DEFAULT_LOGIN_EXPIRE_TIMEOUT;
        } else {
            this.expireSeconds = expireSeconds;
        }
    }
}
