package com.yuntan.article.controller.admin;

import com.yuntan.article.domain.query.ArticleManageQuery;
import com.yuntan.article.domain.vo.admin.ArticleAdminVO;
import com.yuntan.article.service.IArticleService;
import com.yuntan.common.domain.PageDTO;
import com.yuntan.common.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}