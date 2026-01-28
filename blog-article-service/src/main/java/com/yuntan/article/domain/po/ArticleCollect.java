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
 * 文章收藏实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "文章收藏实体")
public class ArticleCollect implements Serializable {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "收藏ID", example = "1")
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
     * 收藏时间
     */
    @Schema(
            description = "收藏时间",
            example = "2024-01-01 00:00:00"
    )
    private LocalDateTime createTime;

}
