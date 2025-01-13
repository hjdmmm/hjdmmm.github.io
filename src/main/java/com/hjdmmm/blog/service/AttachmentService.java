package com.hjdmmm.blog.service;

import com.hjdmmm.blog.domain.vo.ImageListVO;
import com.hjdmmm.blog.domain.vo.PageVO;
import com.hjdmmm.blog.domain.vo.PreviewImageVO;
import com.hjdmmm.blog.exception.AttachmentNotExistException;
import com.hjdmmm.blog.exception.FileHasVirusException;
import com.hjdmmm.blog.exception.PreviewImageException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface AttachmentService {
    long upload(MultipartFile file, boolean scanVirus) throws FileHasVirusException;

    void delete(long id);

    void replace(long sourceId, long destinationId) throws AttachmentNotExistException;

    PageVO<ImageListVO> listImages(int pageNum, int pageSize, String name);

    PreviewImageVO previewImage(long attachmentId) throws PreviewImageException;

    Resource download(String url);
}
