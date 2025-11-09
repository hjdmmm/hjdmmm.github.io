package com.hjdmmm.blog.controller;

import com.hjdmmm.blog.context.UserIdHolder;
import com.hjdmmm.blog.domain.ResponseResult;
import com.hjdmmm.blog.domain.model.AddCommentModel;
import com.hjdmmm.blog.domain.vo.BlogCommentVO;
import com.hjdmmm.blog.domain.vo.PageVO;
import com.hjdmmm.blog.enums.UserOpCodeEnum;
import com.hjdmmm.blog.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/comment")
@Slf4j
@Validated
public class CommentController {
    private final CommentService commentService;

    private final UserIdHolder userIdHolder;

    private static String getRealRemoteIp(HttpHeaders httpHeaders, String directRemoteIp) {
        String ip;

        ip = httpHeaders.getFirst("X-forwarded-for");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        ip = httpHeaders.getFirst("Proxy-Client-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        ip = httpHeaders.getFirst("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        ip = httpHeaders.getFirst("WL-Proxy-Client-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        ip = httpHeaders.getFirst("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        ip = directRemoteIp;
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        return null;
    }

    @PostMapping("")
    public ResponseResult<Void> add(
        @RequestBody AddCommentModel model,
        @RequestHeader HttpHeaders httpHeaders,
        HttpServletRequest request
    ) {
        String directRemoteIp = request.getRemoteAddr();
        String realRemoteIp = getRealRemoteIp(httpHeaders, directRemoteIp);
        if (!StringUtils.hasText(realRemoteIp)) {
            log.warn("IP不存在", new IllegalArgumentException());
            return ResponseResult.errorResult(UserOpCodeEnum.IP_NOT_FOUND);
        }

        boolean canComment;
        try {
            canComment = commentService.canComment(realRemoteIp);
        } catch (Exception e) {
            log.error("系统错误", e);
            return ResponseResult.errorResult(UserOpCodeEnum.SERVER_ERROR);
        }

        if (!canComment) {
            log.warn("IP短时间内评论过多: ip={}", realRemoteIp, new IllegalArgumentException());
            return ResponseResult.errorResult(UserOpCodeEnum.TOO_MANY_COMMENTS);
        }

        long userId = 0L;
        if (userIdHolder.exist()) {
            userId = userIdHolder.get();
        }

        try {
            commentService.add(model, userId);
        } catch (Exception e) {
            log.error("系统错误", e);
            return ResponseResult.errorResult(UserOpCodeEnum.SERVER_ERROR);
        }

        return ResponseResult.okResult();
    }

    @GetMapping("/article/{articleId}")
    public ResponseResult<PageVO<BlogCommentVO>> getArticleComments(
        @PathVariable @Min(1) long articleId,
        @RequestParam("pageNum") @Min(1) int pageNum
    ) {
        PageVO<BlogCommentVO> result;
        try {
            result = commentService.getArticleComments(pageNum, 10, articleId);
        } catch (Exception e) {
            log.error("系统错误", e);
            return ResponseResult.errorResult(UserOpCodeEnum.SERVER_ERROR);
        }
        return ResponseResult.okResult(result);
    }
}
