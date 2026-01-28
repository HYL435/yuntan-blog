package com.yuntan.article.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuntan.article.domain.dto.admin.CategoryDTO;
import com.yuntan.article.domain.dto.admin.CategoryUpdateDTO;
import com.yuntan.article.domain.po.Category;
import com.yuntan.article.domain.vo.admin.CategoryContentVO;
import com.yuntan.article.domain.vo.admin.CategoryVO;
import com.yuntan.article.mapper.CategoryMapper;
import com.yuntan.article.service.ICategoryService;
import com.yuntan.common.constant.StatusConstant;
import com.yuntan.common.utils.BeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

    private final CategoryMapper categoryMapper;

    /**
     * 获取所有分类名称
     */
    @Override
    public List<String> getAllCategoryNames() {

        return categoryMapper.getCategories();

    }

    /**
     * 获取所有分类名称管理端
     */
    @Override
    public List<CategoryVO> getAdminCategory() {

        List<Category> categories = this.list();

        return BeanUtils.copyList(categories, CategoryVO.class);
    }

    /**
     * 添加分类
     */
    @Override
    public void addCategory(CategoryDTO categoryDTO) {

        Category category = BeanUtils.copyBean(categoryDTO, Category.class);

        this.save(category);

    }

    /**
     * 修改分类
     */
    @Override
    public void updateCategory(CategoryUpdateDTO categoryUpdateDTO) {

        Category category = BeanUtils.copyBean(categoryUpdateDTO, Category.class);

        this.updateById(category);
    }

    /**
     * 根据id获取分类内容
     */
    @Override
    public CategoryContentVO getCategoryById(Long id) {

        Category category = this.getById(id);

        return BeanUtils.copyBean(category, CategoryContentVO.class);
    }

    /**
     * 修改分类状态
     */
    @Override
    public void changeCategoryStatus(Long id, Integer status) {

        status = Objects.equals(status, StatusConstant.ENABLE) ? StatusConstant.DISABLE : StatusConstant.ENABLE;

        this.lambdaUpdate()
                .eq(Category::getId, id)
                .set(Category::getStatus, status)
                .update();
    }
}
