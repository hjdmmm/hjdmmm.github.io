package com.hjdmmm.blog.controller.blog;

import com.hjdmmm.blog.domain.ResponseResult;
import com.hjdmmm.blog.domain.vo.BlogCategoryListVO;
import com.hjdmmm.blog.service.CategoryService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category")
@Validated
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/list")
    public ResponseResult<List<BlogCategoryListVO>> list() {
        List<BlogCategoryListVO> result = categoryService.blogListAll();
        return ResponseResult.okResult(result);
    }
}
