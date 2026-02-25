package com.yuntan.article.domain.dto.admin;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "分类实体")
public class CategoryStatusDTO {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "分类ID", example = "1")
    private Long id;

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

}
