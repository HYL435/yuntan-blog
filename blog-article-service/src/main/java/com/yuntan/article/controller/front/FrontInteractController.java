package com.yuntan.article.controller.front;

import com.yuntan.article.service.InteractService;
import com.yuntan.common.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/front/interacts")
@RestController
@RequiredArgsConstructor
@Tag(description = "前台交互接口", name = "交互")
public class FrontInteractController {

    private final InteractService interactService;


    @Operation(summary = "文章点赞")
    @PostMapping("/like/{articleId}")
    public Result<Void> like(@PathVariable Long articleId) {

        log.info("点赞");

        interactService.like(articleId);

        return Result.ok();
    }

    @Operation(summary = "文章收藏")
    @PostMapping("/collect/{articleId}")
    public Result<Void> collect(@PathVariable Long articleId) {

        log.info("收藏");

        interactService.collect(articleId);

        return Result.ok();
    }

}
