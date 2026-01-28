package com.yuntan.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuntan.article.domain.po.ArticleCategory;
import org.apache.ibatis.annotations.Delete;

public interface ArticleCategoryMapper extends BaseMapper<ArticleCategory> {

    /**
     * 根据文章ID删除文章分类关系
     */
    @Delete("delete from article_category where article_id = #{articleId}")
    void deleteByArticleId(Long articleId);

}
