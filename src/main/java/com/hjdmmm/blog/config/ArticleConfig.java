package com.hjdmmm.blog.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("article")
@Data
@NoArgsConstructor
public class ArticleConfig {
    private Long normalArticleTagId;
}
