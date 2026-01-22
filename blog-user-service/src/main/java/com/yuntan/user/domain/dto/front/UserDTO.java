package com.yuntan.user.domain.dto.front;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * UserDTO
 */
@Data
@Schema(description = "用户数据传输对象")
public class UserDTO {
    /**
     * 用户id
     */
    @Schema(description = "用户ID", example = "1")
    private long id;
    
    /**
     * 昵称
     */
    @Schema(description = "用户昵称", example = "张三")
    private String nickname;
    
    /**
     * 邮箱地址
     */
    @Schema(description = "邮箱地址", example = "zhangsan@example.com")
    private String email;
    
    /**
     * 手机号
     */
    @Schema(description = "手机号", example = "13800138000")
    private String phone;
    
    /**
     * 头像URL
     */
    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String image;
    
    /**
     * 个人简介
     */
    @Schema(description = "个人简介", example = "一名热爱技术的开发者")
    private String intro;
    
    /**
     * 密码（8位及以上）
     */
    @Schema(description = "密码（8位及以上）", example = "password123")
    private String password;
}