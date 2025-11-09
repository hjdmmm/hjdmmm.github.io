package com.hjdmmm.blog.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@ConfigurationProperties("login")
@Data
@NoArgsConstructor
public class LoginConfig {
    private static final Duration DEFAULT_LOGIN_EXPIRE_TIMEOUT = Duration.ofHours(1);

    @DurationUnit(ChronoUnit.SECONDS)
    private Duration expireSeconds;

    public Duration getExpireSeconds() {
        if (expireSeconds == null || expireSeconds.isZero() || expireSeconds.isNegative()) {
            return DEFAULT_LOGIN_EXPIRE_TIMEOUT;
        }
        return expireSeconds;
    }
}
