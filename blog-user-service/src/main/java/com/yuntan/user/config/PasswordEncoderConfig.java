package com.yuntan.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码加密配置类
 */
// 1. 配置类名改为 PasswordEncoderConfig（避免和Bean名/框架类重名）
@Configuration
public class PasswordEncoderConfig {

    // 2. 确保这个@Bean方法是唯一的，名字为passwordEncoder
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}