package com.yuntan.article.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文章浏览实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "文章浏览实体")
public class ArticleView implements Serializable {

    /**
     * 主键ID
     */
    @Schema(description = "浏览记录ID", example = "1")
    private Long id;

    /**
     * 关联用户表ID，空字符串=匿名用户
     */
    @Schema(
            description = "关联用户表ID，空字符串=匿名用户",
            example = "1"
    )
    private Long userId;

    /**
     * 关联文章表ID
     */
    @Schema(
            description = "关联文章表ID",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long articleId;

    /**
     * 访客IP，匿名用户去重依据
     */
    @Schema(
            description = "访客IP，匿名用户去重依据",
            example = "192.168.1.1",
            maxLength = 32
    )
    private String ip;

    /**
     * 浏览时间
     */
    @Schema(
            description = "浏览时间",
            example = "2024-01-01 00:00:00"
    )
    private LocalDateTime createTime;

}
