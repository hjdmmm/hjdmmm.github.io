package com.hjdmmm.blog.service.impl;

import com.hjdmmm.blog.dao.AttachmentDAO;
import com.hjdmmm.blog.dao.DrawioDAO;
import com.hjdmmm.blog.domain.entity.Attachment;
import com.hjdmmm.blog.domain.entity.Drawio;
import com.hjdmmm.blog.domain.vo.DrawioGetVO;
import com.hjdmmm.blog.domain.vo.DrawioListVO;
import com.hjdmmm.blog.domain.vo.PageVO;
import com.hjdmmm.blog.domain.vo.PreviewImageVO;
import com.hjdmmm.blog.enums.ServiceCodeEnum;
import com.hjdmmm.blog.exception.AttachmentNotExistException;
import com.hjdmmm.blog.exception.FileHasVirusException;
import com.hjdmmm.blog.exception.ServiceException;
import com.hjdmmm.blog.service.AttachmentService;
import com.hjdmmm.blog.service.DrawioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.List;

@Service
@Slf4j
public class DrawioServiceImpl implements DrawioService {
    private final AttachmentService attachmentService;

    private final AttachmentDAO attachmentDAO;

    private final DrawioDAO drawioDAO;

    public DrawioServiceImpl(AttachmentService attachmentService, AttachmentDAO attachmentDAO, DrawioDAO drawioDAO) {
        this.attachmentService = attachmentService;
        this.attachmentDAO = attachmentDAO;
        this.drawioDAO = drawioDAO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(String name, MultipartFile source, MultipartFile picture) throws FileHasVirusException {
        long sourceAttachmentId = attachmentService.upload(source, false);
        long pictureAttachmentId = attachmentService.upload(picture, false);

        Drawio drawio = new Drawio();
        drawio.setName(name);
        drawio.setSourceAttachmentId(sourceAttachmentId);
        drawio.setPictureAttachmentId(pictureAttachmentId);
        drawioDAO.insert(drawio);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(long id, boolean deleteAttachments) {
        if (!deleteAttachments) {
            drawioDAO.delete(id);
            return;
        }

        Drawio drawio = drawioDAO.select(id);

        attachmentService.delete(drawio.getSourceAttachmentId());
        attachmentService.delete(drawio.getPictureAttachmentId());

        drawioDAO.delete(id);
    }

    @Override
    public void update(long id, String name) {
        drawioDAO.updateName(id, name);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(long id, MultipartFile source, MultipartFile picture) throws AttachmentNotExistException, FileHasVirusException {
        Drawio drawio = drawioDAO.select(id);

        long oldSourceAttachmentId = drawio.getSourceAttachmentId();
        long oldPictureAttachmentId = drawio.getPictureAttachmentId();
        long newSourceAttachmentId = attachmentService.upload(source, false);
        long newPictureAttachmentId = attachmentService.upload(picture, false);

        attachmentService.replace(newSourceAttachmentId, oldSourceAttachmentId);
        attachmentService.replace(newPictureAttachmentId, oldPictureAttachmentId);

        attachmentService.delete(newSourceAttachmentId);
        attachmentService.delete(newPictureAttachmentId);
    }

    @Override
    public DrawioGetVO get(long id) {
        Drawio drawio = drawioDAO.select(id);
        if (drawio == null) {
            return null;
        }

        DrawioGetVO vo = new DrawioGetVO();
        vo.setId(id);
        vo.setName(drawio.getName());

        Attachment sourceAttachment = attachmentDAO.select(drawio.getSourceAttachmentId());
        Resource sourceResource = attachmentService.download(sourceAttachment.getUrl());
        try {
            vo.setSource(sourceResource.getContentAsString(Charset.defaultCharset()));
        } catch (IOException e) {
            throw new ServiceException(ServiceCodeEnum.SERVER_FILE_ERROR, e);
        }

        return vo;
    }

    @Override
    public PageVO<DrawioListVO> list(int pageNum, int pageSize, String name) {
        PageVO<Drawio> pageVO = drawioDAO.pageSelect(pageNum, pageSize, name);

        List<DrawioListVO> drawioListVOList = pageVO.getRows().stream().map(this::buildDrawioListVOFromDrawio).toList();
        return pageVO.convertType(drawioListVOList);
    }

    private DrawioListVO buildDrawioListVOFromDrawio(Drawio drawio) {
        DrawioListVO vo = new DrawioListVO();
        vo.setId(drawio.getId());
        vo.setName(drawio.getName());
        vo.setCreateTime(drawio.getCreateTime());
        vo.setPictureId(drawio.getPictureAttachmentId());

        try {
            PreviewImageVO previewImageVO = attachmentService.previewImage(drawio.getPictureAttachmentId());
            String originalBase64Image = Base64.getEncoder().encodeToString(previewImageVO.getImage().getContentAsByteArray());
            vo.setThumbnail("data:" + previewImageVO.getContentType().toString() + ";base64," + originalBase64Image);
        } catch (Exception e) {
            log.error("加载drawio预览图失败，id:{}", drawio.getId(), e);
        }

        return vo;
    }
}
