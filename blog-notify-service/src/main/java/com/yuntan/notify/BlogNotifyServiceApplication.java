package com.yuntan.notify;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.yuntan")
@MapperScan("com.yuntan.notify.mapper")
public class BlogNotifyServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogNotifyServiceApplication.class, args);
    }

}
