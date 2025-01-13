package com.hjdmmm.blog.controller.admin;

import com.hjdmmm.blog.annotation.Auth;
import com.hjdmmm.blog.domain.ResponseResult;
import com.hjdmmm.blog.domain.model.EditDrawioNameModel;
import com.hjdmmm.blog.domain.vo.DrawioGetVO;
import com.hjdmmm.blog.domain.vo.DrawioListVO;
import com.hjdmmm.blog.domain.vo.PageVO;
import com.hjdmmm.blog.enums.AuthTypeEnum;
import com.hjdmmm.blog.enums.UserOpCodeEnum;
import com.hjdmmm.blog.exception.AttachmentNotExistException;
import com.hjdmmm.blog.exception.FileHasVirusException;
import com.hjdmmm.blog.exception.UserOpException;
import com.hjdmmm.blog.service.DrawioService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/admin/drawio")
@Auth(AuthTypeEnum.AUTHENTICATED)
@Validated
public class AdminDrawioController {

    private final DrawioService drawioService;

    public AdminDrawioController(DrawioService drawioService) {
        this.drawioService = drawioService;
    }

    @PostMapping(value = "")
    public ResponseResult<Void> add(@NotNull String name, @NotNull MultipartFile source, @NotNull MultipartFile picture) {
        if (source.isEmpty()) {
            throw new UserOpException(UserOpCodeEnum.FILE_ERROR, "源码不能为空");
        }

        if (picture.isEmpty()) {
            throw new UserOpException(UserOpCodeEnum.FILE_ERROR, "图片不能为空");
        }

        try {
            drawioService.add(name, source, picture);
        } catch (FileHasVirusException e) {
            throw new UserOpException(UserOpCodeEnum.FILE_HAS_VIRUS);
        }
        return ResponseResult.okResult();
    }

    @DeleteMapping("/{id}")
    public ResponseResult<Void> delete(@PathVariable @NotNull @Min(1) Long id) {
        drawioService.delete(id, true);
        return ResponseResult.okResult();
    }

    @PutMapping("/updateName")
    public ResponseResult<Void> updateName(@RequestBody EditDrawioNameModel model) {
        drawioService.update(model.id(), model.name());
        return ResponseResult.okResult();
    }

    /**
     * 因为Put不能使用form-data，因此使用Post
     */
    @PostMapping(value = "/updateFile")
    public ResponseResult<Void> updateFile(@NotNull @Min(1) Long id, @NotNull MultipartFile source, @NotNull MultipartFile picture) {
        if (source.isEmpty()) {
            throw new UserOpException(UserOpCodeEnum.FILE_ERROR, "源码不能为空");
        }

        if (picture.isEmpty()) {
            throw new UserOpException(UserOpCodeEnum.FILE_ERROR, "图片不能为空");
        }

        try {
            drawioService.update(id, source, picture);
        } catch (AttachmentNotExistException e) {
            throw new UserOpException(UserOpCodeEnum.FILE_NOT_EXIST);
        } catch (FileHasVirusException e) {
            throw new UserOpException(UserOpCodeEnum.FILE_HAS_VIRUS);
        }
        return ResponseResult.okResult();
    }

    @GetMapping("/{id}")
    public ResponseResult<DrawioGetVO> get(@PathVariable @NotNull @Min(1) Long id) {
        DrawioGetVO drawio = drawioService.get(id);
        if (drawio == null) {
            throw new UserOpException(UserOpCodeEnum.METHOD_ARGUMENT_NOT_VALID);
        }

        return ResponseResult.okResult(drawio);
    }

    @GetMapping("/list")
    public ResponseResult<PageVO<DrawioListVO>> list(Integer pageNum, Integer pageSize, String name) {
        PageVO<DrawioListVO> pageVO = drawioService.list(
                Optional.ofNullable(pageNum).orElse(1),
                Optional.ofNullable(pageSize).orElse(10),
                name);

        return ResponseResult.okResult(pageVO);
    }
}
