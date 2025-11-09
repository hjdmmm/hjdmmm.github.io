package com.hjdmmm.blog.service;

import com.hjdmmm.blog.domain.entity.Attachment;
import com.hjdmmm.blog.domain.vo.PageVO;
import com.hjdmmm.blog.domain.vo.PreviewImageVO;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface AttachmentService {
    Path storeLocal(MultipartFile file) throws Exception;

    long upload(Path file, String originalFileName, String mimeType, long userId) throws Exception;

    void delete(long id, long userId) throws Exception;

    PageVO<Attachment> listImages(int pageNum, int pageSize, String name) throws Exception;

    PreviewImageVO previewImage(long attachmentId) throws Exception;
}
