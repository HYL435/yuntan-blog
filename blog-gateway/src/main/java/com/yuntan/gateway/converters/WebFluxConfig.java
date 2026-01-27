package com.yuntan.gateway.converters;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * 适配 Spring Cloud Gateway (WebFlux) 的消息转换器配置
 * 替代原来基于 Spring MVC 的 WebMvcConfigurer 配置
 */
@Configuration
@EnableWebFlux // 启用 WebFlux 配置（Gateway 项目可选，但显式声明更清晰）
public class WebFluxConfig implements WebFluxConfigurer {

    /**
     * 配置 WebFlux 的消息编解码器（替代 MVC 的 HttpMessageConverter）
     */
    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        // 获取默认的 ObjectMapper（Spring 自动配置的）
        ObjectMapper objectMapper = new ObjectMapper();

        // 配置 JSON 编码器和解码器（对应原来的 MappingJackson2HttpMessageConverter）
        Jackson2JsonEncoder encoder = new Jackson2JsonEncoder(objectMapper);
        Jackson2JsonDecoder decoder = new Jackson2JsonDecoder(objectMapper);

        // 将自定义的编解码器添加到配置中
        configurer.defaultCodecs().jackson2JsonEncoder(encoder);
        configurer.defaultCodecs().jackson2JsonDecoder(decoder);

        // 如果你需要保留其他默认的编解码器，这一步会自动保留，无需额外处理
    }

    // 可选：如果需要自定义 ObjectMapper（比如日期格式化、空值处理等），可以单独声明 Bean
    /*
    @Bean
    public ObjectMapper customObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // 示例：配置日期格式化
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        // 示例：忽略空值字段
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }
    */
}