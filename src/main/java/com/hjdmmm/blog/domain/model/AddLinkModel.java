package com.hjdmmm.blog.domain.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@NotNull
public class AddLinkModel {
    private String name;
    private String description;
    private String url;
    private Integer status;
}
