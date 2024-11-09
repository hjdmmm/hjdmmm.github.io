package com.hjdmmm.blog.domain.model;

import com.hjdmmm.blog.domain.entity.Article;
import com.hjdmmm.blog.util.BooleanUtils;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@NotNull
public record AddArticleModel(
        String title,
        Boolean top,
        Boolean comment,
        Boolean draft,
        String content,
        Long categoryId,
        String summary,
        List<Long> tags
) {
    public Article toArticle() {
        Article article = new Article();
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
