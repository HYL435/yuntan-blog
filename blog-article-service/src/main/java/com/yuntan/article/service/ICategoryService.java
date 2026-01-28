package com.yuntan.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuntan.article.domain.dto.admin.CategoryDTO;
import com.yuntan.article.domain.dto.admin.CategoryUpdateDTO;
import com.yuntan.article.domain.po.Category;
import com.yuntan.article.domain.vo.admin.CategoryContentVO;
import com.yuntan.article.domain.vo.admin.CategoryVO;

import java.util.List;

public interface ICategoryService extends IService<Category> {

    /**
     * 获取所有分类名称
     */
    List<String> getAllCategoryNames();

    /**
     * 获取所有分类管理端
     */
    List<CategoryVO> getAdminCategory();

    /**
     * 添加分类
     */
    void addCategory(CategoryDTO categoryDTO);

    /**
     * 修改分类
     */
    void updateCategory(CategoryUpdateDTO categoryUpdateDTO);

    /**
     * 根据id获取分类内容
     */
    CategoryContentVO getCategoryById(Long id);

    /**
     * 修改分类状态
     */
    void changeCategoryStatus(Long id, Integer status);
}
