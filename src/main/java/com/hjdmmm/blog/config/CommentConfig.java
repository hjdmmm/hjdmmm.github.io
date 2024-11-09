package com.hjdmmm.blog.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@ConfigurationProperties("comment")
@Getter
public class CommentConfig {
    private static final Duration DEFAULT_COMMENT_IP_EXPIRE_DURATION = Duration.ofHours(2);
    private static final int DEFAULT_MAX_COMMENT_NUM = 100;

    @DurationUnit(ChronoUnit.SECONDS)
    private final Duration expireSeconds;

    private final int maxCommentNum;

    public CommentConfig(Duration expireSeconds, Integer maxCommentNum) {
        if (expireSeconds == null || expireSeconds.isZero() || expireSeconds.isNegative()) {
            this.expireSeconds = DEFAULT_COMMENT_IP_EXPIRE_DURATION;
        } else {
            this.expireSeconds = expireSeconds;
        }

        if (maxCommentNum == null || maxCommentNum < 0) {
            this.maxCommentNum = DEFAULT_MAX_COMMENT_NUM;
        } else {
            this.maxCommentNum = maxCommentNum;
        }
    }
}
