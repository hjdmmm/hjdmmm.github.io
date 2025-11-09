package com.hjdmmm.blog.controller;

import com.hjdmmm.blog.annotation.Auth;
import com.hjdmmm.blog.domain.ResponseResult;
import com.hjdmmm.blog.domain.model.LoginUserModel;
import com.hjdmmm.blog.domain.vo.LoginVO;
import com.hjdmmm.blog.enums.AuthTypeEnum;
import com.hjdmmm.blog.enums.UserOpCodeEnum;
import com.hjdmmm.blog.service.LoginService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@Slf4j
@Validated
public class LoginController {
    private final LoginService loginService;

    @PostMapping("/system/login")
    @Auth(AuthTypeEnum.ANONYMOUS)
    public ResponseResult<LoginVO> login(@RequestBody LoginUserModel loginUserModel, @RequestHeader HttpHeaders httpHeaders) {
        LoginVO loginVO;
        try {
            loginVO = loginService.login(loginUserModel.username(), loginUserModel.password(), httpHeaders);
        } catch (Exception e) {
            log.error("系统错误", e);
            return ResponseResult.errorResult(UserOpCodeEnum.SERVER_ERROR);
        }

        if (loginVO == null) {
            log.warn("用户名或密码错误: username={}", loginUserModel.username());
            return ResponseResult.errorResult(UserOpCodeEnum.NOT_FOUND);
        }

        return ResponseResult.okResult(loginVO);
    }

    @PostMapping("/system/logout")
    @Auth(AuthTypeEnum.AUTHENTICATED)
    public ResponseResult<Void> logout() {
        try {
            loginService.logout();
        } catch (Exception e) {
            log.error("系统错误", e);
            return ResponseResult.errorResult(UserOpCodeEnum.SERVER_ERROR);
        }

        return ResponseResult.okResult();
    }
}
