package com.yuntan.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuntan.article.domain.po.Article;
import com.yuntan.article.domain.vo.front.ArticleExhibitFrontVO;

public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 联表分页查询文章列表
     */
    Page<ArticleExhibitFrontVO> selectArticleWithCategory(Page<Article> page, Long categoryId, Long tagId);
}
