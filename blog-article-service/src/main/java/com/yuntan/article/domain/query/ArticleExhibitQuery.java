package com.yuntan.article.domain.query;

import com.yuntan.common.domain.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ArticleExhibitQuery extends PageQuery {

    /**
     * 文章分类
     */
    @Schema(
            description = "文章分类",
            example = "技术分享",
            maxLength = 100
    )
    private Long categoryId;

    /**
     * 文章标签，集合
     */
    @Schema(
            description = "文章标签",
            example = "[\"Java\",\"Spring Boot\"]"
    )
    private Long tagId;

}
