package com.yuntan.article.domain.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文章分类关联实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "文章分类关联实体")
public class ArticleCategory implements Serializable {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "关联ID", example = "1")
    private Long id;

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
     * 关联分类表ID
     */
    @Schema(
            description = "关联分类表ID",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long categoryId;

    /**
     * 创建时间
     */
    @Schema(
            description = "创建时间",
            example = "2024-01-01 00:00:00"
    )
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    public ArticleCategory(Long articleId, Long categoryId) {

        this.articleId = articleId;
        this.categoryId = categoryId;
    }
}
