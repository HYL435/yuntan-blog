package com.yuntan.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuntan.article.domain.po.Category;
import org.apache.ibatis.annotations.Select;

public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 根据文章ID获取文章分类
     */
    @Select("select c.category_name from category c left join article_category ac on c.id = ac.category_id where ac.article_id = #{articleId}")
    String getCategoryByArticleId(Long articleId);

}
