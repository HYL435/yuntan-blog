package com.yuntan.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "yuntan.jwt")
public class JwtProperties {

    /** JKS密钥文件路径 */
    private String location;
    /** JKS密钥库别名 */
    private String alias;
    /** JKS密码 */
    private String password;
    /** 令牌有效期（毫秒） */
    private Long tokenTtl;
    /** 刷新令牌有效期（毫秒） */
    private Long refreshTtl;
    /** JWT签发者 */
    private String issuer;
    /** JWT主题（服务标识） */
    private String subject;
    /** JWT请求头名称 */
    private String header;
    /** JWT令牌前缀 */
    private String prefix;

}
