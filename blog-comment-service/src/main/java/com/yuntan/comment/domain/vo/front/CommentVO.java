package com.yuntan.comment.domain.vo.front;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "评论实体")
public class CommentVO {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "评论ID", example = "1")
    private Long id;

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
     * 昵称
     */
    @Schema(
            description = "用户昵称",
            example = "张三",
            maxLength = 50
    )
    private String nickname;

    /**
     * 头像URL
     */
    @Schema(
            description = "头像URL地址",
            example = "https://example.com/avatar.jpg",
            maxLength = 500
    )
    private String userImg;

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
    private LocalDateTime createTime;

    /**
     * 子评论
     */
    @Schema(description = "子评论")
    private List<CommentChildVO> children;
}
