package com.hjdmmm.blog.controller;

import com.hjdmmm.blog.annotation.Auth;
import com.hjdmmm.blog.context.UserIdHolder;
import com.hjdmmm.blog.domain.ResponseResult;
import com.hjdmmm.blog.domain.dto.UserDTO;
import com.hjdmmm.blog.domain.model.AddOrEditUserModel;
import com.hjdmmm.blog.domain.model.ChangeStatusModel;
import com.hjdmmm.blog.domain.vo.PageVO;
import com.hjdmmm.blog.enums.AuthTypeEnum;
import com.hjdmmm.blog.enums.UserOpCodeEnum;
import com.hjdmmm.blog.service.UserService;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@Auth(AuthTypeEnum.AUTHENTICATED)
@RestController
@RequestMapping("/user")
@Slf4j
@Validated
public class UserController {
    private final UserService userService;

    private final UserIdHolder userIdHolder;

    @PostMapping("")
    public ResponseResult<Void> add(@RequestBody AddOrEditUserModel model) {
        boolean usernameExist;
        try {
            usernameExist = !userService.add(model, userIdHolder.get());
        } catch (Exception e) {
            log.error("系统错误", e);
            return ResponseResult.errorResult(UserOpCodeEnum.SERVER_ERROR);
        }

        if (usernameExist) {
            log.warn("用户名重复: username={}", model.username(), new IllegalArgumentException());
            return ResponseResult.errorResult(UserOpCodeEnum.USERNAME_EXIST);
        }

        return ResponseResult.okResult();
    }

    @DeleteMapping("/{id}")
    public ResponseResult<Void> delete(@PathVariable @Min(1) long id) {
        try {
            userService.delete(id, userIdHolder.get());
        } catch (Exception e) {
            log.error("系统错误", e);
            return ResponseResult.errorResult(UserOpCodeEnum.SERVER_ERROR);
        }
        return ResponseResult.okResult();
    }

    @PutMapping("/{id}")
    public ResponseResult<Void> edit(@PathVariable @Min(1) long id, @RequestBody AddOrEditUserModel model) {
        boolean usernameExist;
        try {
            usernameExist = !userService.edit(id, model, userIdHolder.get());
        } catch (Exception e) {
            log.error("系统错误", e);
            return ResponseResult.errorResult(UserOpCodeEnum.SERVER_ERROR);
        }

        if (usernameExist) {
            log.warn("用户名重复: username={}", model.username(), new IllegalArgumentException());
            return ResponseResult.errorResult(UserOpCodeEnum.USERNAME_EXIST);
        }

        return ResponseResult.okResult();
    }

    @PutMapping("/status/{id}")
    public ResponseResult<Void> changeStatus(@PathVariable @Min(1) long id, @RequestBody ChangeStatusModel model) {
        try {
            userService.changeStatus(id, model.status(), userIdHolder.get());
        } catch (Exception e) {
            log.error("系统错误", e);
            return ResponseResult.errorResult(UserOpCodeEnum.SERVER_ERROR);
        }
        return ResponseResult.okResult();
    }

    @GetMapping("/{id}")
    public ResponseResult<UserDTO> get(@PathVariable @Min(1) long id) {
        UserDTO userDTO;
        try {
            userDTO = userService.get(id);
        } catch (Exception e) {
            log.error("系统错误", e);
            return ResponseResult.errorResult(UserOpCodeEnum.SERVER_ERROR);
        }

        if (userDTO == null) {
            log.warn("用户不存在: id={}", id, new IllegalArgumentException());
            return ResponseResult.errorResult(UserOpCodeEnum.NOT_FOUND);
        }

        return ResponseResult.okResult(userDTO);
    }

    @GetMapping("/list")
    public ResponseResult<PageVO<UserDTO>> list(
        @RequestParam("pageNum") int pageNum,
        @RequestParam("pageSize") int pageSize,
        @RequestParam(value = "username", required = false) String username,
        @RequestParam(value = "type", required = false) Integer type,
        @RequestParam(value = "status", required = false) Integer status
    ) {
        PageVO<UserDTO> pageVO;
        try {
            pageVO = userService.list(pageNum, pageSize, username, type, status);
        } catch (Exception e) {
            log.error("系统错误", e);
            return ResponseResult.errorResult(UserOpCodeEnum.SERVER_ERROR);
        }

        return ResponseResult.okResult(pageVO);
    }
}
