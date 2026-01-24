package com.yuntan.common.constant;

/**
 * 信息提示常量类
 */
public class MessageConstant {

    // ==================== 登录相关 ====================
    public static final String PASSWORD_ERROR = "密码错误";
    public static final String ACCOUNT_NOT_FOUND = "用户名或邮箱不存在";
    public static final String ACCOUNT_OR_PASSWORD_ERROR = "用户名或密码错误";
    public static final String ACCOUNT_LOCKED = "账号被锁定";
    public static final String LOGIN_FAILED = "登录失败";
    public static final String USER_NOT_LOGIN = "用户未登录";

    // ==================== 注册相关 ====================
    public static final String USERNAME_EXIST = "用户名已存在";
    public static final String EMAIL_EXIST = "邮箱已被注册";
    public static final String EMAIL_FORMAT_ERROR = "邮箱格式不正确";
    public static final String PASSWORD_STRENGTH_INSUFFICIENT = "密码强度不足，需包含字母和数字，长度8-20位";
    public static final String USER_INFO_INCOMPLETE = "用户信息不完整";

    // ==================== 参数校验相关 ====================
    public static final String PARAMETER_INVALID = "参数无效";
    public static final String REQUIRED_FIELD_MISSING = "必填字段缺失";
    public static final String PHONE_FORMAT_ERROR = "手机号格式不正确";
    public static final String VERIFICATION_CODE_ERROR = "验证码错误";
    public static final String VERIFICATION_CODE_EXPIRED = "验证码已过期";

    // ==================== 资源操作相关 ====================
    public static final String RECORD_NOT_FOUND = "数据不存在";
    public static final String ALREADY_EXISTS = "已存在";
    public static final String OPERATION_FORBIDDEN = "权限不足，禁止操作";
    public static final String DATA_CONFLICT = "数据冲突，请稍后重试";

    // ==================== 文件上传相关 ====================
    public static final String UPLOAD_FAILED = "文件上传失败";
    public static final String FILE_SIZE_TOO_LARGE = "文件大小超出限制";
    public static final String FILE_TYPE_NOT_ALLOWED = "文件类型不支持";

    // ==================== 系统异常 ====================
    public static final String UNKNOWN_ERROR = "未知错误";
    public static final String SYSTEM_ERROR = "系统内部错误，请联系管理员";
    public static final String SERVER_BUSY = "服务器繁忙，请稍后再试";

    // ==================== 其他常见提示 ====================
    public static final String SUCCESS = "操作成功";
    public static final String FAILED = "操作失败";
    public static final String INVALID_TOKEN = "令牌无效或已过期";
    public static final String NOT_FOUND = "不存在";
    public static final String NICKNAME_FORMAT_INVALID = "昵称格式不正确，长度应在2-30个字符之间";
    public static final String OLD_PASSWORD_INCORRECT = "旧密码不正确";
    public static final String LOGOUT_FAILED = "登出失败";
    public static final String USER_NOT_FOUND = "用户未找到";
}