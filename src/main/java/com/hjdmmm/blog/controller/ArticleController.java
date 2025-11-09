package com.hjdmmm.blog.controller;

import com.hjdmmm.blog.annotation.Auth;
import com.hjdmmm.blog.context.UserIdHolder;
import com.hjdmmm.blog.domain.ResponseResult;
import com.hjdmmm.blog.domain.dto.ArticleDTO;
import com.hjdmmm.blog.domain.model.*;
import com.hjdmmm.blog.domain.vo.ArticleDetailVO;
import com.hjdmmm.blog.domain.vo.ArticleNodeVO;
import com.hjdmmm.blog.domain.vo.PageVO;
import com.hjdmmm.blog.enums.AuthTypeEnum;
import com.hjdmmm.blog.enums.UserOpCodeEnum;
import com.hjdmmm.blog.service.ArticleService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/article")
@Slf4j
@Validated
public class ArticleController {
    private final ArticleService articleService;

    private final UserIdHolder userIdHolder;

    @Auth(AuthTypeEnum.AUTHENTICATED)
    @PostMapping("")
    public ResponseResult<Long> add(@RequestBody @NotNull @Valid AddArticleModel model) {
        long id;
        try {
            id = articleService.add(model, userIdHolder.get());
        } catch (Exception e) {
            log.error("系统错误", e);
            return ResponseResult.errorResult(UserOpCodeEnum.SERVER_ERROR);
        }
        return ResponseResult.okResult(id);
    }

    @Auth(AuthTypeEnum.AUTHENTICATED)
    @DeleteMapping("/{id}")
    public ResponseResult<Void> delete(@PathVariable("id") @NotNull @Min(1) Long id) {
        try {
            articleService.delete(id, userIdHolder.get());
        } catch (Exception e) {
            log.error("系统错误", e);
            return ResponseResult.errorResult(UserOpCodeEnum.SERVER_ERROR);
        }
        return ResponseResult.okResult();
    }

    @Auth(AuthTypeEnum.AUTHENTICATED)
    @PutMapping("/{id}/title")
    public ResponseResult<Void> updateTitle(@PathVariable("id") @NotNull @Min(1) Long id, @RequestBody @NotNull @Valid UpdateArticleTitleModel model) {
        try {
            articleService.updateTitle(id, model.title(), userIdHolder.get());
        } catch (Exception e) {
            log.error("系统错误", e);
            return ResponseResult.errorResult(UserOpCodeEnum.SERVER_ERROR);
        }
        return ResponseResult.okResult();
    }

    @Auth(AuthTypeEnum.AUTHENTICATED)
    @PutMapping("/{id}/content")
    public ResponseResult<Void> updateContent(@PathVariable("id") @NotNull @Min(1) Long id, @RequestBody @NotNull @Valid UpdateArticleContentModel model) {
        try {
            articleService.updateContent(id, model.content(), userIdHolder.get());
        } catch (Exception e) {
            log.error("系统错误", e);
            return ResponseResult.errorResult(UserOpCodeEnum.SERVER_ERROR);
        }
        return ResponseResult.okResult();
    }

    @Auth(AuthTypeEnum.AUTHENTICATED)
    @PutMapping("/{id}/parent")
    public ResponseResult<Void> updateParent(@PathVariable("id") @NotNull @Min(1) Long id, @RequestBody @NotNull @Valid UpdateArticleParentModel model) {
        try {
            articleService.updateParent(id, model.parentId(), userIdHolder.get());
        } catch (Exception e) {
            log.error("系统错误", e);
            return ResponseResult.errorResult(UserOpCodeEnum.SERVER_ERROR);
        }
        return ResponseResult.okResult();
    }

    @Auth(AuthTypeEnum.AUTHENTICATED)
    @PutMapping("/{id}/tags")
    public ResponseResult<Void> updateParent(@PathVariable("id") @NotNull @Min(1) Long id, @RequestBody @NotNull @Valid UpdateArticleTagsModel model) {
        try {
            articleService.updateTagIds(id, model.tagIds(), userIdHolder.get());
        } catch (Exception e) {
            log.error("系统错误", e);
            return ResponseResult.errorResult(UserOpCodeEnum.SERVER_ERROR);
        }
        return ResponseResult.okResult();
    }

