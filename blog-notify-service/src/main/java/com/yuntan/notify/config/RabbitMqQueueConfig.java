package com.yuntan.notify.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 队列声明配置类
 * 启动时自动创建所需队列，避免手动创建
 */
@Configuration
public class RabbitMqQueueConfig {

    /**
     * 声明 email.code.queue 队列
     */
    @Bean
    public Queue emailCodeQueue() {
        // 参数说明：
        // 1. 队列名：email.code.queue
        // 2. 是否持久化：true（关键，确保队列重启不丢失）
        // 3. 是否排他：false
        // 4. 是否自动删除：false
        return new Queue("email.code.queue", true, false, false);
    }

    // 如果还有其他缺失的队列，可在此处继续声明
    // @Bean
    // public Queue otherQueue() {
    //     return new Queue("other.queue", true);
    // }
}