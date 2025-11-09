package com.hjdmmm.blog.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "article")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Size(max = 256)
    @Column(name = "title", nullable = false, length = 256)
    private String title;

    @Lob
    @Column(name = "content", nullable = false, length = 65535)
    private String content;

    @Size(max = 1024)
    @Column(name = "summary", nullable = false, length = 1024)
    private String summary;

    @Column(name = "parent_article_id", nullable = false)
    private Long parentArticleId;

    @Column(name = "view_count", nullable = false)
    private Long viewCount;

    @Column(name = "create_by", nullable = false)
    private Long createBy;

    @Column(name = "create_time", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Instant createTime;

    @Column(name = "update_by", nullable = false)
    private Long updateBy;

    @Column(name = "update_time", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Instant updateTime;

}