package com.yuntan.user.domain.dto.front;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * UserLoginDTO
 */
@Data
@Schema(description = "用户登录请求参数")
public class UserLoginDTO {
    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "zhangsan", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱", example = "56454569866@qq.com")
    private String email;

    /**
     * 密码（8位及以上）
     */
    @Schema(description = "密码", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}