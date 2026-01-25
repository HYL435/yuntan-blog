package com.yuntan.gateway.response;

import lombok.Data;

import java.time.LocalDateTime;

// 错误响应体类
@Data
public class ErrorResponse {

    private int code;
    private String message;
    private LocalDateTime timestamp;
    private String path;

    public ErrorResponse(int code, String message, LocalDateTime timestamp, String path) {
        this.code = code;
        this.message = message;
        this.timestamp = timestamp;
        this.path = path;
    }

}