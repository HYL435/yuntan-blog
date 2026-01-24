package com.yuntan.gateway.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.yuntan.common.constant.MessageConstant;
import com.yuntan.common.exception.UnauthorizedException;
import com.yuntan.gateway.config.JwtProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

@Slf4j
@Component
@Data
public class JwtUtil {

    private final JwtProperties jwtProperties;
    private final PublicKey publicKey;
    private final JWTSigner jwtSigner;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        try {
            // 1. 加载 JKS 文件
            ClassPathResource resource = new ClassPathResource(jwtProperties.getLocation());
            KeyStore keyStore = KeyStore.getInstance("JKS");
            try (InputStream inputStream = resource.getStream()) {
                keyStore.load(inputStream, jwtProperties.getPassword().toCharArray());
            }

            // 2. 获取私钥
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(
                    jwtProperties.getAlias(),
                    jwtProperties.getPassword().toCharArray()
            );

            // 3. 获取公钥
            this.publicKey = keyStore.getCertificate(jwtProperties.getAlias()).getPublicKey();

            // 4. 构建标准 KeyPair (公钥+私钥)
            KeyPair keyPair = new KeyPair(this.publicKey, privateKey);

            // 5. 构建签名器 (核心修正：直接使用字符串 "rs256"，传入 KeyPair)
            this.jwtSigner = JWTSignerUtil.createSigner("rs256", keyPair);

        } catch (Exception e) {
            log.error("初始化JWT工具失败，请检查JKS文件配置", e);
            throw new RuntimeException("JWT初始化失败", e);
        }
    }

    /**
     * 创建 Access Token
     */
    public String createToken(Long userId) {
        return createToken(userId, jwtProperties.getTokenTtl());
    }

    /**
     * 创建 Refresh Token
     */
    public String createRefreshToken(Long userId) {
        return createToken(userId, jwtProperties.getRefreshTtl());
    }

    /**
     * 创建 Token 通用方法
     */
    public String createToken(Long userId, Long ttl) {
        // 计算过期时间
        Date expireDate = new Date(System.currentTimeMillis() + ttl);

        return JWT.create()
                // 1. 设置 Payload (载荷)
                .setPayload("user", userId)
                // 2. 设置标准声明
                .setIssuer(jwtProperties.getIssuer())   // 签发者
                .setSubject(jwtProperties.getSubject()) // 主题
                .setIssuedAt(new Date())                // 签发时间
                .setExpiresAt(expireDate)               // 过期时间
                // 3. 签名
                .setSigner(jwtSigner)
                .sign();
    }

    /**
     * 解析并校验 Token
     */
    public Long parseToken(String token) {
        // 1. 校验非空
        if (token == null) {
            throw new UnauthorizedException(MessageConstant.USER_NOT_LOGIN);
        }

        // 2. 去除前缀 (如果有配置前缀，例如 "Bearer ")
        String prefix = jwtProperties.getPrefix();
        if (prefix != null && token.startsWith(prefix)) {
            token = token.substring(prefix.length()).trim();
        }

        JWT jwt;
        try {
            // 3. 解析并验证签名
            jwt = JWT.of(token).setSigner(jwtSigner);
            if (!jwt.verify()) {
                throw new UnauthorizedException("无效的Token，签名不匹配");
            }
        } catch (Exception e) {
            throw new UnauthorizedException("无效的Token格式", e);
        }

        // 4. 校验过期时间
        try {
            JWTValidator.of(jwt).validateDate(DateUtil.date()); // 校验 exp 字段
        } catch (ValidateException e) {
            throw new UnauthorizedException("Token已过期");
        }

        // 5. 获取并转换数据
        Object userPayload = jwt.getPayload("user");
        if (userPayload == null) {
            throw new UnauthorizedException("无效的Token，缺少用户信息");
        }

        try {
            return Long.valueOf(userPayload.toString());
        } catch (NumberFormatException e) {
            throw new UnauthorizedException("无效的Token，用户ID格式错误");
        }
    }
}
