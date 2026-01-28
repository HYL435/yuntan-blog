package com.yuntan.article.domain.vo.admin;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Schema(description = "分类实体")
public class CategoryVO {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "分类ID", example = "1")
    private Long id;

    /**
     * 分类名称
     */
    @Schema(
            description = "分类名称",
            example = "技术分享",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 100
    )
    private String categoryName;

    /**
     * 排序权重，值越大越靠前
     */
    @Schema(
            description = "排序权重，值越大越靠前",
            example = "100",
            defaultValue = "0"
    )
    private Long sort;

    /**
     * 状态：0-禁用，1-启用
     */
    @Schema(
            description = "状态：0-禁用，1-启用",
            example = "1",
            allowableValues = {"0", "1"},
            defaultValue = "1"
    )
    private Integer status;

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
