package com.yuntan.article.domain.dto.admin;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 分类实体类
 */
@Data
@Schema(description = "分类实体")
public class CategoryDTO {

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
