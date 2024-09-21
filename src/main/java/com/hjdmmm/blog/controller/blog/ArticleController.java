package com.hjdmmm.blog.controller.blog;

import com.hjdmmm.blog.domain.ResponseResult;
import com.hjdmmm.blog.domain.vo.BlogArticleDetailVO;
import com.hjdmmm.blog.domain.vo.BlogArticleListVO;
import com.hjdmmm.blog.domain.vo.BlogArticleVO;
import com.hjdmmm.blog.domain.vo.PageVO;
import com.hjdmmm.blog.enums.UserOpCodeEnum;
import com.hjdmmm.blog.exception.UserOpException;
import com.hjdmmm.blog.service.ArticleService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/article")
@Validated
public class ArticleController {
    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/{id}")
    public ResponseResult<BlogArticleDetailVO> detail(@PathVariable @NotNull @Min(1) Long id) {
        BlogArticleDetailVO blogArticleDetailVO = articleService.getArticleDetail(id);
        if (blogArticleDetailVO == null) {
            throw new UserOpException(UserOpCodeEnum.METHOD_ARGUMENT_NOT_VALID);
        }

        return ResponseResult.okResult(blogArticleDetailVO);
    }

    @GetMapping("/list")
    public ResponseResult<PageVO<BlogArticleListVO>> list(Integer pageNum, Long categoryId) {
        PageVO<BlogArticleListVO> pageVO = articleService.getBlogList(
                Optional.ofNullable(pageNum).orElse(1),
                10,
                categoryId);
        return ResponseResult.okResult(pageVO);
    }

    @GetMapping("/hotArticleList")
    public ResponseResult<List<BlogArticleVO>> hotArticleList() {
        List<BlogArticleVO> blogArticleVOList = articleService.getHotArticleList();
        return ResponseResult.okResult(blogArticleVOList);
    }

    @GetMapping("/latestArticleList")
    public ResponseResult<List<BlogArticleVO>> latestArticleList() {
        List<BlogArticleVO> blogArticleVOList = articleService.getLatestArticleList();
        return ResponseResult.okResult(blogArticleVOList);
    }

    @PutMapping("/viewCount/{id}")
    public ResponseResult<Void> increaseViewCount(@PathVariable @NotNull @Min(1) Long id) {
        articleService.increaseViewCount(id);
        return ResponseResult.okResult();
    }
}
