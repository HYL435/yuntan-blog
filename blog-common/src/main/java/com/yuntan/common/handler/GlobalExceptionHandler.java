package com.yuntan.common.handler;

import com.yuntan.common.domain.Result;
import com.yuntan.common.exception.BusinessException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

/**
 * 全局异常处理器
 * @RestControllerAdvice 等价于 @ControllerAdvice + @ResponseBody
 * 主要用于捕获处理不同类型的异常，并返回标准化的错误响应
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理自定义业务异常
     * @param e 捕获的业务异常
     * @return 标准化错误结果
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        // 记录业务异常日志
        logger.error("业务异常发生: {}", e.getMessage(), e);
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常（@RequestBody 注解的参数）
     * @param e 捕获的参数校验异常
     * @return 标准化错误结果，包含参数校验失败信息
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        // 获取第一个校验失败的字段和提示信息
        String message = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        // 记录参数校验异常日志
        logger.warn("参数校验失败：{}", message);
        return Result.error(400, "参数校验失败：" + message);
    }

    /**
     * 处理参数校验异常（@RequestParam 注解的参数）
     * @param e 捕获的参数校验异常
     * @return 标准化错误结果，包含参数校验失败信息
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<?> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().iterator().next().getMessage();
        // 记录参数校验异常日志
        logger.warn("参数校验失败：{}", message);
        return Result.error(400, "参数校验失败：" + message);
    }

    /**
     * 处理请求参数绑定异常（如：@RequestParam 参数绑定失败）
     * @param e 捕获的请求参数绑定异常
     * @return 标准化错误结果，包含绑定失败信息
     */
    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e) {
        String message = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        // 记录参数绑定异常日志
        logger.warn("参数绑定失败：{}", message);
        return Result.error(400, "参数绑定失败：" + message);
    }

    /**
     * 处理所有未捕获的系统异常
     * @param e 捕获的系统异常
     * @return 标准化错误结果，服务器内部错误
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 设置 HTTP 响应状态码为 500
    public Result<?> handleException(Exception e) {
        // 生产环境建议打印日志，不要返回具体异常信息
        if (logger.isErrorEnabled()) {
            logger.error("服务器内部错误", e); // 记录完整的异常堆栈信息
        }
        // 开发环境方便调试，生产环境需删除堆栈信息
        return Result.error(500, "服务器内部错误，请联系管理员");
    }
}
