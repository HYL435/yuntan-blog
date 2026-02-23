package com.yuntan.article.domain.vo.admin;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.yuntan.article.domain.vo.CategorizableVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleAdminVO implements CategorizableVO {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "文章ID", example = "1")
    private Long id;

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
     * 文章封面图URL
     */
    @Schema(
            description = "封面图URL",
            example = "https://example.com/cover.jpg",
            maxLength = 500
    )
    private String coverImg;

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
     * 文章状态：0-草稿，1-已发布，2-私密（默认草稿）
     */
    @Schema(
            description = "文章状态：0-草稿，1-已发布，2-私密",
            example = "0",
            allowableValues = {"0", "1", "2"},
            defaultValue = "0"
    )
    private Integer status;

    /**
     * 文章分类
     */
    @Schema(
            description = "文章分类",
            example = "技术分享",
            maxLength = 100
    )
    private String category;

    /**
     * 文章标签，集合
     */
    @Schema(
            description = "文章标签",
            example = "[\"Java\",\"Spring Boot\"]"
    )
    private List<String> tags;


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
