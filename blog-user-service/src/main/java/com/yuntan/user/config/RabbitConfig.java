package com.yuntan.user.config;

import com.yuntan.common.constant.MqConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // 定义交换机
    @Bean
    public DirectExchange emailExchange() {
        return new DirectExchange(MqConstants.EMAIL_EXCHANGE);
    }
    // 定义队列
    @Bean
    public Queue emailQueue() {
        return new Queue(MqConstants.EMAIL_QUEUE);
    }
    // 绑定
    @Bean
    public Binding bindingEmail() {
        return BindingBuilder.bind(emailQueue()).to(emailExchange()).with(MqConstants.EMAIL_ROUTING_KEY);
    }
    
    // 【重要】使用 JSON 序列化，否则消费者那边反序列化可能报错
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}