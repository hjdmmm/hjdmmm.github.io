package com.hjdmmm.blog.domain.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
