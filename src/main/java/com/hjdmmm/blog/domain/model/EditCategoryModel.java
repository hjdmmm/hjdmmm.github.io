package com.hjdmmm.blog.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@NotNull
public class EditCategoryModel {
    @NotNull
    @Min(1)
    private Long id;
    private String name;
    private Long pid;
    private String description;
    private Integer status;
}
