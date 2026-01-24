package com.yuntan.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackages = "com.yuntan")
@MapperScan("com.yuntan.user.mapper")
public class BlogUserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlogUserServiceApplication.class, args);
    }
}
