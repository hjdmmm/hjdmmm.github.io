package com.hjdmmm.blog.controller;

import com.hjdmmm.blog.annotation.Auth;
import com.hjdmmm.blog.context.UserIdHolder;
import com.hjdmmm.blog.domain.ResponseResult;
import com.hjdmmm.blog.domain.entity.Tag;
import com.hjdmmm.blog.domain.model.AddOrEditTagModel;
import com.hjdmmm.blog.domain.vo.PageVO;
import com.hjdmmm.blog.enums.AuthTypeEnum;
import com.hjdmmm.blog.enums.UserOpCodeEnum;
import com.hjdmmm.blog.service.TagService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@Auth(AuthTypeEnum.AUTHENTICATED)
@RestController
@RequestMapping("/tag")
@Slf4j
@Validated
public class TagController {
    private final TagService tagService;

    private final UserIdHolder userIdHolder;

    @PostMapping("")
    public ResponseResult<Void> add(@RequestBody @NotNull @Valid AddOrEditTagModel model) {
        try {
            tagService.add(model, userIdHolder.get());
        } catch (Exception e) {
            log.error("系统错误", e);
            return ResponseResult.errorResult(UserOpCodeEnum.SERVER_ERROR);
        }
        return ResponseResult.okResult();
    }

    @DeleteMapping("/{id}")
    public ResponseResult<Void> delete(@PathVariable @NotNull @Min(1) Long id) {
        try {
            tagService.delete(id, userIdHolder.get());
        } catch (Exception e) {
            log.error("系统错误", e);
            return ResponseResult.errorResult(UserOpCodeEnum.SERVER_ERROR);
        }
        return ResponseResult.okResult();
    }

    @PutMapping("/{id}")
    public ResponseResult<Void> edit(@PathVariable @NotNull @Min(1) Long id, @RequestBody @NotNull @Valid AddOrEditTagModel model) {
        try {
            tagService.edit(id, model, userIdHolder.get());
        } catch (Exception e) {
            log.error("系统错误", e);
            return ResponseResult.errorResult(UserOpCodeEnum.SERVER_ERROR);
        }
        return ResponseResult.okResult();
    }

    @GetMapping("/{id}")
    public ResponseResult<Tag> get(@PathVariable @NotNull @Min(1) Long id) {
        Tag tag;
        try {
            tag = tagService.get(id);
        } catch (Exception e) {
            log.error("系统错误", e);
            return ResponseResult.errorResult(UserOpCodeEnum.SERVER_ERROR);
        }

        if (tag == null) {
            log.warn("标签不存在: id={}", id, new IllegalArgumentException());
            return ResponseResult.errorResult(UserOpCodeEnum.NOT_FOUND);
        }

        return ResponseResult.okResult(tag);
    }

    @GetMapping("/list")
    public ResponseResult<PageVO<Tag>> list(
        @RequestParam("pageNum") @NotNull @Min(1) Integer pageNum,
        @RequestParam("pageSize") @NotNull @Min(1) Integer pageSize,
        @RequestParam(value = "keyword", required = false) String keyword
    ) {
        PageVO<Tag> pageVO;
        try {
            pageVO = tagService.list(pageNum, pageSize, keyword);
        } catch (Exception e) {
            log.error("系统错误", e);
            return ResponseResult.errorResult(UserOpCodeEnum.SERVER_ERROR);
        }
        return ResponseResult.okResult(pageVO);
    }
}
