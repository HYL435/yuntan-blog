package com.yuntan.article.controller.admin;

import com.yuntan.article.domain.dto.admin.CategoryDTO;
import com.yuntan.article.domain.dto.admin.CategoryUpdateDTO;
import com.yuntan.article.domain.vo.admin.CategoryContentVO;
import com.yuntan.article.domain.vo.admin.CategoryVO;
import com.yuntan.article.service.ICategoryService;
import com.yuntan.common.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/admin/categories")
@RestController
@RequiredArgsConstructor
@Tag(description = "后台分类接口", name = "分类")
public class AdminCategoryController {

    private final ICategoryService categoryService;


    @Operation(summary = "获取所有分类名称")
    @GetMapping
    public Result<List<CategoryVO>> list() {

        log.info("获取所有分类名称");

        List<CategoryVO> list = categoryService.getAdminCategory();

        return Result.ok(list);
    }

    @Operation(summary = "添加分类")
    @PostMapping
    public Result<Void> addCategory(@RequestBody CategoryDTO categoryDTO) {

        log.info("添加分类 {}" , categoryDTO);

        categoryService.addCategory(categoryDTO);

        return Result.ok();
    }

    @Operation(summary = "更新分类")
    @PutMapping
    public Result<Void> updateCategory(@RequestBody CategoryUpdateDTO categoryUpdateDTO) {

        log.info("更新分类，{}", categoryUpdateDTO);

        categoryService.updateCategory(categoryUpdateDTO);

        return Result.ok();
    }

    @Operation(summary = "获取分类内容")
    @GetMapping("/{id}")
    public Result<CategoryContentVO> getCategoryById(@PathVariable Long id) {
        log.info("获取分类内容 {}",  id);

        CategoryContentVO categoryContentVO = categoryService.getCategoryById(id);

        return Result.ok(categoryContentVO);
    }


    @Operation(summary = "修改分类状态")
    @PutMapping("status")
    public Result<Void> changeCategoryStatus(Long id, Integer status) {
        log.info("修改分类状态 {}， 状态为 {}",  id,  status);

        categoryService.changeCategoryStatus(id,  status);

        return Result.ok();
    }

    @Operation(summary = "删除分类")
    @PutMapping("deleted/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        log.info("删除分类 {}",  id);

        categoryService.removeById(id);

        return Result.ok();
    }

}
