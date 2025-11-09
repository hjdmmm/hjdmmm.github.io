package com.hjdmmm.blog.service;

import com.hjdmmm.blog.domain.dto.ArticleDTO;
import com.hjdmmm.blog.domain.model.AddArticleModel;
import com.hjdmmm.blog.domain.vo.ArticleDetailVO;
import com.hjdmmm.blog.domain.vo.ArticleNodeVO;
import com.hjdmmm.blog.domain.vo.PageVO;

import java.util.List;

public interface ArticleService {
    long add(AddArticleModel model, long operator) throws Exception;

    void delete(long id, long modifier) throws Exception;

    void updateTitle(long id, String title, long modifier) throws Exception;

    void updateContent(long id, String content, long modifier) throws Exception;

    void updateParent(long id, Long parentId, long modifier) throws Exception;

    void updateTagIds(long id, List<Long> tagIds, long modifier) throws Exception;

    void increaseViewCount(long id) throws Exception;

    List<Long> getHotArticleIds() throws Exception;

    List<Long> getLatestArticleIds() throws Exception;

    PageVO<Long> listCategoryArticleIds(int pageNum, int pageSize) throws Exception;

    List<ArticleNodeVO> getArticleNodes(List<Long> articleIds, Long userId) throws Exception;

    ArticleDetailVO getArticleDetail(long id, Long userId) throws Exception;

    PageVO<ArticleDTO> list(int pageNum, int pageSize, String title, String summary) throws Exception;
}
