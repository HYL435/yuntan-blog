package com.yuntan.user.domain.dto.front;

import io.swagger.v3.oas.annotations.media.Schema;

public class UserCommentDTO {

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
