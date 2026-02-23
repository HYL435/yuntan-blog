package com.yuntan.article.domain.query;

import com.yuntan.common.domain.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArticleManageQuery extends PageQuery {

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
     * 是否原创：0-转载，1-原创（默认1原创）
     */
    @Schema(
            description = "是否原创：0-转载，1-原创",
            example = "1",
            allowableValues = {"0", "1"},
            defaultValue = "1"
    )
    private Integer isOriginal;

    /**
     * 文章分类
     */
    @Schema(
            description = "文章分类",
            example = "技术分享",
            maxLength = 100
    )
    private String category;

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
