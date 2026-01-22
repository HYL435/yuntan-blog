package com.yuntan.user.domain.dto.front;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * UserRegisterDTO
 */
@Data
@Schema(description = "用户注册请求参数")
public class UserRegisterDTO {
    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "zhangsan", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;
    
    /**
     * 密码（8位及以上）
     */
    @Schema(description = "密码（8位及以上）", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
    
    /**
     * 邮箱地址
     */
    @Schema(description = "邮箱地址", example = "zhangsan@example.com")
    private String email;
}