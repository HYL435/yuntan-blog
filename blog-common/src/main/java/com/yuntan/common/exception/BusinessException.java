package com.yuntan.common.exception;

import com.yuntan.common.constant.MessageConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 业务异常类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BusinessException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final int code; // 自定义业务错误码
    private final int httpStatus; // HTTP 状态码

    // 原有构造器：保留用于自定义 code/httpStatus 的场景
    public BusinessException(int code, String message, int httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    // 新增：无参构造器，使用默认 code/httpStatus
    public BusinessException() {
        super("Business exception");
        this.code = 50000;
        this.httpStatus = 500;
    }

    // 新增：仅 message（常见子类调用场景）
    public BusinessException(String message) {
        super(message);
        this.code = 50000;
        this.httpStatus = 500;
    }

    // 新增：message + cause
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = 50000;
        this.httpStatus = 500;
    }

    // 新增：仅 cause
    public BusinessException(Throwable cause) {
        super(cause);
        this.code = 50000;
        this.httpStatus = 500;
    }

    // 静态工厂方法：通用错误（400 Bad Request）
    public static BusinessException badRequest(String message) {
        return new BusinessException(40000, message, 400);
    }

    // 用户名已存在
    public static BusinessException usernameExist() {
        return new BusinessException(40001, MessageConstant.USERNAME_EXIST, 400);
    }

    // 邮箱已存在
    public static BusinessException emailExist() {
        return new BusinessException(40002, MessageConstant.EMAIL_EXIST, 400);
    }

    // 登录失败（账号或密码错误）
    public static BusinessException loginFailed() {
        return new BusinessException(40100, MessageConstant.ACCOUNT_OR_PASSWORD_ERROR, 401);
    }

    // 权限不足
    public static BusinessException forbidden() {
        return new BusinessException(40300, MessageConstant.OPERATION_FORBIDDEN, 403);
    }

    // 资源未找到
    public static BusinessException notFound(String resource) {
        return new BusinessException(40400, resource + MessageConstant.NOT_FOUND, 404);
    }

    // 系统内部错误（500）
    public static BusinessException internalError(String message) {
        return new BusinessException(50000, MessageConstant.SYSTEM_ERROR + message, 500);
    }
}