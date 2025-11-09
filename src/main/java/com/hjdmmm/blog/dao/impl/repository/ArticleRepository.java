package com.hjdmmm.blog.dao.impl.repository;

import com.hjdmmm.blog.domain.entity.Article;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;

public interface ArticleRepository extends JpaRepository<Article, Long>, JpaSpecificationExecutor<Article> {
    @Modifying
    @Query("update Article a set a.viewCount = a.viewCount + 1 where a.id = ?1")
    void increaseViewCount(long id) throws Exception;

    @Modifying
    @Query("update Article a set a.title = ?1, a.updateBy = ?2 where a.id = ?3")
    void updateTitleById(String title, long updateBy, long id) throws Exception;

    @Modifying
    @Query("update Article a set a.content = ?1, a.updateBy = ?2 where a.id = ?3")
    void updateContentById(String content, long updateBy, long id) throws Exception;

    @Modifying
    @Query("update Article a set a.parentArticleId = ?1, a.updateBy = ?2 where a.id = ?3")
    void updateParentById(long parentId, long modifier, long id) throws Exception;

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    boolean existsForUpdateById(long id) throws Exception;
}