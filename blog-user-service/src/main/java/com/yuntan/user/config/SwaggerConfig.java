package com.yuntan.user.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    // =================================================
    // 1. 读取 YAML 中的配置信息
    // =================================================
    @Value("${api.info.title}")
    private String title;

    @Value("${api.info.description}")
    private String description;

    @Value("${api.info.version}")
    private String version;

    @Value("${api.info.contact-name}")
    private String contactName;

    @Value("${api.info.contact-email}")
    private String contactEmail;

    @Value("${api.info.contact-url}")
    private String contactUrl;

    // =================================================
    // 2. 配置全局 OpenAPI 元数据 (标题、作者等)
    // =================================================
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(title)
                        .description(description)
                        .version(version)
                        .contact(new Contact()
                                .name(contactName)
                                .email(contactEmail)
                                .url(contactUrl))
                );
    }

    // =================================================
    // 3. 配置接口分组 (GroupedOpenApi)
    // =================================================

    /**
     * 分组1：前台接口
     * 扫描 /api/front/** 路径
     */
    @Bean
    public GroupedOpenApi frontApi() {
        return GroupedOpenApi.builder()
                .group("前台接口")
                .pathsToMatch("/front/**")
                .build();
    }

    /**
     * 分组2：后台管理接口
     * 扫描 /api/admin/** 路径
     */
    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("后台管理")
                .pathsToMatch("/admin/**")
                .build();
    }

    /**
     * 分组3：全部接口 (方便调试用)
     * 扫描 /** 路径
     */
    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("全部接口")
                .pathsToMatch("/**")
                .build();
    }
}