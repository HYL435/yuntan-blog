package com.yuntan.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailMsgDTO implements Serializable {
    private String email;   // 接收邮箱
    private String code;    // 验证码
    private Integer type;   // 业务类型：1-注册，2-找回密码
}