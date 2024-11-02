package com.hjdmmm.blog.controller.blog;

import com.hjdmmm.blog.domain.ResponseResult;
import com.hjdmmm.blog.domain.model.AddCommentModel;
import com.hjdmmm.blog.domain.vo.BlogCommentVO;
import com.hjdmmm.blog.domain.vo.PageVO;
import com.hjdmmm.blog.enums.UserOpCodeEnum;
import com.hjdmmm.blog.exception.IllegalArticleCommentException;
import com.hjdmmm.blog.exception.UserOpException;
import com.hjdmmm.blog.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/comment")
@Validated
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    private static String getRealRemoteIp(HttpHeaders httpHeaders, String directRemoteIp) {
        String ip;

        ip = httpHeaders.getFirst("X-forwarded-for");
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        ip = httpHeaders.getFirst("Proxy-Client-IP");
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        ip = httpHeaders.getFirst("X-Forwarded-For");
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        ip = httpHeaders.getFirst("WL-Proxy-Client-IP");
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        ip = httpHeaders.getFirst("X-Real-IP");
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        ip = directRemoteIp;
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        return null;
    }

    @GetMapping("/article")
    public ResponseResult<PageVO<BlogCommentVO>> articleComments(Integer pageNum, @NotNull @Min(1) Long articleId) {
        PageVO<BlogCommentVO> result = commentService.getArticleComments(
                Optional.ofNullable(pageNum).orElse(1),
                10,
                articleId);
        return ResponseResult.okResult(result);
    }

    @PostMapping("")
    public ResponseResult<Void> add(@RequestBody AddCommentModel model, @RequestHeader HttpHeaders httpHeaders, HttpServletRequest request) {
        String directRemoteIp = request.getRemoteAddr();
        String realRemoteIp = getRealRemoteIp(httpHeaders, directRemoteIp);
        if (!StringUtils.hasText(realRemoteIp)) {
            throw new UserOpException(UserOpCodeEnum.IP_NOT_FOUND);
        }

        if (!commentService.canComment(realRemoteIp)) {
            throw new UserOpException(UserOpCodeEnum.TOO_MANY_COMMENTS);
        }

        try {
            commentService.add(model.toComment());
        } catch (IllegalArticleCommentException e) {
            throw new UserOpException(UserOpCodeEnum.ILLEGAL_ARTICLE_COMMENT);
        }

        return ResponseResult.okResult();
    }
}
