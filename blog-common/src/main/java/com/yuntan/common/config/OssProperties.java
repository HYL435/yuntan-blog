package com.yuntan.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@ConfigurationProperties(prefix = "aliyun.oss")
@Data
@RefreshScope
public class OssProperties {
    private String endpoint;
    private String bucketName;
    private String accessKeyId;
    private String accessKeySecret;
}