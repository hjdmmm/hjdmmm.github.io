package com.hjdmmm.blog.service;

import com.hjdmmm.blog.domain.vo.ImageListVO;
import com.hjdmmm.blog.domain.vo.PageVO;
import com.hjdmmm.blog.domain.vo.PreviewImageVO;
import com.hjdmmm.blog.exception.FileHasVirusException;
import com.hjdmmm.blog.exception.PreviewImageException;
import org.springframework.web.multipart.MultipartFile;

public interface AttachmentService {
    String upload(MultipartFile file, boolean scanVirus) throws FileHasVirusException;

    void delete(long id);

    PageVO<ImageListVO> list(int pageNum, int pageSize, String name);

    PreviewImageVO previewImage(long attachmentId) throws PreviewImageException;
}
