package com.hjdmmm.blog.controller;

import com.hjdmmm.blog.domain.ResponseResult;
import com.hjdmmm.blog.domain.vo.FeedArticleInfoVO;
import com.hjdmmm.blog.domain.vo.PageVO;
import com.hjdmmm.blog.enums.UserOpCodeEnum;
import com.hjdmmm.blog.service.ArticleService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/feed")
@Slf4j
@Validated
public class FeedController {
    private final ArticleService articleService;

    @GetMapping("/articleInfo")
    public ResponseResult<FeedArticleInfoVO> getFeedArticleInfo() {
        List<Long> hotArticleIds = null;
        try {
            hotArticleIds = articleService.getHotArticleIds();
        } catch (Exception e) {
            log.error("获取热门文章错误", e);
        }
        List<Long> latestArticleIds = null;
        try {
            latestArticleIds = articleService.getLatestArticleIds();
        } catch (Exception e) {
            log.error("获取最新文章错误", e);
        }
        PageVO<Long> categoryArticleIdPage = null;
        try {
            categoryArticleIdPage = articleService.listCategoryArticleIds(1, 10);
        } catch (Exception e) {
            log.error("获取文章分类错误", e);
        }
        FeedArticleInfoVO articleInfoVO = FeedArticleInfoVO.builder()
            .hotArticleIds(hotArticleIds)
            .latestArticleIds(latestArticleIds)
            .categoryArticleIdPage(categoryArticleIdPage)
            .build();
        return ResponseResult.okResult(articleInfoVO);
    }
}
