package com.yuntan.comment.domain.dto.admin;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class CommentStatusDTO implements Serializable {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "评论ID", example = "1")
    private Long id;

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
}
