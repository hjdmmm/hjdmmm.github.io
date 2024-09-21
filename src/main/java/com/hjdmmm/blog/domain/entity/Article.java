package com.hjdmmm.blog.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("article")
public class Article {
    /**
     * 文章是草稿状态
     */
    public static final int ARTICLE_STATUS_DRAFT = 1;

    /**
     * 文章是正常发布状态
     */
    public static final int ARTICLE_STATUS_NORMAL = 0;

    @TableId
    private Long id;

    private String title;

    private String content;

    private String summary;

    private Long categoryId;

    /**
     * 缩略图文件ID
     */
    private Long thumbnailId;

    /**
     * 是否置顶（0否，1是）
     */
    private Integer top;

    /**
     * 是否允许评论（0否，1是）
     */
    private Integer comment;

    /**
     * 状态（0已发布，1草稿）
     */
    private Integer status;

    private Long viewCount;

    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 删除标志（0代表未删除，1代表已删除）
     */
    private Integer delFlag;

}
