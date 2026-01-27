package com.yuntan.article.controller.front;

import com.yuntan.article.domain.query.ArticleExhibitQuery;
import com.yuntan.article.domain.vo.front.ArticleExhibitFrontVO;
import com.yuntan.article.domain.vo.front.ArticleFrontVO;
import com.yuntan.article.service.IArticleService;
import com.yuntan.common.domain.PageDTO;
import com.yuntan.common.domain.PageQuery;
import com.yuntan.common.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/front/articles")
@RestController
@RequiredArgsConstructor
@Tag(description = "前台文章接口", name = "文章管理")
public class FrontArticleController {

    private final IArticleService articleService;

    @Operation(summary = "根据文章ID获取文章信息", description = "包括文章标题，摘要，正文，封面图，关键词，分类，标签，是否原创，是否置顶，文章状态，点赞数，评论数，收藏数，浏览量，发布时间")
    @GetMapping("/{id}")
    public Result<ArticleFrontVO> getArticleById(@PathVariable Long id) {

        log.info("根据文章ID获取文章信息，文章ID：{}", id);

        ArticleFrontVO articleFrontVO = articleService.getArticleById(id);

        return Result.ok(articleFrontVO);
    }

    @Operation(summary = "分页查询文章列表", description = "包括文章标题，摘要，封面图，分类，标签，是否原创，是否置顶，文章状态，点赞数，评论数，收藏数，浏览量，发布时间")
    @GetMapping("/page")
    public Result<PageDTO<ArticleExhibitFrontVO>> pageExhibit(PageQuery pageQuery) {

        log.info("分页查询文章列表，查询参数：{}", pageQuery);

        PageDTO<ArticleExhibitFrontVO> result = articleService.pageExhibit(pageQuery);

        return Result.ok(result);
    }

    @Operation(summary = "分页查询文章列表", description = "包括文章标题，摘要，封面图，分类，标签，是否原创，是否置顶，文章状态，点赞数，评论数，收藏数，浏览量，发布时间")
    @GetMapping("/page/categoryOrTags")
    public Result<PageDTO<ArticleExhibitFrontVO>> pageExhibitByCategoryOrTags(ArticleExhibitQuery articleExhibitQuery) {

        log.info("分页查询文章列表，查询参数：{}", articleExhibitQuery);

        PageDTO<ArticleExhibitFrontVO> result = articleService.pageExhibitByCategoryOrTags(articleExhibitQuery);

        return Result.ok(result);
    }

}
