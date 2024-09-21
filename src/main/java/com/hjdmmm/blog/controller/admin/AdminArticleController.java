package com.hjdmmm.blog.controller.admin;

import com.hjdmmm.blog.annotation.Auth;
import com.hjdmmm.blog.domain.ResponseResult;
import com.hjdmmm.blog.domain.model.AddArticleModel;
import com.hjdmmm.blog.domain.model.EditArticleModel;
import com.hjdmmm.blog.domain.vo.ArticleListVO;
import com.hjdmmm.blog.domain.vo.ArticleVO;
import com.hjdmmm.blog.domain.vo.PageVO;
import com.hjdmmm.blog.enums.AuthTypeEnum;
import com.hjdmmm.blog.enums.UserOpCodeEnum;
import com.hjdmmm.blog.exception.UserOpException;
import com.hjdmmm.blog.service.ArticleService;
import com.hjdmmm.blog.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@RestController
@RequestMapping("/admin/article")
@Auth(AuthTypeEnum.AUTHENTICATED)
@Validated
public class AdminArticleController {
    private final ArticleService articleService;

    public AdminArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @PostMapping("")
    public ResponseResult<Void> add(@RequestBody AddArticleModel addArticleModel) {
        articleService.add(addArticleModel.toArticle(), CollectionUtils.emptyIfNull(addArticleModel.getTags()));
        return ResponseResult.okResult();
    }

    @DeleteMapping("/{id}")
    public ResponseResult<Void> delete(@PathVariable @NotNull @Min(1) Long id) {
        articleService.delete(id);
        return ResponseResult.okResult();
    }

    @PutMapping("")
    public ResponseResult<Void> edit(@RequestBody EditArticleModel editArticleModel) {
        articleService.edit(editArticleModel.toArticle(), CollectionUtils.emptyIfNull(editArticleModel.getTags()));
        return ResponseResult.okResult();
    }

    @GetMapping("/{id}")
    public ResponseResult<ArticleVO> get(@PathVariable @NotNull @Min(1) Long id) {
        ArticleVO articleVO = articleService.get(id);
        if (articleVO == null) {
            throw new UserOpException(UserOpCodeEnum.METHOD_ARGUMENT_NOT_VALID);
        }

        return ResponseResult.okResult(articleVO);
    }

    @GetMapping("/list")
    public ResponseResult<PageVO<ArticleListVO>> list(Integer pageNum, Integer pageSize, String title, String summary) {
        PageVO<ArticleListVO> pageVO = articleService.list(
                Optional.ofNullable(pageNum).orElse(1),
                Optional.ofNullable(pageSize).orElse(10),
                title, summary);
        return ResponseResult.okResult(pageVO);
    }
}
