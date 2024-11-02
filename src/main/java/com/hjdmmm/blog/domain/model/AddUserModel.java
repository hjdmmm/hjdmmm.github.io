package com.hjdmmm.blog.domain.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@NotNull
public class AddUserModel {
    @NotNull
    private String userName;
    private String nickName;
    @NotEmpty
    private String password;
    private Integer type;
    private Integer status;
}
