package com.yuntan.user.domain.vo.front;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * UserVO
 */
@Data
@Schema(description = "用户登录相应对象")
public class UserLoginVO {
    /**
     * 用户id
     */
    @Schema(description = "用户ID", example = "1")
    private Long id;

    /**
     * 昵称
     */
    @Schema(description = "用户昵称", example = "张三")
    private String nickname;

    /**
     * 头像URL
     */
    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String image;

    /**
     * 用户角色
     */
    @Schema(description = "用户角色", example = "2")
    private Integer role;

    /**
     * 用户状态
     */
    @Schema(description = "用户状态", example = "1")
    private Integer status;

    /**
     * token
     */
    @Schema(description = "用户登录令牌", example = "eyJhbGci)OiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
}