package com.hjdmmm.blog.controller;

import com.hjdmmm.blog.domain.vo.PreviewImageVO;
import com.hjdmmm.blog.service.AttachmentService;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/image")
@Slf4j
@Validated
public class ImageController {
    private final AttachmentService attachmentService;

    @GetMapping(value = "/{attachmentId}")
    public ResponseEntity<Resource> preview(@PathVariable @Min(1) long attachmentId) {
        PreviewImageVO previewImageVO;
        try {
            previewImageVO = attachmentService.previewImage(attachmentId);
        } catch (Exception e) {
            log.error("系统错误", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (previewImageVO == null) {
            log.warn("图片不存在: attachmentId={}", attachmentId, new IllegalArgumentException());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(previewImageVO.contentType());
        return new ResponseEntity<>(previewImageVO.image(), headers, HttpStatus.OK);
    }
}
