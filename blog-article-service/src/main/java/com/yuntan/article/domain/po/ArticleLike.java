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
 * 文章点赞实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "文章点赞实体")
public class ArticleLike implements Serializable {

    /**
     * 主键ID
     */
    @Schema(description = "点赞ID", example = "1")
    private Long id;

    /**
     * 关联用户表ID
     */
    @Schema(
            description = "关联用户表ID",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long userId;

    /**
     * 关联文章表ID
     */
    @Schema(
            description = "关联文章表ID",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long articleId;

    /**
     * 点赞时间
     */
    @Schema(
            description = "点赞时间",
            example = "2024-01-01 00:00:00"
    )
    private LocalDateTime createTime;

}
