package com.yuntan.comment.domain.dto.front;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

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
public class CommentDTO implements Serializable {

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
    private MultipartFile imageFile;
}
