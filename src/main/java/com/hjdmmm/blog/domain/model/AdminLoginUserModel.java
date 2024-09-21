package com.hjdmmm.blog.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@NotNull
public class AdminLoginUserModel {
    @NotNull
    private String userName;
    @NotNull
    private String password;
}
