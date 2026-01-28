package com.yuntan.article;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.yuntan")
@MapperScan("com.yuntan.article.mapper")
public class BlogArticleServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogArticleServiceApplication.class, args);
    }

}
