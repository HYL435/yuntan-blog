package com.yuntan.article.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuntan.article.domain.dto.admin.ArticleSaveDTO;
import com.yuntan.article.domain.po.Article;
import com.yuntan.article.domain.po.ArticleCategory;
import com.yuntan.article.domain.po.ArticleTag;
import com.yuntan.article.domain.query.ArticleExhibitQuery;
import com.yuntan.article.domain.query.ArticleManageQuery;
import com.yuntan.article.domain.vo.CategorizableVO;
import com.yuntan.article.domain.vo.admin.ArticleAdminVO;
import com.yuntan.article.domain.vo.admin.ArticleDetailVO;
import com.yuntan.article.domain.vo.front.ArticleExhibitFrontVO;
import com.yuntan.article.domain.vo.front.ArticleFrontVO;
import com.yuntan.article.enums.ArticleStatusEnum;
import com.yuntan.article.enums.ArticleTopStatusEnum;
import com.yuntan.article.mapper.*;
import com.yuntan.article.service.IArticleService;
import com.yuntan.common.constant.MessageConstant;
import com.yuntan.common.domain.PageDTO;
import com.yuntan.common.domain.PageQuery;
import com.yuntan.common.utils.BeanUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements IArticleService {

    public final ArticleMapper articleMapper;
    public final CategoryMapper categoryMapper;
    public final TagMapper tagMapper;
    public final ArticleCategoryMapper articleCategoryMapper;
    public final ArticleTagMapper articleTagMapper;

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

        // 设置查询条件
        LambdaQueryChainWrapper<Article> query = new LambdaQueryChainWrapper<>(articleMapper)
                .eq(Article::getStatus, ArticleStatusEnum.PUBLISHED.getValue())
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

    /**
     * 文章置顶
     */
    @Override
    public void articleTop(Long id) {

        this.lambdaUpdate()
                .eq(Article::getId, id)
                .set(Article::getIsTop, ArticleTopStatusEnum.TOP.getValue())
                .update();

    }

    /**
     * 获取文章详情
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArticleDetailVO getArticleDetail(Long id) {

        // 获取文章详情
        Article article = this.getById(id);

        if (article == null) {
            throw new RuntimeException(MessageConstant.ARTICLE_NOT_FOUND);
        }
        // 转换为VO对象
        ArticleDetailVO articleDetailVO = BeanUtils.copyBean(article, ArticleDetailVO.class);

        // 获取并设置分类和标签
        setCategoryAndTags(articleDetailVO);

        return articleDetailVO;
    }

    /**
     * 保存文章
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveArticle(ArticleSaveDTO articleSaveDTO) {

        // 构建文章对象
        Article article = BeanUtils.copyBean(articleSaveDTO, Article.class);

        // 将文章内容存储到数据库中
        this.save(article);
        // 获取文章id
        Long articleId = article.getId();
        // 获取标签ids
        List<Long> tagIds = articleSaveDTO.getTagIds();

        // 将文章分类和标签存储到数据库中
        // 删除原本的文章和分类关系
        articleCategoryMapper.deleteByArticleId(articleId);
        // 将新的文章和分类关系存入数据库中
        articleCategoryMapper.insert(new ArticleCategory(articleId, articleSaveDTO.getCategoryId()));

        // 删除原本的文章和标签关系
        articleTagMapper.deleteByArticleId(articleId);
        // 将新的文章和标签关系存入数据库中
        tagIds.forEach(tagId -> articleTagMapper.insert(new ArticleTag(articleId, tagId)));

    }

    /**
     * 发布文章
     */
    @Override
    public void publishArticle(Long id) {

        this.lambdaUpdate()
                .eq(Article::getId, id)
                .set(Article::getStatus, ArticleStatusEnum.PUBLISHED.getValue())
                .set(Article::getPublishTime, LocalDateTime.now())
                .update();

    }

    /**
     * 私密文章
     */
    @Override
    public void privateArticle(Long id) {

        this.lambdaUpdate()
                .eq(Article::getId, id)
                .set(Article::getStatus, ArticleStatusEnum.PRIVATE.getValue())
                .update();

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
