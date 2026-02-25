package com.yuntan.article.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuntan.article.constant.RedisConstant;
import com.yuntan.article.domain.dto.admin.CategoryDTO;
import com.yuntan.article.domain.dto.admin.CategoryStatusDTO;
import com.yuntan.article.domain.dto.admin.CategoryUpdateDTO;
import com.yuntan.article.domain.po.Category;
import com.yuntan.article.domain.vo.admin.CategoryContentVO;
import com.yuntan.article.domain.vo.admin.CategoryVO;
import com.yuntan.article.domain.vo.front.CategoryFrontVO;
import com.yuntan.article.mapper.ArticleCategoryMapper;
import com.yuntan.article.mapper.CategoryMapper;
import com.yuntan.article.service.ICategoryService;
import com.yuntan.common.constant.StatusConstant;
import com.yuntan.common.exception.BusinessException;
import com.yuntan.common.utils.BeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

    public final CategoryMapper categoryMapper;
    public final ArticleCategoryMapper articleCategoryMapper;

    public final StringRedisTemplate redisTemplate;

    /**
     * 获取所有分类名称
     */
    @Override
    public List<CategoryFrontVO> getAllCategoryNames() {

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

        clearArticleCacheByCategoryId(category.getId());
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
    public void changeCategoryStatus(CategoryStatusDTO categoryStatusDTO) {

//        Integer status;
//
//        status = Objects.equals(categoryStatusDTO.getStatus(), StatusConstant.ENABLE) ? StatusConstant.DISABLE : StatusConstant.ENABLE;

        // 参数校验，确保参数不为空
        if (categoryStatusDTO.getId() == null || categoryStatusDTO.getStatus() == null) {
            throw new BusinessException("分类ID和状态不能为空");
        }

        this.lambdaUpdate()
                .eq(Category::getId, categoryStatusDTO.getId())
                .set(Category::getStatus, categoryStatusDTO.getStatus())
                .update();

        clearArticleCacheByCategoryId(categoryStatusDTO.getId());
    }

    /**
    * 删除分类
    */
    @Override
    public void removeCategoryById(Long id) {

        // 如果当前分类绑定的有文章，则不能删除
        List<Long> articleIds = getArticleIdsByCategoryId(id);
        if (!articleIds.isEmpty()) {
            throw new IllegalStateException("当前分类绑定的有文章，不能删除");
        } else {
            // 删除分类
            this.removeById(id);
        }


    }

    // 通过分类id获取文章列表
    public List<Long> getArticleIdsByCategoryId(Long categoryId) {

        return articleCategoryMapper.getArticlesByCategoryId(categoryId);
    }

    // 清除文章相关的缓存
    private void clearArticleCacheByCategoryId(Long categoryId) {

        List<Long> articleIds = getArticleIdsByCategoryId(categoryId);

        for (Long articleId : articleIds) {
            redisTemplate.delete(RedisConstant.CACHE_KEY_PREFIX + articleId);
        }
    }

}
