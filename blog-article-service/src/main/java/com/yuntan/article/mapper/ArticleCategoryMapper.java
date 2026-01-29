package com.yuntan.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuntan.article.domain.po.ArticleCategory;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ArticleCategoryMapper extends BaseMapper<ArticleCategory> {

    /**
     * 根据文章ID删除文章分类关系
     */
    @Delete("delete from article_category where article_id = #{articleId}")
    void deleteByArticleId(Long articleId);

    /**
     * 根据分类ID获取文章ID列表
     */
    @Select("select article_id from article_category where article_id = #{catId} order by create_time desc limit #{i}")
    List<Long> selectCategoryHotIds(Integer catId, int i);
}
