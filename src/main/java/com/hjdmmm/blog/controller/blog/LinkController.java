package com.hjdmmm.blog.controller.blog;

import com.hjdmmm.blog.domain.ResponseResult;
import com.hjdmmm.blog.domain.vo.BlogLinkListVO;
import com.hjdmmm.blog.service.LinkService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/link")
@Validated
public class LinkController {
    private final LinkService linkService;

    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    @GetMapping("/list")
    public ResponseResult<List<BlogLinkListVO>> list() {
        List<BlogLinkListVO> blogLinkListVOList = linkService.blogListAll();
        return ResponseResult.okResult(blogLinkListVOList);
    }
}
