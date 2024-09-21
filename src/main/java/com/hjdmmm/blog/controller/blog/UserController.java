package com.hjdmmm.blog.controller.blog;

import com.hjdmmm.blog.context.UserIdHolder;
import com.hjdmmm.blog.domain.ResponseResult;
import com.hjdmmm.blog.domain.vo.BlogUserInfoVO;
import com.hjdmmm.blog.enums.UserOpCodeEnum;
import com.hjdmmm.blog.exception.UserOpException;
import com.hjdmmm.blog.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {
    private final UserService userService;

    private final UserIdHolder userIdHolder;

    public UserController(UserService userService, UserIdHolder userIdHolder) {
        this.userService = userService;
        this.userIdHolder = userIdHolder;
    }

    @GetMapping("/info")
    public ResponseResult<BlogUserInfoVO> info() {
        if (!userIdHolder.exist()) {
            throw new UserOpException(UserOpCodeEnum.USER_NOT_LOGIN_ERROR);
        }

        long id = userIdHolder.get();
        BlogUserInfoVO blogUserInfoVO = userService.getUserInfo(id);
        return ResponseResult.okResult(blogUserInfoVO);
    }
}
