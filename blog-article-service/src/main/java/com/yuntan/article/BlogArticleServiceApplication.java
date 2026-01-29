package com.yuntan.article;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.yuntan")
@MapperScan("com.yuntan.article.mapper")
@EnableScheduling  // 启用定时任务
public class BlogArticleServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogArticleServiceApplication.class, args);
    }

}
