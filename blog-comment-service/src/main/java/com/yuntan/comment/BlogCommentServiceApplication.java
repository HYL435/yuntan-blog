package com.yuntan.comment;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yuntan.comment.mapper")
public class BlogCommentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogCommentServiceApplication.class, args);
    }

}