    @PutMapping("/{id}/viewCount")
    public ResponseResult<Void> increaseViewCount(@PathVariable("id") @NotNull @Min(1) Long id) {
        try {
            articleService.increaseViewCount(id);
        } catch (Exception e) {
            log.error("系统错误", e);
            return ResponseResult.errorResult(UserOpCodeEnum.SERVER_ERROR);
        }

        return ResponseResult.okResult();
    }

    @GetMapping("/hotArticleIds")
    public ResponseResult<List<Long>> getHotArticleIds() {
        List<Long> articleIds;
        try {
            articleIds = articleService.getHotArticleIds();
        } catch (Exception e) {
            log.error("系统错误", e);
            return ResponseResult.errorResult(UserOpCodeEnum.SERVER_ERROR);
        }

        return ResponseResult.okResult(articleIds);
    }

    @GetMapping("/latestArticleIds")
    public ResponseResult<List<Long>> getLatestArticleIds() {
        List<Long> articleIds;
        try {
            articleIds = articleService.getLatestArticleIds();
        } catch (Exception e) {
            log.error("系统错误", e);
            return ResponseResult.errorResult(UserOpCodeEnum.SERVER_ERROR);
        }

        return ResponseResult.okResult(articleIds);
    }

    @GetMapping("/categoryArticleIds")
    public ResponseResult<PageVO<Long>> listCategoryArticleIds(
        @RequestParam("pageNum") @NotNull @Min(1) Integer pageNum,
        @RequestParam("pageSize") @NotNull @Min(1) Integer pageSize
    ) {
        PageVO<Long> pageVO;
        try {
            pageVO = articleService.listCategoryArticleIds(pageNum, pageSize);
        } catch (Exception e) {
            log.error("系统错误", e);
            return ResponseResult.errorResult(UserOpCodeEnum.SERVER_ERROR);
        }

        return ResponseResult.okResult(pageVO);
    }

    @GetMapping("/list")
    public ResponseResult<PageVO<ArticleDTO>> list(
        @RequestParam("pageNum") @NotNull @Min(1) Integer pageNum,
        @RequestParam("pageSize") @NotNull @Min(1) Integer pageSize,
        @RequestParam(value = "title", required = false) String title,
        @RequestParam(value = "summary", required = false) String summary
    ) {
        PageVO<ArticleDTO> pageVO;
        try {
            pageVO = articleService.list(pageNum, pageSize, title, summary);
        } catch (Exception e) {
            log.error("系统错误", e);
            return ResponseResult.errorResult(UserOpCodeEnum.SERVER_ERROR);
        }

        return ResponseResult.okResult(pageVO);
    }

    @PostMapping("/articleNodes")
    public ResponseResult<List<ArticleNodeVO>> getArticleNodes(@RequestBody @NotEmpty @Valid List<@NotNull @Min(1) Long> articleIds) {
        Long userId = userIdHolder.get();
        List<ArticleNodeVO> articleNodeVOList;
        try {
            articleNodeVOList = articleService.getArticleNodes(articleIds, userId);
        } catch (Exception e) {
            log.error("系统错误", e);
            return ResponseResult.errorResult(UserOpCodeEnum.SERVER_ERROR);
        }

        return ResponseResult.okResult(articleNodeVOList);
    }

    @GetMapping("/{id}")
    public ResponseResult<ArticleDetailVO> getArticleDetail(@PathVariable("id") @NotNull @Min(1) Long id) {
        Long userId = userIdHolder.get();
        ArticleDetailVO articleDetailVO;
        try {
            articleDetailVO = articleService.getArticleDetail(id, userId);
        } catch (Exception e) {
            log.error("系统错误", e);
            return ResponseResult.errorResult(UserOpCodeEnum.SERVER_ERROR);
        }

        if (articleDetailVO == null) {
            return ResponseResult.errorResult(UserOpCodeEnum.NOT_FOUND);
        }
        return ResponseResult.okResult(articleDetailVO);
    }
}
