package com.yuntan.gateway.hanlder;

import com.yuntan.common.domain.Result;
import com.yuntan.common.exception.BusinessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

/**
 * 网关专属：WebFlux 全局异常处理器（响应式）
 */
@ControllerAdvice // WebFlux用@ControllerAdvice（无ResponseBody）
public class GlobalWebExceptionHandler implements WebExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalWebExceptionHandler.class);
    private final ObjectMapper objectMapper;

    // 注入网关配置的自定义ObjectMapper
    public GlobalWebExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 核心方法：处理所有WebFlux异常
     */
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Result<?> result;
        // 区分异常类型，返回标准化Result
        if (ex instanceof BusinessException) {
            BusinessException e = (BusinessException) ex;
            logger.error("网关业务异常: {}", e.getMessage(), e);
            result = Result.error(e.getCode(), e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST);
        } else if (ex instanceof ResponseStatusException) {
            ResponseStatusException e = (ResponseStatusException) ex;
            logger.warn("网关请求异常: {}", e.getReason());
            result = Result.error(e.getStatusCode().value(), e.getReason());
            response.setStatusCode(e.getStatusCode());
        } else {
            logger.error("网关系统异常", ex);
            result = Result.error(500, "网关内部错误，请联系管理员");
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 将Result转为JSON字符串，写入响应体
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(result);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            logger.error("异常响应序列化失败", e);
            return response.setComplete();
        }
    }

    // 可选：单独处理特定异常（简化版）
    @ExceptionHandler(BusinessException.class)
    public Mono<Void> handleBusinessException(ServerWebExchange exchange, BusinessException e) {
        return handle(exchange, e);
    }
}