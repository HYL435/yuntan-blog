package com.yuntan.gateway.filters;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.alibaba.nacos.common.utils.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuntan.gateway.config.AuthProperties;
import com.yuntan.gateway.constant.KeyConstant;
import com.yuntan.gateway.response.ErrorResponse;
import com.yuntan.gateway.utils.JwtUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 全局鉴权过滤器：用于拦截所有经过网关的请求，验证 JWT Token 的有效性，并根据角色控制访问权限。
 * - 支持配置白名单路径（无需鉴权）
 * - 验证 Token 合法性（签名、过期等）
 * - 管理员接口（/admin）需角色 >= 1
 * - 将用户信息透传至下游服务
 */
@Component
@RequiredArgsConstructor // Lombok 自动生成构造函数注入（final 字段）
@Slf4j // 日志支持
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    // 注入配置属性（白名单路径等）
    private final AuthProperties authProperties;
    // 注入 JWT 工具类，用于解析 Token
    private final JwtUtil jwtUtil;
    // 路径匹配解析器（Spring Web 提供）
    private final PathPatternParser pathPatternParser = PathPatternParser.defaultInstance;
    // 存储从配置中加载的白名单路径模式
    private final List<PathPattern> excludePatterns = new ArrayList<>();
    // 用于将错误响应对象序列化为 JSON
    private final ObjectMapper objectMapper;

    /**
     * Bean 初始化完成后执行：加载白名单路径规则
     */
    @PostConstruct
    public void initPatterns() {
        // 如果配置中存在排除路径，则逐个解析为 PathPattern 对象
        if (!CollectionUtils.isEmpty(authProperties.getExcludePaths())) {
            authProperties.getExcludePaths().forEach(path -> {
                excludePatterns.add(pathPatternParser.parse(path));
            });
            log.info("鉴权白名单加载完毕，共 {} 条规则", excludePatterns.size());
        }
    }

    /**
     * 核心过滤逻辑：对每个请求进行鉴权处理
     *
     * @param exchange 当前请求上下文
     * @param chain    过滤器链，用于继续传递请求
     * @return 返回 Mono<Void> 表示异步处理完成
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("进入鉴权过滤器");

        ServerHttpRequest request = exchange.getRequest();
        log.info("【鉴权过滤器】当前请求路径：{}", request.getPath());

        // 1. 检查是否在白名单中（如 /login, /register 等）
        if (isExclude(request.getPath())) {
            return chain.filter(exchange); // 跳过鉴权，直接放行
        }

        // 2. 从请求头中获取 Authorization 字段（格式：Bearer <token>）
        String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // 3. 判断 Token 是否存在
        if (!StringUtils.hasText(token)) {
            log.warn("鉴权失败：未携带 Token，Path: {}", request.getPath());
            return unauthorized(exchange, "未携带 Token");
        }

        // 4. 去除 "Bearer " 前缀，提取纯 Token 字符串
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 5. 解析并验证 JWT Token
        Map<String, Object> payload;
        try {
            payload = jwtUtil.parseToken(token); // 可能抛出异常（如过期、签名错误）
        } catch (Exception e) {
            log.error("鉴权失败：Token 无效或已过期，Path: {}, Error: {}", request.getPath(), e.getMessage());
            return unauthorized(exchange, "令牌无效或已过期");
        }

        // 6. 特殊权限校验：如果请求路径包含 "/admin"，则要求角色为管理员（role <= 1）
        if (request.getPath().toString().contains("/admin")) {
            Integer role = (Integer) payload.get(KeyConstant.ROLE);
            if (role == null || role > 1) {
                log.warn("鉴权失败：权限不足，Path: {}, Role: {}", request.getPath(), role);
                return forbidden(exchange, "权限不足");
            }
        }

        // 7. 将用户关键信息（user-id, role）通过请求头透传给下游微服务
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(builder -> builder
                        .header("user-info", payload.get(KeyConstant.USER_ID).toString()) // 用户ID
                        .header("role", payload.get(KeyConstant.ROLE).toString())         // 用户角色
                )
                .build();

        // 8. 继续执行后续过滤器链
        return chain.filter(mutatedExchange);
    }

    /**
     * 判断当前请求路径是否匹配任意一个白名单规则
     *
     * @param pathContainer 当前请求路径容器
     * @return true 表示应跳过鉴权
     */
    private boolean isExclude(PathContainer pathContainer) {
        if (CollectionUtils.isEmpty(excludePatterns)) {
            return false;
        }
        for (PathPattern pattern : excludePatterns) {
            if (pattern.matches(pathContainer)) {
                return true;
            }
        }
        return false;
    }

    // ==================== 错误响应构建方法 ====================

    /**
     * 构建 401 Unauthorized 响应
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        return buildErrorResponse(exchange, HttpStatus.UNAUTHORIZED, message);
    }

    /**
     * 构建 403 Forbidden 响应
     */
    private Mono<Void> forbidden(ServerWebExchange exchange, String message) {
        return buildErrorResponse(exchange, HttpStatus.FORBIDDEN, message);
    }

    /**
     * 通用错误响应构建方法：返回标准化 JSON 错误体
     *
     * @param exchange 请求上下文
     * @param status   HTTP 状态码
     * @param message  错误提示信息
     * @return 写入响应体的 Mono 流
     */
    private Mono<Void> buildErrorResponse(ServerWebExchange exchange, HttpStatus status, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // 创建结构化错误响应对象
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),                    // 状态码
                message,                           // 错误消息
                LocalDateTime.now(),               // 时间戳
                exchange.getRequest().getURI().getPath() // 触发错误的路径
        );

        // 将错误对象序列化为 JSON 字节数组并写入响应
        try {
            byte[] responseBytes = objectMapper.writeValueAsBytes(errorResponse);
            return response.writeWith(Mono.just(response.bufferFactory().wrap(responseBytes)));
        } catch (Exception e) {
            log.error("错误响应序列化失败：{}", e.getMessage());
            // 若序列化失败，至少关闭连接避免挂起
            return response.setComplete();
        }
    }

    /**
     * 设置过滤器执行顺序：值越小，优先级越高。
     * 此处设为 -1，确保在其他过滤器（如路由过滤器）之前执行。
     */
    @Override
    public int getOrder() {
        return -1;
    }
}