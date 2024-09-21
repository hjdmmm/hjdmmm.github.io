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
@TableName("comment")
public class Comment {
    /**
     * 顶级评论的父ID
     */
    public static final long ROOT_PID = -1;

    /**
     * 评论是文章评论
     */
    public static final int ARTICLE_COMMENT = 0;

    @TableId
    private Long id;

    /**
     * 评论类型（0代表文章评论，1代表全局评论）
     */
    private Integer type;

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 父评论ID（-1表示本条评论为根评论）
     */
    private Long pid;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 回复的目标评论用户ID
     */
    private Long toCommentUserId;

    /**
     * 回复的目标评论ID
     */
    private Long toCommentId;

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
