package com.hjdmmm.blog.controller.admin;

import com.hjdmmm.blog.annotation.Auth;
import com.hjdmmm.blog.constant.AttachmentConstants;
import com.hjdmmm.blog.context.UserIdHolder;
import com.hjdmmm.blog.domain.ResponseResult;
import com.hjdmmm.blog.domain.entity.Attachment;
import com.hjdmmm.blog.domain.vo.PageVO;
import com.hjdmmm.blog.enums.AuthTypeEnum;
import com.hjdmmm.blog.enums.UserOpCodeEnum;
import com.hjdmmm.blog.service.AttachmentService;
import com.hjdmmm.blog.service.ImageCompressor;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.Optional;

@AllArgsConstructor
@Auth(AuthTypeEnum.AUTHENTICATED)
@RestController
@RequestMapping("/admin/image")
@Slf4j
@Validated
public class AdminImageController {
    private static final int IMAGE_MAX_WIDTH = 600;

    private final AttachmentService attachmentService;

    private final ImageCompressor imageCompressor;

    private final UserIdHolder userIdHolder;

    @PostMapping(value = "")
    public ResponseResult<Long> upload(@NotNull MultipartFile img) {
        if (img.isEmpty()) {
            log.warn("图片为空", new IllegalArgumentException());
            return ResponseResult.errorResult(UserOpCodeEnum.METHOD_ARGUMENT_NOT_VALID);
        }

        Optional<MediaType> mediaType = MediaTypeFactory.getMediaType(img.getOriginalFilename());
        if (mediaType.isEmpty() || !AttachmentConstants.IMAGE_MEDIA_TYPE.includes(mediaType.get())) {
            log.warn("图片类型错误: mediaType={}", mediaType.orElse(null), new IllegalArgumentException());
            return ResponseResult.errorResult(UserOpCodeEnum.FILE_NOT_SUPPORT);
        }

        long attachmentId;
        try {
            attachmentId = doUpload(img, mediaType.get());
        } catch (Exception e) {
            log.error("系统错误", e);
            return ResponseResult.errorResult(UserOpCodeEnum.SERVER_ERROR);
        }

        return ResponseResult.okResult(attachmentId);
    }

    @DeleteMapping("/{id}")
    public ResponseResult<Void> delete(@PathVariable @Min(1) long id) {
        try {
            attachmentService.delete(id, userIdHolder.get());
        } catch (Exception e) {
            log.error("系统错误", e);
            return ResponseResult.errorResult(UserOpCodeEnum.SERVER_ERROR);
        }

        return ResponseResult.okResult();
    }

    @GetMapping("/list")
    public ResponseResult<PageVO<Attachment>> list(
        @RequestParam("pageNum") int pageNum,
        @RequestParam("pageSize") int pageSize,
        @RequestParam(value = "name", required = false) String name
    ) {
        PageVO<Attachment> pageVO;
        try {
            pageVO = attachmentService.listImages(pageNum, pageSize, name);
        } catch (Exception e) {
            log.error("系统错误", e);
            return ResponseResult.errorResult(UserOpCodeEnum.SERVER_ERROR);
        }

        return ResponseResult.okResult(pageVO);
    }

    private long doUpload(MultipartFile img, MediaType mediaType) throws Exception {
        long attachmentId;
        Path path = attachmentService.storeLocal(img);

        if (imageCompressor.support(mediaType)) {
            imageCompressor.compress(path, IMAGE_MAX_WIDTH);
        }

        attachmentId = attachmentService.upload(path, img.getOriginalFilename(), mediaType.toString(), userIdHolder.get());
        return attachmentId;
    }
}
