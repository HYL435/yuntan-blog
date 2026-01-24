package com.yuntan.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "yuntan.auth")
public class AuthProperties {
    /**
     * 需要进行认证的路径
     */
    private List<String> includePaths;
    /**
     * 不需要进行认证的路径
     */
    private List<String> excludePaths;
}
