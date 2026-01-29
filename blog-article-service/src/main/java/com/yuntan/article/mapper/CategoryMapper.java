package com.yuntan.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuntan.article.domain.po.Category;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 根据文章ID获取文章分类
     */
    @Select("select c.category_name from category c left join article_category ac on c.id = ac.category_id where ac.article_id = #{articleId}")
    String getCategoryByArticleId(Long articleId);

    /**
     * 获取所有分类
     */
    @Select("select category_name from category where status = 1 and deleted = 0 order by sort desc")
    List<String> getCategories();

    /**
     * 获取所有分类ID
     */
    @Select("select id from category where status = 1 and deleted = 0 order by sort desc")
    List<Integer> selectAllCategoryIds();
}
