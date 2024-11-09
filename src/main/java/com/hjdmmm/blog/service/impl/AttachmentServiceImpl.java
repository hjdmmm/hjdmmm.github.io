package com.hjdmmm.blog.service.impl;

import com.hjdmmm.blog.config.AttachmentConfig;
import com.hjdmmm.blog.dao.AttachmentDAO;
import com.hjdmmm.blog.domain.entity.Attachment;
import com.hjdmmm.blog.domain.vo.ImageListVO;
import com.hjdmmm.blog.domain.vo.PageVO;
import com.hjdmmm.blog.domain.vo.PreviewImageVO;
import com.hjdmmm.blog.enums.PreviewImageErrorTypeEnum;
import com.hjdmmm.blog.enums.ServiceCodeEnum;
import com.hjdmmm.blog.exception.*;
import com.hjdmmm.blog.service.AttachmentService;
import com.hjdmmm.blog.service.ImageCompressor;
import com.hjdmmm.blog.service.StorageService;
import com.hjdmmm.blog.service.VirusScanner;
import com.hjdmmm.blog.util.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static com.hjdmmm.blog.domain.entity.Attachment.IMAGE_MEDIA_TYPE;

@Service
@Slf4j
public class AttachmentServiceImpl implements AttachmentService {
    private static final int IMAGE_MAX_WIDTH = 600;

    private final AttachmentConfig attachmentConfig;

    private final AttachmentDAO attachmentDAO;

    private final ImageCompressor imageCompressor;

    private final StorageService storageService;

    private final VirusScanner virusScanner;

    public AttachmentServiceImpl(AttachmentConfig attachmentConfig, AttachmentDAO attachmentDAO, ImageCompressor imageCompressor, StorageService storageService, VirusScanner virusScanner) {
        this.attachmentConfig = attachmentConfig;
        this.attachmentDAO = attachmentDAO;
        this.imageCompressor = imageCompressor;
        this.storageService = storageService;
        this.virusScanner = virusScanner;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String upload(MultipartFile file, boolean scanVirus) throws FileHasVirusException {
        if (file == null || file.isEmpty()) {
            throw new ServiceException(ServiceCodeEnum.SERVER_FILE_ERROR, String.format("文件 %s 为空", file));
        }

        // 添加数据库记录
        Attachment attachment = new Attachment();
        attachment.setOriginalName(file.getOriginalFilename());
        attachment.setMimeType(file.getContentType());
        attachmentDAO.insert(attachment);
        long id = attachment.getId();

        // 暂存在服务器本地
        File localFile = storeLocal(file, id, attachment.getCreateTime());

        // 病毒扫描
        if (scanVirus) {
            boolean hasVirus;
            try {
                hasVirus = virusScanner.scan(localFile);
            } catch (VirusScannerException e) {
                switch (e.errorType) {
                    case NO_SCANNER_CONFIG -> throw new ServiceException(ServiceCodeEnum.SERVER_ERROR, e);
                    case THIRD_PARTY_RESULT_ERROR ->
                            throw new ServiceException(ServiceCodeEnum.THIRD_PARTY_RESULT_ERROR, e);
                    case THIRD_PARTY_TIMEOUT ->
                            throw new ServiceException(ServiceCodeEnum.THIRD_PARTY_TIMEOUT_ERROR, e);
                    default -> throw new ServiceException(ServiceCodeEnum.THIRD_PARTY_ERROR, e);
                }
            }

            if (hasVirus) {
                throw new FileHasVirusException(String.format("文件 %s 有病毒", file.getOriginalFilename()));
            }
        }

        // 图片压缩
        Optional<MediaType> mediaType = MediaTypeFactory.getMediaType(file.getOriginalFilename());
        if (mediaType.isPresent() && imageCompressor.support(mediaType.get())) {
            try {
                imageCompressor.compress(localFile, IMAGE_MAX_WIDTH);
            } catch (ImageCompressException e) {
                throw new ServiceException(ServiceCodeEnum.SERVER_FILE_ERROR, String.format("压缩图片 %s 时出错", file.getOriginalFilename()), e);
            }
        }

        // 实际存储
        String url = storageService.upload(localFile, id);
        try {
            attachmentDAO.update(id, url);
        } catch (Exception e) {
            throw new ServiceException(ServiceCodeEnum.SERVER_ERROR, "修改 attachment 的 url 时出错", e);
        }

        return String.valueOf(id);
    }

    @Override
    public void delete(long id) {
        attachmentDAO.delete(id);
    }

    @Override
    public PageVO<ImageListVO> list(int pageNum, int pageSize, String name) {
        PageVO<Attachment> attachmentPageVO = attachmentDAO.pageSelect(pageNum, pageSize, name);

        List<ImageListVO> imageListVOList = BeanUtils.copyBeanList(attachmentPageVO.getRows(), ImageListVO.class);
        for (ImageListVO imageListVO : imageListVOList) {
            File file = storageService.download(imageListVO.getUrl());
            String originalBase64Image = null;
            try {
                originalBase64Image = Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));
            } catch (Exception e) {
                log.error("服务器读取文件失败", e);
            }

            if (originalBase64Image != null) {
                imageListVO.setThumbnail("data:" + imageListVO.getMimeType() + ";base64," + originalBase64Image);
            }
        }

        return attachmentPageVO.convertType(imageListVOList);
    }

    @Override
    public PreviewImageVO previewImage(long attachmentId) throws PreviewImageException {
        Attachment attachment = attachmentDAO.select(attachmentId);
        if (attachment == null) {
            throw new PreviewImageException(PreviewImageErrorTypeEnum.FILE_NOT_EXIST);
        }

        MediaType mediaType = MediaType.parseMediaType(attachment.getMimeType());
        if (!IMAGE_MEDIA_TYPE.includes(mediaType)) {
            throw new PreviewImageException(PreviewImageErrorTypeEnum.FILE_NOT_IMAGE);
        }

        FileSystemResource resource = new FileSystemResource(storageService.download(attachment.getUrl()));
        return new PreviewImageVO(mediaType, resource);
    }

    private File storeLocal(MultipartFile file, long id, LocalDateTime localDateTime) {
        String timeDirPath = localDateTime.getYear() + File.separator + localDateTime.getMonthValue() + File.separator;
        String dirPath = attachmentConfig.getLocalPath() + timeDirPath;
        File directory = new File(dirPath);

        if (directory.exists()) {
            if (directory.isFile()) {
                throw new ServiceException(ServiceCodeEnum.SERVER_FILE_ERROR, String.format("存在与本地图片文件夹 %s 重名的文件", dirPath));
            }
        } else if (!directory.mkdirs()) {
            throw new ServiceException(ServiceCodeEnum.SERVER_FILE_ERROR, String.format("创建文件夹 %s 失败", dirPath));
        }

        String filename = id + "_" + file.getOriginalFilename();
        File localFile = new File(dirPath + filename);
        try {
            file.transferTo(localFile);
        } catch (IOException e) {
            throw new ServiceException(ServiceCodeEnum.SERVER_FILE_ERROR, String.format("将图片 %s 传入本地图片文件夹 %s 时出错", file.getOriginalFilename(), localFile.getPath()), e);
        }

        return localFile;
    }
}
