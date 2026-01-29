package com.yuntan.article.task;

import com.yuntan.article.constant.RedisConstant;
import com.yuntan.article.domain.dto.front.ArticleInteractDTO;
import com.yuntan.article.mapper.ArticleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class InteractSyncTask {

    private final StringRedisTemplate redisTemplate;
    // 注意：这里通常更新的是 article 主表，所以注入 ArticleMapper
    // 如果你的计数在另一张表，请注入对应的 Mapper
    private final ArticleMapper articleMapper;

    /**
     * 每 5 分钟执行一次同步
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void syncInteractData() {
        // 1. 从脏数据集合中弹出 ID (使用 pop，取出后即从 Set 中移除，防止重复处理)
        // 每次最多取 1000 条，防止一次处理数据过多
        Set<String> dirtyIds = (Set<String>) redisTemplate.opsForSet().pop(RedisConstant.DIRTY_SET_KEY, 1000);

        if (dirtyIds == null || dirtyIds.isEmpty()) {
            return;
        }

        log.info("开始同步文章互动数据，涉及文章数: {}", dirtyIds.size());

        List<ArticleInteractDTO> updateList = new ArrayList<>();

        // 2. 遍历 ID，从 Redis Hash 中读取最新计数
        for (String idStr : dirtyIds) {
            long articleId = Long.parseLong(idStr);
            String key = RedisConstant.ARTICLE_HASH_PREFIX + articleId;

            // 批量获取 viewCount, likeCount, collectCount
            // 顺序要和 Arrays.asList 中的字段名一致
            List<Object> counts = redisTemplate.opsForHash().multiGet(key, Arrays.asList("viewCount", "likeCount", "collectCount"));

            if (counts.isEmpty()) {
                continue;
            }

            // 解析数据，Redis取出来是String，需要转Integer，注意判空
            Integer viewCount = parseCount(counts.get(0));
            Integer likeCount = parseCount(counts.get(1));
            Integer collectCount = parseCount(counts.get(2));
            
            // 如果所有数据都是 null（可能 Key 过期了），则不更新 DB
            if (viewCount == null && likeCount == null && collectCount == null) {
                continue;
            }

            ArticleInteractDTO dto = ArticleInteractDTO.builder()
                    .id(articleId)
                    .viewCount(viewCount)
                    .likeCount(likeCount)
                    .collectCount(collectCount)
                    .build();

            updateList.add(dto);
        }

        // 3. 批量更新数据库
        if (!updateList.isEmpty()) {
            articleMapper.updateInteractBatch(updateList);
            log.info("同步完成，更新数据库条数: {}", updateList.size());
        }
    }

    // 辅助方法：安全转换 Redis 数据
    private Integer parseCount(Object obj) {
        if (obj == null) return null;
        try {
            return Integer.parseInt(obj.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}