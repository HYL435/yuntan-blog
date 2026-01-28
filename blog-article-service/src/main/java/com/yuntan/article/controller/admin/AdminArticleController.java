package com.yuntan.article.controller.admin;

import com.yuntan.article.domain.dto.admin.ArticleSaveDTO;
import com.yuntan.article.domain.query.ArticleManageQuery;
import com.yuntan.article.domain.vo.admin.ArticleAdminVO;
import com.yuntan.article.domain.vo.admin.ArticleDetailVO;
import com.yuntan.article.service.IArticleService;
import com.yuntan.common.domain.PageDTO;
import com.yuntan.common.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/articles")
@Tag(description = "后台文章接口", name = "文章管理")
public class AdminArticleController {

    public final IArticleService articleService;


    @Operation(summary = "获取文章管理列表")
    @GetMapping("/page")
    public Result<PageDTO<ArticleAdminVO>> pageManage(ArticleManageQuery articleManageQuery) {

        log.info("获取文章管理列表，查询参数：{}", articleManageQuery);

        PageDTO<ArticleAdminVO> result = articleService.pageManage(articleManageQuery);

        return Result.ok(result);
    }

    @Operation(summary = "文章置顶")
    @PutMapping("/top/{id}")
    public Result<Void> articleTop(@PathVariable Long id) {

        log.info("文章置顶，文章ID：{}", id);

        articleService.articleTop(id);

        return Result.ok();
    }


    @Operation(summary = "软删除文章")
    @DeleteMapping("/deleted/{id}")
    public Result<Void> deletedArticle(@PathVariable Long id) {

        log.info("软删除文章，文章ID：{}", id);

        articleService.removeById(id);

        return Result.ok();
    }

    @Operation(summary = "获取文章详情")
    @GetMapping("/{id}")
    public Result<ArticleDetailVO> getArticleDetail(@PathVariable Long id) {

        log.info("获取文章详情，文章ID：{}", id);

        ArticleDetailVO articleDetailVO = articleService.getArticleDetail(id);

        return Result.ok(articleDetailVO);
    }

    @Operation(summary = "保存文章")
    @PostMapping
    public Result<Void> saveArticle(@RequestBody ArticleSaveDTO articleSaveDTO) {

        log.info("保存文章，文章保存参数：{}", articleSaveDTO);

        articleService.saveArticle(articleSaveDTO);

        return Result.ok();
    }

    @Operation(summary = "发布文章")
    @PutMapping("/publish/{id}")
    public Result<Void> publishArticle(@PathVariable Long id) {

        log.info("发布文章，文章ID：{}", id);

        articleService.publishArticle(id);

        return Result.ok();
    }

    @Operation(summary = "私有文章")
    @PutMapping("/private/{id}")
    public Result<Void> privateArticle(@PathVariable Long id) {

        log.info("私有文章，文章ID：{}", id);

        articleService.privateArticle(id);

        return Result.ok();
    }

}