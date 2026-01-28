package com.yuntan.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuntan.article.domain.dto.admin.ArticleSaveDTO;
import com.yuntan.article.domain.po.Article;
import com.yuntan.article.domain.query.ArticleExhibitQuery;
import com.yuntan.article.domain.query.ArticleManageQuery;
import com.yuntan.article.domain.vo.admin.ArticleAdminVO;
import com.yuntan.article.domain.vo.admin.ArticleDetailVO;
import com.yuntan.article.domain.vo.front.ArticleExhibitFrontVO;
import com.yuntan.article.domain.vo.front.ArticleFrontVO;
import com.yuntan.common.domain.PageDTO;
import com.yuntan.common.domain.PageQuery;

public interface IArticleService extends IService<Article> {

    /**
     * 根据文章ID获取文章信息
     */
    ArticleFrontVO getArticleById(Long id);

    /**
     * 分页查询文章列表
     */
    PageDTO<ArticleExhibitFrontVO> pageExhibit(PageQuery pageQuery);

    /**
     * 根据分类或标签分页查询文章列表
     */
    PageDTO<ArticleExhibitFrontVO> pageExhibitByCategoryOrTags(ArticleExhibitQuery articleExhibitQuery);

    /**
     * 分页查询文章管理列表
     */
    PageDTO<ArticleAdminVO> pageManage(ArticleManageQuery articleManageQuery);

    /**
     * 文章置顶
     */
    void articleTop(Long id, Integer top);

    /**
     * 获取文章详情
     */
    ArticleDetailVO getArticleDetail(Long id);

    /**
     * 保存文章
     */
    void saveArticle(ArticleSaveDTO articleSaveDTO);

    /**
     * 发布文章
     */
    void publishArticle(Long id);

    /**
     * 私有文章
     */
    void privateArticle(Long id);
}
