package com.hjdmmm.blog.controller.admin;

import com.hjdmmm.blog.annotation.Auth;
import com.hjdmmm.blog.domain.ResponseResult;
import com.hjdmmm.blog.domain.entity.Attachment;
import com.hjdmmm.blog.domain.vo.ImageListVO;
import com.hjdmmm.blog.domain.vo.PageVO;
import com.hjdmmm.blog.enums.AuthTypeEnum;
import com.hjdmmm.blog.enums.UserOpCodeEnum;
import com.hjdmmm.blog.exception.FileHasVirusException;
import com.hjdmmm.blog.exception.UserOpException;
import com.hjdmmm.blog.service.AttachmentService;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@RestController
@RequestMapping("/admin/image")
@Auth(AuthTypeEnum.AUTHENTICATED)
@Validated
public class AdminImageController {

    private final AttachmentService attachmentService;

    public AdminImageController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping(value = "")
    public ResponseResult<String> upload(@NotNull MultipartFile img) {
        if (img.isEmpty()) {
            throw new UserOpException(UserOpCodeEnum.FILE_ERROR, "图片不能为空");
        }

        Optional<MediaType> mediaType = MediaTypeFactory.getMediaType(img.getOriginalFilename());
        if (!mediaType.isPresent() || !Attachment.IMAGE_MEDIA_TYPE.includes(mediaType.get())) {
            throw new UserOpException(UserOpCodeEnum.FILE_NOT_SUPPORT);
        }

        String attachmentId;
        try {
            attachmentId = attachmentService.upload(img, false);
        } catch (FileHasVirusException e) {
            throw new UserOpException(UserOpCodeEnum.FILE_HAS_VIRUS);
        }

        return ResponseResult.okResult(attachmentId);
    }

    @DeleteMapping("/{id}")
    public ResponseResult<Void> delete(@PathVariable @NotNull @Min(1) Long id) {
        attachmentService.delete(id);
        return ResponseResult.okResult();
    }

    @GetMapping("/list")
    public ResponseResult<PageVO<ImageListVO>> list(Integer pageNum, Integer pageSize, String name) {
        PageVO<ImageListVO> pageVO = attachmentService.list(
                Optional.ofNullable(pageNum).orElse(1),
                Optional.ofNullable(pageSize).orElse(10),
                name);

        return ResponseResult.okResult(pageVO);
    }
}
