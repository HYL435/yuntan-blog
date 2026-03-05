package com.yuntan.manage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.yuntan")
@MapperScan("com.yuntan.manage.mapper")
public class BlogManageServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogManageServiceApplication.class, args);
    }

}
