package com.yuntan.article.domain.vo.front;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "分类实体")
public class CategoryFrontVO {

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
}
