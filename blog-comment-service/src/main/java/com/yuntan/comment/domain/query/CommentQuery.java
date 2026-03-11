package com.yuntan.comment.domain.query;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.yuntan.common.domain.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentQuery extends PageQuery {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "评论ID", example = "1")
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

    /**
     * 昵称
     */
    @Schema(description = "用户昵称", example = "张三")
    private String nickname;

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
