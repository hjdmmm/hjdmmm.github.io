package com.hjdmmm.blog.controller.admin;

import com.hjdmmm.blog.annotation.Auth;
import com.hjdmmm.blog.domain.ResponseResult;
import com.hjdmmm.blog.domain.entity.Link;
import com.hjdmmm.blog.domain.model.AddLinkModel;
import com.hjdmmm.blog.domain.model.ChangeStatusModel;
import com.hjdmmm.blog.domain.model.EditLinkModel;
import com.hjdmmm.blog.domain.vo.PageVO;
import com.hjdmmm.blog.enums.AuthTypeEnum;
import com.hjdmmm.blog.enums.UserOpCodeEnum;
import com.hjdmmm.blog.exception.UserOpException;
import com.hjdmmm.blog.service.LinkService;
import com.hjdmmm.blog.util.BeanUtils;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/admin/link")
@Auth(AuthTypeEnum.AUTHENTICATED)
@Validated
public class AdminLinkController {
    private final LinkService linkService;

    public AdminLinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    @PostMapping("")
    public ResponseResult<Void> add(@RequestBody AddLinkModel model) {
        Link link = BeanUtils.copyBean(model, Link.class);
        linkService.add(link);
        return ResponseResult.okResult();
    }

    @DeleteMapping("/{id}")
    public ResponseResult<Void> delete(@PathVariable @NotNull @Min(1) Long id) {
        linkService.delete(id);
        return ResponseResult.okResult();
    }

    @PutMapping("")
    public ResponseResult<Void> edit(@RequestBody EditLinkModel model) {
        Link link = BeanUtils.copyBean(model, Link.class);
        linkService.edit(link);
        return ResponseResult.okResult();
    }

    @PutMapping("/status")
    public ResponseResult<Void> changeStatus(@RequestBody ChangeStatusModel model) {
        linkService.changeStatus(model.id(), model.status());
        return ResponseResult.okResult();
    }

    @GetMapping("/{id}")
    public ResponseResult<Link> get(@PathVariable @NotNull @Min(1) Long id) {
        Link link = linkService.get(id);
        if (link == null) {
            throw new UserOpException(UserOpCodeEnum.METHOD_ARGUMENT_NOT_VALID);
        }

        return ResponseResult.okResult(link);
    }

    @GetMapping("/list")
    public ResponseResult<PageVO<Link>> list(Integer pageNum, Integer pageSize, String name, Integer status) {
        PageVO<Link> pageVO = linkService.list(
                Optional.ofNullable(pageNum).orElse(1),
                Optional.ofNullable(pageSize).orElse(10),
                name, status);
        return ResponseResult.okResult(pageVO);
    }
}
