package com.yuntan.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuntan.article.domain.po.ArticleTag;
import org.apache.ibatis.annotations.Delete;

public interface ArticleTagMapper extends BaseMapper<ArticleTag> {

    /**
     * 根据文章ID删除文章标签关系
     */
    @Delete("delete from article_tag where article_id = #{articleId}")
    void deleteByArticleId(Long articleId);
}
