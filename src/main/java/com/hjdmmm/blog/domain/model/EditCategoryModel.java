package com.hjdmmm.blog.domain.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
