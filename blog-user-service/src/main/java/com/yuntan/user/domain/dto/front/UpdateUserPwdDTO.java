package com.yuntan.user.domain.dto.front;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * UpdateUserPwdDTO
 */
@Data
@Schema(description = "更新用户密码请求参数")
public class UpdateUserPwdDTO {
    /**
     * 用户id
     */
    @Schema(description = "用户ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private long id;
    
    /**
     * 旧密码
     */
    @Schema(description = "旧密码", example = "oldpassword123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String oldPassword;
    
    /**
     * 新密码
     */
    @Schema(description = "新密码", example = "newpassword456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newPassword;
}