SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for article
-- ----------------------------
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article`
(
    `id`           bigint(20) NOT NULL AUTO_INCREMENT,
    `title`        varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `content`      longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
    `summary`      varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `category_id`  bigint(20) NULL DEFAULT NULL,
    `thumbnail_id` bigint(20) NULL DEFAULT NULL COMMENT '缩略图文件ID',
    `top`          tinyint(1) NULL DEFAULT 0 COMMENT '是否置顶（0否，1是）',
    `comment`      tinyint(1) NULL DEFAULT 1 COMMENT '是否允许评论（0否，1是）',
    `status`       tinyint(1) NULL DEFAULT 1 COMMENT '状态（0已发布，1草稿）',
    `view_count`   bigint(20) NULL DEFAULT 0,
    `create_by`    bigint(20) NULL DEFAULT NULL,
    `create_time`  datetime NULL DEFAULT NULL,
    `update_by`    bigint(20) NULL DEFAULT NULL,
    `update_time`  datetime NULL DEFAULT NULL,
    `del_flag`     tinyint(1) NULL DEFAULT 0 COMMENT '删除标志（0代表未删除，1代表已删除）',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for article_tag
-- ----------------------------
DROP TABLE IF EXISTS `article_tag`;
CREATE TABLE `article_tag`
(
    `article_id`  bigint(20) NOT NULL COMMENT '文章id',
    `tag_id`      bigint(20) NOT NULL COMMENT '标签id',
    `create_by`   bigint(20) NULL DEFAULT NULL,
    `create_time` datetime NULL DEFAULT NULL,
    `update_by`   bigint(20) NULL DEFAULT NULL,
    `update_time` datetime NULL DEFAULT NULL,
    `del_flag`    tinyint(1) NULL DEFAULT NULL COMMENT '删除标志（0代表未删除，1代表已删除）',
    PRIMARY KEY (`article_id`, `tag_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for attachment
-- ----------------------------
DROP TABLE IF EXISTS `attachment`;
CREATE TABLE `attachment`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT,
    `original_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `mime_type`     varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'MIME类型，如image/jpeg',
    `url`           varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件地址',
    `create_by`     bigint(20) NULL DEFAULT NULL,
    `create_time`   datetime NULL DEFAULT NULL,
    `update_by`     bigint(20) NULL DEFAULT NULL,
    `update_time`   datetime NULL DEFAULT NULL,
    `del_flag`      tinyint(1) NULL DEFAULT 0 COMMENT '删除标志（0代表未删除，1代表已删除）',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `name`        varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `pid`         bigint(20) NULL DEFAULT -1 COMMENT '父分类ID，如果没有父分类为-1',
    `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `status`      tinyint(1) NULL DEFAULT 0 COMMENT '状态（0正常，1禁用）',
    `create_by`   bigint(20) NULL DEFAULT NULL,
    `create_time` datetime NULL DEFAULT NULL,
    `update_by`   bigint(20) NULL DEFAULT NULL,
    `update_time` datetime NULL DEFAULT NULL,
    `del_flag`    tinyint(1) NULL DEFAULT 0 COMMENT '删除标志（0代表未删除，1代表已删除）',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`
(
    `id`                 bigint(20) NOT NULL AUTO_INCREMENT,
    `type`               tinyint(1) NULL DEFAULT 0 COMMENT '评论类型（0代表文章评论，1代表全局评论）',
    `article_id`         bigint(20) NULL DEFAULT NULL COMMENT '文章ID',
    `pid`                bigint(20) NULL DEFAULT -1 COMMENT '父评论ID（-1表示本条评论为根评论）',
    `content`            varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '评论内容',
    `to_comment_user_id` bigint(20) NULL DEFAULT NULL COMMENT '回复的目标评论用户ID',
    `to_comment_id`      bigint(20) NULL DEFAULT -1 COMMENT '回复的目标评论ID',
    `create_by`          bigint(20) NULL DEFAULT NULL,
    `create_time`        datetime NULL DEFAULT NULL,
    `update_by`          bigint(20) NULL DEFAULT NULL,
    `update_time`        datetime NULL DEFAULT NULL,
    `del_flag`           tinyint(1) NULL DEFAULT 0 COMMENT '删除标志（0代表未删除，1代表已删除）',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for drawio
-- ----------------------------
DROP TABLE IF EXISTS `drawio`;
CREATE TABLE `drawio`
(
    `id`                    bigint(20) NOT NULL AUTO_INCREMENT,
    `name`                  varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `source_attachment_id`  bigint(20) NOT NULL,
    `picture_attachment_id` bigint(20) NOT NULL,
    `create_by`             bigint(20) NULL DEFAULT NULL,
    `create_time`           datetime NULL DEFAULT NULL,
    `update_by`             bigint(20) NULL DEFAULT NULL,
    `update_time`           datetime NULL DEFAULT NULL,
    `del_flag`              tinyint(1) NULL DEFAULT 0 COMMENT '删除标志（0代表未删除，1代表已删除）',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for link
-- ----------------------------
DROP TABLE IF EXISTS `link`;
CREATE TABLE `link`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `name`        varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `url`         varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '网站地址',
    `status`      tinyint(1) NULL DEFAULT 2 COMMENT '审核状态（0代表审核通过，1代表审核未通过，2代表未审核）',
    `create_by`   bigint(20) NULL DEFAULT NULL,
    `create_time` datetime NULL DEFAULT NULL,
    `update_by`   bigint(20) NULL DEFAULT NULL,
    `update_time` datetime NULL DEFAULT NULL,
    `del_flag`    tinyint(1) NULL DEFAULT 0 COMMENT '删除标志（0代表未删除，1代表已删除）',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `user_name`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'NULL' COMMENT '用户名',
    `nick_name`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'NULL' COMMENT '昵称',
    `password`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'NULL' COMMENT '密码',
    `type`        tinyint(1) NULL DEFAULT 0 COMMENT '用户类型（0代表普通用户，1代表管理员）',
    `status`      tinyint(1) NULL DEFAULT 0 COMMENT '账号状态（0正常，1停用）',
    `email`       varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
    `avatar_id`   bigint(20) NULL DEFAULT NULL COMMENT '头像文件ID',
    `create_by`   bigint(20) NULL DEFAULT NULL,
    `create_time` datetime NULL DEFAULT NULL,
    `update_by`   bigint(20) NULL DEFAULT NULL,
    `update_time` datetime NULL DEFAULT NULL,
    `del_flag`    tinyint(1) NULL DEFAULT 0 COMMENT '删除标志（0代表未删除，1代表已删除）',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `name`        varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标签名',
    `remark`      varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
    `create_by`   bigint(20) NULL DEFAULT NULL,
    `create_time` datetime NULL DEFAULT NULL,
    `update_by`   bigint(20) NULL DEFAULT NULL,
    `update_time` datetime NULL DEFAULT NULL,
    `del_flag`    tinyint(1) NULL DEFAULT 0 COMMENT '删除标志（0代表未删除，1代表已删除）',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

SET
FOREIGN_KEY_CHECKS = 1;
