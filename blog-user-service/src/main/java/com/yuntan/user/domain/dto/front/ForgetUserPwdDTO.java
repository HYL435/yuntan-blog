package com.yuntan.user.domain.dto.front;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * UpdateUserPwdDTO
 */
@Data
@Schema(description = "更新用户密码请求参数")
public class ForgetUserPwdDTO {
    /**
     * 用户邮箱
     */
    @Schema(description = "用户邮箱", example = "kfje484@qq.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
    
    /**
     * 新密码
     */
    @Schema(description = "新密码", example = "newpassword456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newPassword;

    /**
     * 验证码
     */
    @Schema(description = "验证码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;
}