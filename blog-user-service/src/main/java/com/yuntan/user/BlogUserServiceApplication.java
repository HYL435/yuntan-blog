package com.yuntan.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;

@SpringBootApplication
@MapperScan("com.yuntan.user.mapper")
public class BlogUserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlogUserServiceApplication.class, args);
    }
}
