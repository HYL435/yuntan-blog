package com.yuntan.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuntan.article.domain.dto.front.ArticleInteractDTO;
import com.yuntan.article.domain.po.Article;
import com.yuntan.article.domain.vo.front.ArticleExhibitFrontVO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 联表分页查询文章列表
     */
    Page<ArticleExhibitFrontVO> selectArticleWithCategory(Page<Article> page, Long categoryId, Long tagId);

    /**
     * 获取全局热门文章ID列表
     */
    @Select("SELECT id FROM article ORDER BY view_count DESC LIMIT #{i};")
    List<Long> selectGlobalHotIds(int i);

    /**
     * 批量更新文章互动数据
     */
    void updateInteractBatch(List<ArticleInteractDTO> updateList);
}
