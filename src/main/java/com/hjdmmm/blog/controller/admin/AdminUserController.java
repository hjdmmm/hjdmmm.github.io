package com.hjdmmm.blog.controller.admin;

import com.hjdmmm.blog.annotation.Auth;
import com.hjdmmm.blog.domain.ResponseResult;
import com.hjdmmm.blog.domain.model.AddUserModel;
import com.hjdmmm.blog.domain.model.ChangeStatusModel;
import com.hjdmmm.blog.domain.model.EditUserModel;
import com.hjdmmm.blog.domain.vo.PageVO;
import com.hjdmmm.blog.domain.vo.UserGetVO;
import com.hjdmmm.blog.domain.vo.UserListVO;
import com.hjdmmm.blog.enums.AuthTypeEnum;
import com.hjdmmm.blog.enums.UserOpCodeEnum;
import com.hjdmmm.blog.exception.UserNameExistException;
import com.hjdmmm.blog.exception.UserOpException;
import com.hjdmmm.blog.service.UserService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/admin/user")
@Auth(AuthTypeEnum.AUTHENTICATED)
@Validated
public class AdminUserController {
    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("")
    public ResponseResult<Void> add(@RequestBody AddUserModel model) {
        try {
            userService.add(model.getUserName(), model.getNickName(), model.getPassword(), model.getType(), model.getStatus());
        } catch (UserNameExistException e) {
            throw new UserOpException(UserOpCodeEnum.USERNAME_EXIST);
        }

        return ResponseResult.okResult();
    }

    @DeleteMapping("/{id}")
    public ResponseResult<Void> delete(@PathVariable @NotNull @Min(1) Long id) {
        userService.delete(id);
        return ResponseResult.okResult();
    }

    @PutMapping("")
    public ResponseResult<Void> edit(@RequestBody EditUserModel model) {
        try {
            userService.edit(model.getId(), model.getUserName(), model.getNickName(), model.getPassword(), model.getType(), model.getStatus());
        } catch (UserNameExistException e) {
            throw new UserOpException(UserOpCodeEnum.USERNAME_EXIST);
        }

        return ResponseResult.okResult();
    }

    @PutMapping("/status")
    public ResponseResult<Void> changeStatus(@RequestBody ChangeStatusModel model) {
        userService.changeStatus(model.getId(), model.getStatus());
        return ResponseResult.okResult();
    }

    @GetMapping("/{id}")
    public ResponseResult<UserGetVO> get(@PathVariable @NotNull @Min(1) Long id) {
        UserGetVO userGetVO = userService.get(id);
        if (userGetVO == null) {
            throw new UserOpException(UserOpCodeEnum.METHOD_ARGUMENT_NOT_VALID);
        }

        return ResponseResult.okResult(userGetVO);
    }

    @GetMapping("/list")
    public ResponseResult<PageVO<UserListVO>> list(Integer pageNum, Integer pageSize, String userName, Integer type, Integer status) {
        PageVO<UserListVO> pageVO = userService.list(
                Optional.ofNullable(pageNum).orElse(1),
                Optional.ofNullable(pageSize).orElse(10),
                userName, type, status);
        return ResponseResult.okResult(pageVO);
    }
}
