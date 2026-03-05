package com.yuntan.api.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleInfoDTO {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "文章ID", example = "1")
    private Long id;

    /**
     * 文章标题
     */
    @Schema(
            description = "文章标题",
            example = "Spring Boot入门指南",
            maxLength = 200
    )
    private String title;
}
