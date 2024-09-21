package com.hjdmmm.blog.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserListVO {
    private Long id;
    private String userName;
    private String nickName;
    private Integer type;
    private Integer status;
    private LocalDateTime createTime;
}
