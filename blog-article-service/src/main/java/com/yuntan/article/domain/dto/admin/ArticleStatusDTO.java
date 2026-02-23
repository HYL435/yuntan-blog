package com.yuntan.article.domain.dto.admin;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleStatusDTO {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "文章ID", example = "1")
    private Long id;

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

}
