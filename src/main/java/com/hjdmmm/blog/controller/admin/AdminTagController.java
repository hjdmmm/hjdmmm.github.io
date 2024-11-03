package com.hjdmmm.blog.controller.admin;

import com.hjdmmm.blog.annotation.Auth;
import com.hjdmmm.blog.domain.ResponseResult;
import com.hjdmmm.blog.domain.entity.Tag;
import com.hjdmmm.blog.domain.model.AddTagModel;
import com.hjdmmm.blog.domain.model.EditTagModel;
import com.hjdmmm.blog.domain.vo.LinkListAllVO;
import com.hjdmmm.blog.domain.vo.PageVO;
import com.hjdmmm.blog.enums.AuthTypeEnum;
import com.hjdmmm.blog.enums.UserOpCodeEnum;
import com.hjdmmm.blog.exception.UserOpException;
import com.hjdmmm.blog.service.TagService;
import com.hjdmmm.blog.util.BeanUtils;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin/tag")
@Auth(AuthTypeEnum.AUTHENTICATED)
@Validated
public class AdminTagController {
    private final TagService tagService;

    public AdminTagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping("")
    public ResponseResult<Void> add(@RequestBody AddTagModel model) {
        Tag tag = BeanUtils.copyBean(model, Tag.class);
        tagService.add(tag);
        return ResponseResult.okResult();
    }

    @DeleteMapping("/{id}")
    public ResponseResult<Void> delete(@PathVariable @NotNull @Min(1) Long id) {
        tagService.delete(id);
        return ResponseResult.okResult();
    }

    @PutMapping("")
    public ResponseResult<Void> edit(@RequestBody EditTagModel model) {
        Tag tag = BeanUtils.copyBean(model, Tag.class);
        tagService.edit(tag);
        return ResponseResult.okResult();
    }

    @GetMapping("/{id}")
    public ResponseResult<Tag> get(@PathVariable @NotNull @Min(1) Long id) {
        Tag tag = tagService.get(id);
        if (tag == null) {
            throw new UserOpException(UserOpCodeEnum.METHOD_ARGUMENT_NOT_VALID);
        }

        return ResponseResult.okResult(tag);
    }

    @GetMapping("/list")
    public ResponseResult<PageVO<Tag>> list(Integer pageNum, Integer pageSize, String name, String remark) {
        PageVO<Tag> pageVO = tagService.list(
                Optional.ofNullable(pageNum).orElse(1),
                Optional.ofNullable(pageSize).orElse(10),
                name, remark);
        return ResponseResult.okResult(pageVO);
    }

    @GetMapping("/listAll")
    public ResponseResult<List<LinkListAllVO>> listAll() {
        List<LinkListAllVO> list = tagService.listAll();
        return ResponseResult.okResult(list);
    }
}
