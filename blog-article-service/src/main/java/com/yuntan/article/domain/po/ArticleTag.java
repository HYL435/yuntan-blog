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
 * 文章标签关联实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "文章标签关联实体")
public class ArticleTag implements Serializable {

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
     * 关联标签表ID
     */
    @Schema(
            description = "关联标签表ID",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long tagId;

    /**
     * 创建时间
     */
    @Schema(
            description = "创建时间",
            example = "2024-01-01 00:00:00"
    )
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
