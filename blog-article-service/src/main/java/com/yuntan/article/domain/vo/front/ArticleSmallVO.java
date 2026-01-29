package com.yuntan.article.domain.vo.front;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 文章实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleSmallVO {

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
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 200
    )
    private String title;

    /**
     * 文章封面图URL
     */
    @Schema(
            description = "封面图URL",
            example = "https://example.com/cover.jpg",
            maxLength = 500
    )
    private String coverImg;
}
