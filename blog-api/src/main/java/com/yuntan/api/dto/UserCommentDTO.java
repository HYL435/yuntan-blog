package com.yuntan.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCommentDTO {

    /**
    * 用户ID
     */
    @Schema(description = "用户ID", example = "1")
    private Long UserId;

    /**
     * 昵称
     */
    @Schema(description = "用户昵称", example = "张三")
    private String nickname;

    /**
     * 头像URL
     */
    @Schema(description = "头像地址", example = "https://example.com/avatar.jpg")
    private String image;

}
