package com.hjdmmm.blog.service.impl;

import com.hjdmmm.blog.config.AttachmentConfig;
import com.hjdmmm.blog.constant.AttachmentConstants;
import com.hjdmmm.blog.dao.AttachmentDAO;
import com.hjdmmm.blog.domain.entity.Attachment;
import com.hjdmmm.blog.domain.vo.PageVO;
import com.hjdmmm.blog.domain.vo.PreviewImageVO;
import com.hjdmmm.blog.service.AttachmentService;
import com.hjdmmm.blog.service.StorageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@AllArgsConstructor
@Service
@Slf4j
public class AttachmentServiceImpl implements AttachmentService {

    private static final String IMAGE_MEDIA_TYPE_PREFIX = "image/";

    private final AttachmentConfig attachmentConfig;

    private final AttachmentDAO attachmentDAO;

    private final StorageService storageService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long upload(Path file, String originalFileName, String mimeType, long modifier) throws Exception {
        Attachment attachment = new Attachment();
        attachment.setOriginalName(originalFileName);
        attachment.setMimeType(mimeType);
        attachment.setCreateBy(modifier);
        attachment.setUpdateBy(modifier);
        attachmentDAO.insert(attachment);

        long id = attachment.getId();

        String url = storageService.upload(file, id);

        try {
            Attachment urlAttachment = new Attachment();
            urlAttachment.setId(id);
            urlAttachment.setUrl(url);
            attachmentDAO.updateById(urlAttachment);
        } catch (Exception e) {
            storageService.delete(url);
            throw e;
        }

        return id;
    }

    @Override
    public void delete(long id, long modifier) throws Exception {
        attachmentDAO.delete(id);
    }

    @Override
    public PageVO<Attachment> listImages(int pageNum, int pageSize, String name) throws Exception {
        return attachmentDAO.pageSelect(pageNum, pageSize, name, IMAGE_MEDIA_TYPE_PREFIX);
    }

    @Override
    public PreviewImageVO previewImage(long attachmentId) throws Exception {
        Attachment attachment = attachmentDAO.select(attachmentId);
        if (attachment == null) {
            return null;
        }

        MediaType mediaType = MediaType.parseMediaType(attachment.getMimeType());
        if (!AttachmentConstants.IMAGE_MEDIA_TYPE.includes(mediaType)) {
            return null;
        }

        FileSystemResource resource = new FileSystemResource(storageService.download(attachment.getUrl()));
        return PreviewImageVO.builder().contentType(mediaType).image(resource).build();
    }

    @Override
    public Path storeLocal(MultipartFile file) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        String timeDirPath = now.getYear() + File.separator + now.getMonthValue() + File.separator;
        String dirPath = attachmentConfig.getLocalPath() + timeDirPath;

        Path directory = Path.of(dirPath);
        if (Files.exists(directory)) {
            if (!Files.isDirectory(directory)) {
                throw new IOException("存在与本地图片文件夹重名的文件: " + dirPath);
            }
        } else {
            Files.createDirectories(directory);
        }

        Path path;
        do {
            path = directory.resolve(ThreadLocalRandom.current().nextLong() + "_" + file.getOriginalFilename());
        } while (Files.exists(path));

        file.transferTo(path);

        return path;
    }
}
