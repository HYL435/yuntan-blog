package com.yuntan.notify.consumer;

import com.yuntan.common.constant.MqConstants;
import com.yuntan.common.domain.EmailMsgDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailConsumer {

    public final JavaMailSender mailSender; // Spring Boot 自动注入的发送器

    @Value("${spring.mail.username}") // 获取发件人账号
    private String from;

    /**
     * 监听邮件队列
     */
    @RabbitListener(queues = MqConstants.EMAIL_QUEUE)
    public void processEmail(EmailMsgDTO msg) {
        log.info("收到邮件发送请求：{}", msg);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);           // 发件人
            message.setTo(msg.getEmail());   // 收件人
            message.setSubject("【云坛博客】找回密码验证"); // 标题
            message.setText("您好，您的验证码是：" + msg.getCode() + "，有效期5分钟。请勿泄露给他人。"); // 内容

            mailSender.send(message);
            log.info("邮件发送成功：{}", msg.getEmail());

        } catch (Exception e) {
            log.error("邮件发送失败：{}", e.getMessage());
            // 这里可以考虑由 RabbitMQ 重试机制处理，或者记录到死信队列
        }
    }
}