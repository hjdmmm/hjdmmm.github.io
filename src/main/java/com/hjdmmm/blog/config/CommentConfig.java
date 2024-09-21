package com.hjdmmm.blog.config;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties("comment")
@Setter
public class CommentConfig {
    private static final Duration DEFAULT_COMMENT_IP_EXPIRE_DURATION = Duration.ofHours(2);
    private static final int DEFAULT_MAX_COMMENT_NUM = 100;

    private long expireSeconds = 0L;
    private int maxCommentNum = -1;

    public Duration getCommentIpExpireDuration() {
        if (expireSeconds <= 0L) {
            return DEFAULT_COMMENT_IP_EXPIRE_DURATION;
        }

        return Duration.ofSeconds(expireSeconds);
    }

    public int getMaxCommentNum() {
        if (maxCommentNum < 0) {
            return DEFAULT_MAX_COMMENT_NUM;
        }

        return maxCommentNum;
    }
}
