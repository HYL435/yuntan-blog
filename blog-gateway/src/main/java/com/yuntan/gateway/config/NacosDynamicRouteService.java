package com.yuntan.gateway.config;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.listener.Listener; // Nacos配置监听器接口
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference; // Jackson泛型类型引用
import com.fasterxml.jackson.databind.ObjectMapper; // JSON序列化/反序列化工具
import jakarta.annotation.PostConstruct; // JSR-250注解，用于初始化方法
import lombok.RequiredArgsConstructor; // Lombok注解，生成有参构造函数
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired; // Spring依赖注入注解
import org.springframework.cloud.gateway.event.RefreshRoutesEvent; // Gateway路由刷新事件
import org.springframework.cloud.gateway.route.RouteDefinition; // Gateway路由定义实体
import org.springframework.cloud.gateway.route.RouteDefinitionWriter; // 路由定义写入器
import org.springframework.context.ApplicationEventPublisher; // 应用事件发布者
import org.springframework.context.ApplicationEventPublisherAware; // 实现此接口可获得事件发布能力
import org.springframework.stereotype.Component; // Spring组件注解
import reactor.core.publisher.Mono; // Reactor响应式编程单对象流

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor; // 执行器接口

/**
 * Nacos 动态路由监听器
 *
 * 该服务用于从Nacos配置中心动态获取网关路由配置，并在配置变更时自动更新Spring Cloud Gateway的路由规则
 * 支持运行时动态添加、删除、修改路由，无需重启网关服务
 */
@Component
@RequiredArgsConstructor
public class NacosDynamicRouteService implements ApplicationEventPublisherAware {

    private static final Logger log = LoggerFactory.getLogger(NacosDynamicRouteService.class);

    // Nacos 配置文件的 DataId
    public static final String DATA_ID = "gateway-router.json";
    // Nacos 配置组
    public static final String GROUP = "DEFAULT_GROUP";

    private final RouteDefinitionWriter routeDefinitionWriter; // 路由定义写入器，用于添加/删除路由

    private final NacosConfigManager nacosConfigManager; // Nacos配置管理器

    private ApplicationEventPublisher applicationEventPublisher; // 应用事件发布器

    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON对象映射器

    // 缓存当前路由ID，用于更新时先删除旧路由
    private final List<String> routeIds = new ArrayList<>();

    /**
     * 设置应用事件发布器
     * 实现ApplicationEventPublisherAware接口的方法，Spring会自动注入事件发布器
     *
     * @param applicationEventPublisher 应用事件发布器
     */
    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * 项目启动时加载配置并添加监听
     *
     * 1. 从Nacos获取初始路由配置
     * 2. 添加Nacos配置监听器，当配置发生变化时自动更新路由
     */
    @PostConstruct
    public void init() {
        try {
            // 1. 获取 Nacos 初始配置
            String configInfo = nacosConfigManager.getConfigService().getConfig(DATA_ID, GROUP, 5000);
            if (configInfo != null) {
                updateRoutes(configInfo);
            }

            // 2. 添加监听器
            nacosConfigManager.getConfigService().addListener(DATA_ID, GROUP, new Listener() {

                /**
                 * 获取执行器，返回null表示使用默认执行器
                 *
                 * @return 执行器实例，null表示使用默认执行器
                 */
                @Override
                public Executor getExecutor() {
                    return null;
                }

                /**
                 * 接收配置变更信息的回调方法
                 * 当Nacos中的路由配置发生变更时，会触发此方法
                 *
                 * @param configInfo 变更后的配置信息
                 */
                @Override
                public void receiveConfigInfo(String configInfo) {
                    log.info("接收到 Nacos 路由更新配置: {}", configInfo);
                    updateRoutes(configInfo);
                }
            });

        } catch (Exception e) {
            log.error("加载动态路由异常", e);
        }
    }

    /**
     * 更新路由逻辑
     *
     * 1. 解析传入的JSON配置为路由定义列表
     * 2. 删除当前缓存的所有路由
     * 3. 添加新解析出的路由
     * 4. 发布路由刷新事件，使更改生效
     *
     * @param configInfo 包含路由配置的JSON字符串
     */
    public void updateRoutes(String configInfo) {
        try {
            // 将JSON配置字符串解析为路由定义列表
            List<RouteDefinition> gatewayRouteDefinitions = objectMapper.readValue(configInfo, new TypeReference<List<RouteDefinition>>() {});

            // 1. 删除旧路由
            for (String id : routeIds) {
                routeDefinitionWriter.delete(Mono.just(id)).subscribe();
            }
            routeIds.clear();

            // 2. 添加新路由
            for (RouteDefinition routeDefinition : gatewayRouteDefinitions) {
                routeDefinitionWriter.save(Mono.just(routeDefinition)).subscribe();
                routeIds.add(routeDefinition.getId());
            }

            // 3. 发布事件，通知 Gateway 刷新路由
            applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
            log.info("路由更新成功，共 {} 条", gatewayRouteDefinitions.size());

        } catch (JsonProcessingException e) {
            log.error("解析路由配置失败", e);
        }
    }
}
