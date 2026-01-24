package com.yuntan.gateway.filters;

import com.yuntan.gateway.config.AuthProperties;
import com.yuntan.gateway.utils.JwtUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * 网关全局过滤器 - 统一鉴权
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final AuthProperties authProperties;
    private final JwtUtil jwtUtil;

    // 1. 使用 WebFlux 专用的路径解析器实例
    private final PathPatternParser pathPatternParser = PathPatternParser.defaultInstance;

    // 2. 缓存编译后的路径模式，避免每次请求重复解析字符串
    private final List<PathPattern> excludePatterns = new ArrayList<>();

    /**
     * 初始化时将配置的 String 路径预编译为 PathPattern
     * 注意：如果 AuthProperties 支持动态刷新（@RefreshScope），需要监听刷新事件更新此列表
     */
    @PostConstruct
    public void initPatterns() {
        if (!CollectionUtils.isEmpty(authProperties.getExcludePaths())) {
            authProperties.getExcludePaths().forEach(path -> {
                // 解析并添加到缓存列表
                excludePatterns.add(pathPatternParser.parse(path));
            });
            log.info("鉴权白名单加载完毕，共 {} 条规则", excludePatterns.size());
        }
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // 3. 校验白名单
        // 直接使用 RequestPath 对象，避免 toString() 造成的性能损耗
        if (isExclude(request.getPath())) {
            return chain.filter(exchange);
        }

        // 4. 获取 Token
        // getFirst 自动处理 List 为空的情况，返回 null
        String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // 5. 校验 Token 格式与存在性
        if (!StringUtils.hasText(token)) {
            log.warn("鉴权失败：未携带 Token，Path: {}", request.getPath());
            return unauthorized(exchange);
        }

        // 6. 处理 Bearer 前缀 (标准规范)
        // 现在的网关通常需要处理前端传来的 "Bearer eyJhb..." 格式
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 7. 解析与验证
        Long userId;
        try {
            userId = jwtUtil.parseToken(token);
        } catch (Exception e) {
            log.error("鉴权失败：Token 无效或已过期，Path: {}, Error: {}", request.getPath(), e.getMessage());
            return unauthorized(exchange);
        }

        // 8. 传递用户信息 (Header传递)
        // 使用 mutate 传递是标准的做法
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(builder -> builder.header("user-info", userId.toString()))
                .build();

        return chain.filter(mutatedExchange);
    }

    /**
     * 判断路径是否在白名单中
     * 使用 PathPattern 进行对象级匹配，性能优于 AntPathMatcher
     *
     * @param pathContainer 请求路径对象
     */
    private boolean isExclude(PathContainer pathContainer) {
        if (CollectionUtils.isEmpty(excludePatterns)) {
            return false;
        }
        // 遍历预编译好的 Pattern 进行匹配
        for (PathPattern pattern : excludePatterns) {
            if (pattern.matches(pathContainer)) {
                return true;
            }
        }
        return false;
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        // 状态码设置为 401 未授权
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        // 结束请求，不继续向下游服务转发，直接响应
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        // 优先级设置，足够高以优先拦截，但需根据业务链调整
        return -1;
    }
}