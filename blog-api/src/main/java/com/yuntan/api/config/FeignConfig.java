package com.yuntan.api.config;

import com.yuntan.common.context.BaseContext; // 你的上下文工具类
import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;

public class FeignConfig {

    @Bean
    public Logger.Level feignLoggerLevel() {
        // 开发环境用 FULL，生产环境建议改成 BASIC
        return Logger.Level.FULL; 
    }

    @Bean
    public RequestInterceptor userInfoInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // 1. 获取当前登录用户 ID
                Long userId = BaseContext.getUserId();
                // 2. 如果存在，放入请求头，传递给下游微服务
                if (userId != null) {
                    template.header("user-info", userId.toString());
                }
            }
        };
    }
}