package com.yuntan.user.domain.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * UserDTO
 */
@Data
@Schema(description = "用户数据传输对象")
public class UserRoleDTO {
    /**
     * 用户id
     */
    @Schema(description = "用户ID", example = "1")
    private Long id;

    /**
     * 角色：0-博主，1-管理员，2-普通访客（默认为2普通访客）
     */
    @Schema(
            description = "用户角色：0-博主，1-管理员，2-普通访客（默认2）",
            example = "2",
            allowableValues = {"0", "1", "2"},
            defaultValue = "2"
    )
    private Integer role;
}