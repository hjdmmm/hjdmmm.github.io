package com.hjdmmm.blog.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@NotNull
public class ChangeStatusModel {
    @NotNull
    private Long id;
    private Integer status;
}
