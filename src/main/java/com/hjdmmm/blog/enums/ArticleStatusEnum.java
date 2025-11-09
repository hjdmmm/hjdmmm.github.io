package com.hjdmmm.blog.enums;

public enum ArticleStatusEnum {
    DRAFT(1),
    PUBLISHED(2),
    ;

    public final int number;

    ArticleStatusEnum(int number) {
        this.number = number;
    }
}
