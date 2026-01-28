package com.yuntan.article.controller.front;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/front/categories")
@RestController
@RequiredArgsConstructor
@Tag(description = "前台分类接口", name = "分类")
public class FrontCategoryController {



}
