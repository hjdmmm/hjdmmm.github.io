package com.hjdmmm.blog.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogLoginVO {
    private String token;
    private BlogUserInfoVO userInfo;
}
