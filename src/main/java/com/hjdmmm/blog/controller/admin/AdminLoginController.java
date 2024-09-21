package com.hjdmmm.blog.controller.admin;

import com.hjdmmm.blog.annotation.Auth;
import com.hjdmmm.blog.domain.ResponseResult;
import com.hjdmmm.blog.domain.model.AdminLoginUserModel;
import com.hjdmmm.blog.domain.vo.LoginVO;
import com.hjdmmm.blog.enums.AuthTypeEnum;
import com.hjdmmm.blog.enums.UserOpCodeEnum;
import com.hjdmmm.blog.exception.UserOpException;
import com.hjdmmm.blog.service.LoginService;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class AdminLoginController {
    private final LoginService loginService;

    public AdminLoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/system/login")
    @Auth(AuthTypeEnum.ANONYMOUS)
    public ResponseResult<LoginVO> login(@RequestBody AdminLoginUserModel adminLoginUserModel, @RequestHeader HttpHeaders httpHeaders) {
        if (!StringUtils.hasText(adminLoginUserModel.getUserName())) {
            throw new UserOpException(UserOpCodeEnum.REQUIRE_USERNAME);
        }
        LoginVO loginVO = loginService.login(adminLoginUserModel.getUserName(), adminLoginUserModel.getPassword(), httpHeaders);
        return ResponseResult.okResult(loginVO);
    }

    @PostMapping("/system/logout")
    @Auth(AuthTypeEnum.AUTHENTICATED)
    public ResponseResult<Void> logout() {
        loginService.logout();
        return ResponseResult.okResult();
    }
}
