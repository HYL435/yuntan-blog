package com.yuntan.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 配置类，定义指定泛型的 RedisTemplate Bean
 */
@Configuration
public class RedisConfig {

    /**
     * 配置 RedisTemplate<String, Object> 类型的 Bean
     * 解决泛型匹配问题，同时配置序列化方式（避免乱码）
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        // 创建 RedisTemplate 对象
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // 设置连接工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 配置 key 的序列化方式（String 序列化）
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        // 配置 value 的序列化方式（JSON 序列化，避免乱码）
        GenericJackson2JsonRedisSerializer jsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        redisTemplate.setValueSerializer(jsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jsonRedisSerializer);

        // 初始化 RedisTemplate
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}