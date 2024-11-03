package com.hjdmmm.blog.domain.model;

import com.hjdmmm.blog.domain.entity.Article;
import com.hjdmmm.blog.util.BooleanUtils;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@NotNull
public class EditArticleModel {
    @NotNull
    @Min(1)
    private Long id;
    private String title;
    private Boolean top;
    private Boolean comment;
    private Boolean draft;
    private String content;
    private Long categoryId;
    private String summary;
    private List<Long> tags;

    public Article toArticle() {
        Article article = new Article();
        article.setId(id);
        article.setTitle(title);
        article.setTop(BooleanUtils.toInt(top));
        article.setComment(BooleanUtils.toInt(comment));
        article.setStatus(BooleanUtils.toInt(draft));
        article.setContent(content);
        article.setCategoryId(categoryId);
        article.setSummary(summary);
        return article;
    }
}
