package com.yuntan.article.service.impl;

import com.yuntan.article.constant.RedisConstant;
import com.yuntan.article.domain.po.ArticleCollect;
import com.yuntan.article.domain.po.ArticleLike;
import com.yuntan.article.domain.po.ArticleView;
import com.yuntan.article.mapper.InteractMapper;
import com.yuntan.article.service.InteractService;
import com.yuntan.common.context.BaseContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InteractServiceImpl implements InteractService {

    private final InteractMapper interactMapper;
    private final StringRedisTemplate redisTemplate;

    /**
     * 文章点赞
     */
    @Override
    public void like(Long articleId) {
        Long userId = BaseContext.getUserId();

        // 检查是否已点赞
        boolean isLiked = interactMapper.isLike(articleId, userId) > 0;

        if (isLiked) {
            // 取消点赞
            interactMapper.deleteLike(articleId, userId);
            // 更新Redis计数
            updateLikeCount(articleId, -1);
        } else {
            // 添加点赞记录
            ArticleLike like = ArticleLike.builder()
                    .articleId(articleId)
                    .userId(userId)
                    .createTime(LocalDateTime.now())
                    .build();
            interactMapper.like(like);
            // 更新Redis计数
            updateLikeCount(articleId, 1);
        }
    }

    /**
     * 文章收藏
     */
    @Override
    public void collect(Long articleId) {
        Long userId = BaseContext.getUserId();

        // 检查是否已收藏
        boolean isCollected = interactMapper.isCollect(articleId, userId) > 0;

        if (isCollected) {
            // 取消收藏
            interactMapper.deleteCollect(articleId, userId);
            // 更新Redis计数
            updateCollectCount(articleId, -1);
        } else {
            // 添加收藏记录
            ArticleCollect collect = ArticleCollect.builder()
                    .articleId(articleId)
                    .userId(userId)
                    .createTime(LocalDateTime.now())
                    .build();
            interactMapper.collect(collect);
            // 更新Redis计数
            updateCollectCount(articleId, 1);
        }
    }

    /**
     * 文章浏览
     */
    @Override
    public void view(Long articleId) {
        // 获取用户ID，如果没有登录则为null
        Long userId = BaseContext.getUserId();
        // 获取IP地址（需要从请求上下文中获取）
        String ip = getCurrentRequestIp(); // 这个方法需要你自己实现

        // 添加浏览记录到数据库
        addViewRecord(articleId, userId, ip);

        // 更新Redis计数
        updateViewCount(articleId, 1);
    }

    /**
     * 添加浏览记录到数据库
     */
    private void addViewRecord(Long articleId, Long userId, String ip) {

         ArticleView view = ArticleView.builder()
                 .articleId(articleId)
                 .userId(userId)
                 .ip(ip)
                 .createTime(LocalDateTime.now())
                 .build();

         interactMapper.addView(view); // 需要在mapper中添加对应方法
    }

    /**
     * 获取当前请求的IP地址
     */
    private String getCurrentRequestIp() {
        // 从请求上下文中获取IP地址
        // 这通常需要注入 HttpServletRequest 或使用其他框架提供的工具类
        // 示例实现：
        try {
            HttpServletRequest request = BaseContext.getRequest(); // 假设有一个RequestHolder工具类
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            return ip;
        } catch (Exception e) {
            // 如果无法获取IP，返回默认值
            return "0.0.0.0";
        }
    }

    /**
     * 更新阅读量
     */
    private void updateViewCount(Long articleId, int delta) {
        String key = RedisConstant.ARTICLE_HASH_PREFIX + articleId;

        // 原子递增Redis Hash中的viewCount
        redisTemplate.opsForHash().increment(key, "viewCount", delta);

        // 将ID加入脏数据集合，用于后续同步到数据库
        redisTemplate.opsForSet().add(RedisConstant.DIRTY_SET_KEY, String.valueOf(articleId));
    }

    /**
     * 更新收藏数
     */
    private void updateCollectCount(Long articleId, int delta) {
        String key = RedisConstant.ARTICLE_HASH_PREFIX + articleId;

        // 原子递增Redis Hash中的collectCount
        redisTemplate.opsForHash().increment(key, "collectCount", delta);

        // 将ID加入脏数据集合，用于后续同步到数据库
        redisTemplate.opsForSet().add(RedisConstant.DIRTY_SET_KEY, String.valueOf(articleId));
    }

    /**
     * 更新点赞数
     */
    private void updateLikeCount(Long articleId, int delta) {
        String key = RedisConstant.ARTICLE_HASH_PREFIX + articleId;

        // 原子递增Redis Hash中的likeCount
        redisTemplate.opsForHash().increment(key, "likeCount", delta);

        // 将ID加入脏数据集合，用于后续同步到数据库
        redisTemplate.opsForSet().add(RedisConstant.DIRTY_SET_KEY, String.valueOf(articleId));
    }

    /**
     * 检查用户是否已点赞
     */
    public boolean hasUserLiked(Long userId, Long articleId) {
        return interactMapper.isLike(articleId, userId) > 0;
    }

    /**
     * 检查用户是否已收藏
     */
    public boolean hasUserCollected(Long userId, Long articleId) {
        return interactMapper.isCollect(articleId, userId) > 0;
    }
}
