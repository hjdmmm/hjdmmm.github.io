package com.hjdmmm.blog.dao;

import com.hjdmmm.blog.domain.entity.Attachment;
import com.hjdmmm.blog.domain.vo.PageVO;

public interface AttachmentDAO {
    void insert(Attachment attachment) throws Exception;

    void delete(long id) throws Exception;

    void updateById(Attachment attachment) throws Exception;

    Attachment select(long id) throws Exception;

    PageVO<Attachment> pageSelect(int pageNum, int pageSize, String name, String mimeTypePrefix) throws Exception;
}
