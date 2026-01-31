package com.yuntan.comment.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评论实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "评论实体")
public class Comment implements Serializable {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "评论ID", example = "1")
    private Long id;

    /**
     * 关联文章ID
     */
    @Schema(
            description = "文章ID",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long articleId;

    /**
     * 评论者用户ID
     */
    @Schema(
            description = "评论者ID",
            example = "2",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long userId;

    /**
     * 父评论ID（用于嵌套回复，0 表示根评论）
     */
    @Schema(
            description = "父评论ID，0 表示根评论",
            example = "0"
    )
    private Long parentId;

    /**
     * 被回复用户ID（仅当 parentId > 0 时有效）
     */
    @Schema(
            description = "被回复用户ID（可选）",
            example = "3"
    )
    private Long toUserId;

    /**
     * 评论内容
     */
    @Schema(
            description = "评论内容",
            example = "写得真好！",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 1000
    )
    private String content;

    /**
     * 评论URL
     */
    @Schema(
            description = "评论URL",
            example = "https://example.com/cover.jpg",
            maxLength = 500
    )
    private String image;

    /**
     * 点赞数（缓存同步）
     */
    @Schema(
            description = "点赞数",
            example = "5",
            defaultValue = "0"
    )
    private Integer likeCount;

    /**
     * 评论状态：0-待审核，1-审核通过， 2-审核拒绝
     */
    @Schema(
            description = "评论状态：0-待审核，1-审核通过， 2-审核拒绝",
            example = "0",
            allowableValues = {"0", "1"},
            defaultValue = "0"
    )
    private Integer status;

    /**
     * 评论人IP
     */
    @Schema(
            description = "评论人IP",
            example = "192.168.1.1"
    )
    private String ip;

    /**
     * 创建时间
     */
    @Schema(
            description = "创建时间",
            example = "2024-01-15 10:30:00"
    )
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(
            description = "最后更新时间",
            example = "2024-01-15 10:35:00"
    )
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 软删除标记：0-未删，1-已删
     */
    @Schema(
            description = "软删除标记：0-未删，1-已删",
            example = "0",
            allowableValues = {"0", "1"},
            defaultValue = "0"
    )
    @TableLogic
    private Integer deleted;
}
