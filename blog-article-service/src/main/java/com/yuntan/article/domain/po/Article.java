package com.yuntan.article.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文章实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "文章实体")
public class Article implements Serializable {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "文章ID", example = "1")
    private Long id;

    /**
     * 关联用户表ID（作者）
     */
    @Schema(
            description = "作者ID",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long authorId;

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

    /**
     * 文章状态：0-草稿，1-已发布，2-回收站，3-私密（默认草稿）
     */
    @Schema(
            description = "文章状态：0-草稿，1-已发布，2-私密",
            example = "0",
            allowableValues = {"0", "1", "2"},
            defaultValue = "0"
    )
    private Integer status;


    /**
     * 文章点赞数（缓存同步）
     */
    @Schema(
            description = "点赞数",
            example = "10",
            defaultValue = "0"
    )
    private Long likeCount;

    /**
     * 文章评论数（缓存同步）
     */
    @Schema(
            description = "评论数",
            example = "5",
            defaultValue = "0"
    )
    private Long commentCount;

    /**
     * 文章收藏数（缓存同步）
     */
    @Schema(
            description = "收藏数",
            example = "3",
            defaultValue = "0"
    )
    private Long collectCount;

    /**
     * 文章浏览量（缓存同步）
     */
    @Schema(
            description = "浏览量",
            example = "100",
            defaultValue = "0"
    )
    private Long viewCount;

    /**
     * 发布时间（草稿转发布时填充，允许为空）
     */
    @Schema(
            description = "发布时间",
            example = "2024-01-15 10:30:00"
    )
    private LocalDateTime publishTime;

    /**
     * 软删除：0-未删，1-已删
     */
    @Schema(
            description = "软删除标记：0-未删，1-已删",
            example = "0",
            allowableValues = {"0", "1"},
            defaultValue = "0"
    )
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    @Schema(
            description = "创建时间",
            example = "2024-01-01 00:00:00"
    )
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(
            description = "最后更新时间",
            example = "2024-01-15 09:00:00"
    )
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
