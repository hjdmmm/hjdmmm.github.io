package com.hjdmmm.blog.controller.admin;

import com.hjdmmm.blog.annotation.Auth;
import com.hjdmmm.blog.domain.ResponseResult;
import com.hjdmmm.blog.domain.entity.Category;
import com.hjdmmm.blog.domain.model.AddCategoryModel;
import com.hjdmmm.blog.domain.model.ChangeStatusModel;
import com.hjdmmm.blog.domain.model.EditCategoryModel;
import com.hjdmmm.blog.domain.vo.CategoryListAllVO;
import com.hjdmmm.blog.domain.vo.PageVO;
import com.hjdmmm.blog.enums.AuthTypeEnum;
import com.hjdmmm.blog.enums.UserOpCodeEnum;
import com.hjdmmm.blog.exception.UserOpException;
import com.hjdmmm.blog.service.CategoryService;
import com.hjdmmm.blog.util.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin/category")
@Auth(AuthTypeEnum.AUTHENTICATED)
@Validated
public class AdminCategoryController {
    private final CategoryService categoryService;

    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("")
    public ResponseResult<Void> add(@RequestBody AddCategoryModel model) {
        Category category = BeanUtils.copyBean(model, Category.class);
        categoryService.add(category);
        return ResponseResult.okResult();
    }

    @DeleteMapping("/{id}")
    public ResponseResult<Void> delete(@PathVariable @NotNull @Min(1) Long id) {
        categoryService.delete(id);
        return ResponseResult.okResult();
    }

    @PutMapping("")
    public ResponseResult<Void> edit(@RequestBody EditCategoryModel model) {
        Category category = BeanUtils.copyBean(model, Category.class);
        categoryService.edit(category);
        return ResponseResult.okResult();
    }

    @PutMapping("/status")
    public ResponseResult<Void> changeStatus(@RequestBody ChangeStatusModel model) {
        categoryService.changeStatus(model.getId(), model.getStatus());
        return ResponseResult.okResult();
    }

    @GetMapping("/{id}")
    public ResponseResult<Category> get(@PathVariable @NotNull @Min(1) Long id) {
        Category category = categoryService.get(id);
        if (category == null) {
            throw new UserOpException(UserOpCodeEnum.METHOD_ARGUMENT_NOT_VALID);
        }

        return ResponseResult.okResult(category);
    }

    @GetMapping("/list")
    public ResponseResult<PageVO<CategoryListAllVO>> list(Integer pageNum, Integer pageSize, String name, Integer status) {
        PageVO<CategoryListAllVO> pageVO = categoryService.list(
                Optional.ofNullable(pageNum).orElse(1),
                Optional.ofNullable(pageSize).orElse(10),
                status, name);
        return ResponseResult.okResult(pageVO);
    }

    @GetMapping("/listAll")
    public ResponseResult<List<CategoryListAllVO>> listAll() {
        List<CategoryListAllVO> categoryListAllVOList = categoryService.listAll();
        return ResponseResult.okResult(categoryListAllVOList);
    }
}
