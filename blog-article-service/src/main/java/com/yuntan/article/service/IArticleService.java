package com.yuntan.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuntan.article.domain.po.Article;
import com.yuntan.article.domain.query.ArticleExhibitQuery;
import com.yuntan.article.domain.query.ArticleManageQuery;
import com.yuntan.article.domain.vo.admin.ArticleAdminVO;
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
}
