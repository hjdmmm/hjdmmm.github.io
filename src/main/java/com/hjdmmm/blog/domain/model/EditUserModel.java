package com.hjdmmm.blog.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditUserModel {
    private Long id;
    private String userName;
    private String nickName;
    private String password;
    private Integer type;
    private Integer status;


}
