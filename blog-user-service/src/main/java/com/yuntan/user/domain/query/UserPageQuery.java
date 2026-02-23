package com.yuntan.user.domain.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuntan.common.domain.PageQuery;
import com.yuntan.user.domain.po.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 用户查询参数DTO
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "用户查询参数")
public class UserPageQuery extends PageQuery {
    
    /**
     * 用户名
     */
    @Schema(
        description = "用户名（模糊查询）", 
        example = "zhangsan",
        maxLength = 50
    )
    private String username;
    
    /**
     * 昵称
     */
    @Schema(
        description = "用户昵称（模糊查询）", 
        example = "张三",
        maxLength = 50
    )
    private String nickname;
    
    /**
     * 邮箱地址
     */
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$|^$", message = "邮箱格式不正确")
    @Schema(
        description = "邮箱地址（精确查询）", 
        example = "zhangsan@example.com",
        pattern = "^[A-Za-z0-9+_.-]+@(.+)$"
    )
    private String email;
    
    /**
     * 角色：0-博主，1-管理员，2-普通访客（默认为2普通访客）
     */
//    @Min(value = 0, message = "角色值必须在0-2之间")
//    @Max(value = 2, message = "角色值必须在0-2之间")
    @Schema(
        description = "用户角色：0-博主，1-管理员，2-普通访客", 
        example = "2",
        allowableValues = {"0", "1", "2"}
    )
    private Integer role;
    
    /**
     * 账号状态：1-正常，2-禁用
     */
    @Min(value = 1, message = "状态值必须在1-2之间")
    @Max(value = 2, message = "状态值必须在1-2之间")
    @Schema(
        description = "账号状态：1-正常，2-禁用", 
        example = "1",
        allowableValues = {"1", "2"}
    )
    private Integer status;

    /**
     * 软删除：0-未删，1-已删
     */
    @Schema(
            description = "软删除标记：0-未删，1-已删",
            example = "0",
            allowableValues = {"0", "1"},
            defaultValue = "0"
    )
    private Integer deleted;

    /**
     * 将查询参数转换为MyBatis-Plus的LambdaQueryWrapper
     * @return
     */
    public LambdaQueryWrapper<User> toWrapper() {

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        // 处理删除标志，如果未传递 deleted，则默认为查询未删除的用户
        if (deleted == null) {
            wrapper.eq(User::getDeleted, 0);
        } else {
            wrapper.eq(User::getDeleted, deleted);
        }

        // 处理用户名模糊查询
        if (org.apache.commons.lang3.StringUtils.isNotBlank(username)) {
            wrapper.like(User::getUsername, username.trim());
        }

        // 处理昵称模糊查询
        if (org.apache.commons.lang3.StringUtils.isNotBlank(nickname)) {
            wrapper.like(User::getNickname, nickname.trim());
        }

        // 处理邮箱精确查询
        if (org.apache.commons.lang3.StringUtils.isNotBlank(email)) {
            wrapper.eq(User::getEmail, email.trim());
        }

        // 只在 role 不为 null 时进行查询
        if (role != null) {
            wrapper.eq(User::getRole, role);
        }

        // 只在 status 不为 null 时进行查询
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }

        // 按创建时间倒序排序
        wrapper.orderByDesc(User::getUpdateTime);

        return wrapper;
    }


}