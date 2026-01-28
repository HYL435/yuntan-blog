package com.yuntan.article.domain.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;


@Data
public class ArticleSaveDTO {

    /**
     * 文章标题
     */
    @Schema(
            description = "文章标题",
            example = "Spring Boot入门指南",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 200
    )
    private String title;

    /**
     * 文章摘要，列表页展示
     */
    @Schema(
            description = "文章摘要",
            example = "本文介绍了Spring Boot的基础知识...",
            maxLength = 500
    )
    private String summary;

    /**
     * 文章正文（支持Markdown/HTML）
     */
    @Schema(
            description = "文章正文",
            example = "# Spring Boot介绍...",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String content;

    /**
     * 文章封面图URL
     */
    @Schema(
            description = "封面图URL",
            example = "https://example.com/cover.jpg",
            maxLength = 500
    )
    private String coverImg;

    /**
     * 文章关键词（SEO用）
     */
    @Schema(
            description = "SEO关键词",
            example = "spring boot,java,framework",
            maxLength = 200
    )
    private String keywords;

    /**
     * 文章分类
     */
    @Schema(
            description = "文章分类",
            example = "技术分享",
            maxLength = 100
    )
    private Long categoryId;

    /**
     * 文章标签，集合
     */
    @Schema(
            description = "文章标签",
            example = "[\"Java\",\"Spring Boot\"]"
    )
    private List<Long> tagIds;

    /**
     * 是否原创：0-转载，1-原创（默认1原创）
     */
    @Schema(
            description = "是否原创：0-转载，1-原创",
            example = "1",
            allowableValues = {"0", "1"},
            defaultValue = "1"
    )
    private Integer isOriginal;

    /**
     * 是否置顶：0-否，1-是（默认0否）
     */
    @Schema(
            description = "是否置顶：0-否，1-是",
            example = "0",
            allowableValues = {"0", "1"},
            defaultValue = "0"
    )
    private Integer isTop;

}
