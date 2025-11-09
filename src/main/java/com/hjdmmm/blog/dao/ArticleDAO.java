package com.hjdmmm.blog.dao;

import com.hjdmmm.blog.domain.dto.ArticleDTO;
import com.hjdmmm.blog.domain.dto.ArticleDetailDTO;
import com.hjdmmm.blog.domain.entity.Article;
import com.hjdmmm.blog.domain.vo.PageVO;

import java.util.List;

public interface ArticleDAO {
    void insert(Article article) throws Exception;

    void delete(long id) throws Exception;

    void updateTitleById(long id, String title, long modifier) throws Exception;

    void updateContentById(long id, String content, long modifier) throws Exception;

    void updateParentById(long id, long parentId, long modifier) throws Exception;

    void updateTagIds(long id, List<Long> tagIds, long modifier) throws Exception;

    void increaseViewCount(long id) throws Exception;

    PageVO<ArticleDTO> pageSelectOrderByViewCountDescWithTagId(int status, int pageNum, int pageSize, Long tagId) throws Exception;

    PageVO<ArticleDTO> pageSelectOrderByCreateTimeDescWithTagId(int status, int pageNum, int pageSize, Long tagId) throws Exception;

    PageVO<ArticleDTO> pageSelect(long parentId, int status, int pageNum, int pageSize) throws Exception;

    PageVO<ArticleDTO> pageSelect(int pageNum, int pageSize, String title, String summary) throws Exception;

    List<ArticleDTO> selectByStatusAndIdsOrParentIds(int status, List<Long> articleIds, List<Long> parentIds) throws Exception;

    ArticleDetailDTO selectDetail(long id) throws Exception;
}
