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
public class ChangeStatusModel {
    @NotNull
    @Min(1)
    private Long id;
    private Integer status;
}
