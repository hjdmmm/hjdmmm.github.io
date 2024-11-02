package com.hjdmmm.blog.controller.blog;

import com.hjdmmm.blog.domain.vo.PreviewImageVO;
import com.hjdmmm.blog.enums.UserOpCodeEnum;
import com.hjdmmm.blog.exception.PreviewImageException;
import com.hjdmmm.blog.exception.UserOpException;
import com.hjdmmm.blog.service.AttachmentService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/image")
@Validated
public class ImageController {
    private final AttachmentService attachmentService;

    public ImageController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @GetMapping(value = "/{attachmentId}")
    public ResponseEntity<Resource> preview(@PathVariable @NotNull @Min(1) Long attachmentId) {
        PreviewImageVO previewImageVO;
        try {
            previewImageVO = attachmentService.previewImage(attachmentId);
        } catch (PreviewImageException e) {
            switch (e.errorType) {
                case FILE_NOT_EXIST:
                    throw new UserOpException(UserOpCodeEnum.FILE_NOT_EXIST);
                case FILE_NOT_IMAGE:
                    throw new UserOpException(UserOpCodeEnum.FILE_NOT_SUPPORT);
                default:
                    throw new UserOpException(UserOpCodeEnum.FILE_ERROR);
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(previewImageVO.getContentType());
        return new ResponseEntity<>(previewImageVO.getImage(), headers, HttpStatus.OK);
    }
}
