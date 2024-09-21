package com.hjdmmm.blog.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@NotNull
public class AddUserModel {
    @NotNull
    private String userName;
    private String nickName;
    private String password;
    private Integer type;
    private Integer status;
}
