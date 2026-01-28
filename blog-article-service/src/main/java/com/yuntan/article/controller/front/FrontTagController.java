package com.yuntan.article.controller.front;

import com.yuntan.article.service.ITagService;
import com.yuntan.common.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/front/tags")
@RequiredArgsConstructor
@Tag(description = "前台标签接口", name = "标签")
public class FrontTagController {

    private final ITagService tagService;


    @Operation(summary = "获取所有标签名称")
    @GetMapping
    public Result<List<String>> getAllTagNames() {
        log.info("获取所有标签名称");

        List<String> list = tagService.getAllTagNames();

        return Result.ok(list);
    }

}
