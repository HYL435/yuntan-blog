package com.yuntan.user.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@Schema(description = "用户实体")
public class User {

    /**
     * 用户id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "用户ID", example = "1")
    private Long id;

    /**
     * 用户名
     */
    @Schema(
            description = "用户名",
            example = "zhangsan",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 3,
            maxLength = 20
    )
    private String username;

    /**
     * 密码（8位及以上）
     */
    @Schema(
            description = "密码（加密存储）",
            example = "********",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 8,
            maxLength = 100
    )
    private String password;

    /**
     * 昵称
     */
    @Schema(
            description = "用户昵称",
            example = "张三",
            maxLength = 50
    )
    private String nickname;

    /**
     * 邮箱地址
     */
    @Schema(
            description = "邮箱地址",
            example = "zhangsan@example.com",
            pattern = "^[A-Za-z0-9+_.-]+@(.+)$"
    )
    private String email;

    /**
     * 手机号
     */
    @Schema(
            description = "手机号码",
            example = "13800138000",
            pattern = "^1[3-9]\\d{9}$"
    )
    private String phone;

    /**
     * 头像URL
     */
    @Schema(
            description = "头像URL地址",
            example = "https://example.com/avatar.jpg",
            maxLength = 500
    )
    private String image;

    /**
     * 个人简介
     */
    @Schema(
            description = "个人简介",
            example = "一名热爱技术的开发者",
            maxLength = 500
    )
    private String intro;

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

    /**
     * 账号状态：1-正常，2-禁用
     */
    @Schema(
            description = "账号状态：1-正常，2-禁用",
            example = "1",
            allowableValues = {"1", "2"},
            defaultValue = "1"
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
     * 最后登录时间
     */
    @Schema(
            description = "最后登录时间",
            example = "2024-01-15 10:30:00"
    )
    private LocalDateTime lastLoginTime;

    /**
     * 创建时间
     */
    @Schema(
            description = "创建时间",
            example = "2024-01-01 00:00:00"
    )
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(
            description = "最后更新时间",
            example = "2024-01-15 09:00:00"
    )
    private LocalDateTime updateTime;
}