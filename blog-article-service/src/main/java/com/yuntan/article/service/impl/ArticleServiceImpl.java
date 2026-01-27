package com.yuntan.article.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuntan.article.domain.po.Article;
import com.yuntan.article.domain.query.ArticleExhibitQuery;
import com.yuntan.article.domain.query.ArticleManageQuery;
import com.yuntan.article.domain.vo.CategorizableVO;
import com.yuntan.article.domain.vo.admin.ArticleAdminVO;
import com.yuntan.article.domain.vo.front.ArticleExhibitFrontVO;
import com.yuntan.article.domain.vo.front.ArticleFrontVO;
import com.yuntan.article.mapper.ArticleMapper;
import com.yuntan.article.mapper.CategoryMapper;
import com.yuntan.article.mapper.TagMapper;
import com.yuntan.article.service.IArticleService;
import com.yuntan.common.constant.MessageConstant;
import com.yuntan.common.domain.PageDTO;
import com.yuntan.common.domain.PageQuery;
import com.yuntan.common.utils.BeanUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements IArticleService {

    public final ArticleMapper articleMapper;

    public final CategoryMapper categoryMapper;

    public final TagMapper tagMapper;

    /**
     * 根据文章ID获取文章信息
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ArticleFrontVO getArticleById(Long id) {

        // 获取文章信息
        Article article = this.getById(id);

        // 转换为前台VO对象
        if (article == null) {
            throw new RuntimeException(MessageConstant.ARTICLE_NOT_FOUND);
        }
        ArticleFrontVO articleFrontVO = BeanUtils.copyBean(article, ArticleFrontVO.class);

        // 填充分类和标签
        setCategoryAndTags(articleFrontVO);

        return articleFrontVO;
    }

    /**
     * 分页查询文章列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PageDTO<ArticleExhibitFrontVO> pageExhibit(PageQuery pageQuery) {

        // 构建分页参数，获取page对象
        Page<Article> page = pageQuery.toMpPage();

        // 获取总记录数
        long totalCount = this.count();
        page.setTotal(totalCount);  // 设置总记录数

        // 设置查询条件
        LambdaQueryChainWrapper<Article> query = new LambdaQueryChainWrapper<>(articleMapper)
                .eq(Article::getStatus, 1)
                .orderByDesc(Article::getIsTop)
                .orderByDesc(Article::getUpdateTime);

        // 查询
        Page<Article> result = query.page(page);

        // 转换为VO对象
        PageDTO<ArticleExhibitFrontVO> articleExhibitFrontVOPageDTO = PageDTO.of(result, ArticleExhibitFrontVO.class);

        // 获取并设置分类和标签
        articleExhibitFrontVOPageDTO.getList().forEach(this::setCategoryAndTags);

        return articleExhibitFrontVOPageDTO;
    }

    /**
     * 分页查询文章列表通过分类或标签
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PageDTO<ArticleExhibitFrontVO> pageExhibitByCategoryOrTags(ArticleExhibitQuery query) {

        // 构建分页参数，获取page对象
        Page<Article> page = query.toMpPage();

        // 获取总记录数
        long totalCount = this.count();
        page.setTotal(totalCount);  // 设置总记录数

        // 调用Mapper联表分页查询（分页插件自动拼接LIMIT）
        Page<ArticleExhibitFrontVO> resultPage = articleMapper.selectArticleWithCategory(
                page,
                query.getCategoryId(),
                query.getTagId()
        );

        // 转换为VO对象
        PageDTO<ArticleExhibitFrontVO> articleExhibitFrontVOPageDTO = PageDTO.of(resultPage, ArticleExhibitFrontVO.class);

        // 获取并设置分类和标签
        articleExhibitFrontVOPageDTO.getList().forEach(this::setCategoryAndTags);

        return articleExhibitFrontVOPageDTO;
    }

    /**
     * 分页查询文章管理列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PageDTO<ArticleAdminVO> pageManage(ArticleManageQuery query) {

        // 构建分页参数，获取page对象
        Page<Article> page = query.toMpPage();

        // 获取总记录数
        long totalCount = this.count();
        page.setTotal(totalCount);  // 设置总记录数

        // 设置查询条件
        LambdaQueryChainWrapper<Article> pageQuery = new LambdaQueryChainWrapper<>(articleMapper)
                .orderByDesc(Article::getIsTop)
                .orderByDesc(Article::getUpdateTime);

        // 查询
        Page<Article> resultPage = pageQuery.page(page);

        // 转换为VO对象
        PageDTO<ArticleAdminVO> articleAdminVOPageDTO = PageDTO.of(resultPage, ArticleAdminVO.class);

        // 获取并设置分类和标签
        articleAdminVOPageDTO.getList().forEach(this::setCategoryAndTags);

        return articleAdminVOPageDTO;
    }

    // 获取并设置文章分类和标签
    private void setCategoryAndTags(CategorizableVO categorizableVO) {

        // 获取文章分类
        String category = categoryMapper.getCategoryByArticleId(categorizableVO.getId());

        // 获取文章标签
        List<String> tags = tagMapper.getTagsByArticleId(categorizableVO.getId());

        categorizableVO.setCategory(category);
        categorizableVO.setTags(tags);

    }
}
