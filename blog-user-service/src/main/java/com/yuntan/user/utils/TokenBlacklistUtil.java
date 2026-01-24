package com.yuntan.user.utils;


import com.yuntan.common.constant.RedisKeyConstant;
import com.yuntan.user.config.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class TokenBlacklistUtil {

    private final StringRedisTemplate stringRedisTemplate;

    private final JwtProperties jwtProperties;

    // 1. 登出：将Token加入黑名单（核心）
    public void addLogoutToken(String token) {
        String key = RedisKeyConstant.BLACKLIST_TOKEN_PREFIX + token;
        // 存入Redis，设置过期时间
        stringRedisTemplate.opsForValue().set(key, "logout", jwtProperties.getTokenTtl(), TimeUnit.MILLISECONDS);
    }

    // 2. 校验：Token是否在黑名单
    public boolean isLogoutToken(String token) {
        String key = RedisKeyConstant.BLACKLIST_TOKEN_PREFIX + token;
        // 直接判断Key是否存在，性能最优
        return stringRedisTemplate.hasKey(key);
    }
}