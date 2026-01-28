package com.yuntan.article.controller.admin;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/admin/tags")
@RequiredArgsConstructor
@Tag(description = "后台标签接口", name = "标签")
public class AdminTagController {
}
