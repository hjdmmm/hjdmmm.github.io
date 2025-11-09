package com.hjdmmm.blog.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@ConfigurationProperties("comment")
@Data
@NoArgsConstructor
public class CommentConfig {
    private static final Duration DEFAULT_COMMENT_IP_EXPIRE_DURATION = Duration.ofHours(2);
    private static final int DEFAULT_MAX_COMMENT_NUM = 100;

    @DurationUnit(ChronoUnit.SECONDS)
    private Duration expireSeconds;

    private int maxCommentNum;

    public Duration getExpireSeconds() {
        if (expireSeconds == null || expireSeconds.isZero() || expireSeconds.isNegative()) {
            return DEFAULT_COMMENT_IP_EXPIRE_DURATION;
        }
        return expireSeconds;
    }

    public int getMaxCommentNum() {
        if (maxCommentNum < 0) {
            return DEFAULT_MAX_COMMENT_NUM;
        }
        return maxCommentNum;
    }
}
