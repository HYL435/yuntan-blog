package com.yuntan.comment;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.yuntan")
@MapperScan("com.yuntan.comment.mapper")
@EnableFeignClients(basePackages = {"com.yuntan.api.client"})
public class BlogCommentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogCommentServiceApplication.class, args);
    }

}
