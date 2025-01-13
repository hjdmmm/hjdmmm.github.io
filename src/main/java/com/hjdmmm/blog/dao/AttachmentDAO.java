package com.hjdmmm.blog.dao;

import com.hjdmmm.blog.domain.entity.Attachment;
import com.hjdmmm.blog.domain.vo.PageVO;

public interface AttachmentDAO {
    void insert(Attachment attachment);

    void delete(long id);

    void update(long id, String url);

    void updateById(Attachment attachment);

    Attachment select(long id);

    PageVO<Attachment> pageSelect(int pageNum, int pageSize, String name, String mimeTypePrefix);
}
