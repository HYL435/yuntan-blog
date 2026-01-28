package com.yuntan.article.domain.dto.admin;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 分类实体类
 */
@Data
@Schema(description = "分类实体")
public class CategoryUpdateDTO {

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

}
