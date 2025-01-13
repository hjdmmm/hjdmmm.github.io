package com.hjdmmm.blog.service;

import com.hjdmmm.blog.domain.vo.DrawioGetVO;
import com.hjdmmm.blog.domain.vo.DrawioListVO;
import com.hjdmmm.blog.domain.vo.PageVO;
import com.hjdmmm.blog.exception.AttachmentNotExistException;
import com.hjdmmm.blog.exception.FileHasVirusException;
import org.springframework.web.multipart.MultipartFile;

public interface DrawioService {
    void add(String name, MultipartFile source, MultipartFile picture) throws FileHasVirusException;

    void delete(long id, boolean deleteAttachments);

    void update(long id, String name);

    void update(long id, MultipartFile source, MultipartFile picture) throws AttachmentNotExistException, FileHasVirusException;

    DrawioGetVO get(long id);

    PageVO<DrawioListVO> list(int pageNum, int pageSize, String name);
}
