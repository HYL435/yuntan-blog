package com.yuntan.article.task;

import com.yuntan.article.constant.RedisConstant;
import com.yuntan.article.mapper.ArticleCategoryMapper;
import com.yuntan.article.mapper.ArticleMapper;
import com.yuntan.article.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
@Slf4j
public class ArticleRankTask {

    private final ArticleMapper articleMapper; // 你的 MyBatis Mapper
    public final CategoryMapper categoryMapper;
    public final ArticleCategoryMapper articleCategoryMapper;

    private final StringRedisTemplate redisTemplate;

    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点执行
    public void refreshRankings() {
        System.out.println("开始刷新排行榜...");

        // 1. 刷新全站推荐 (Top 10)
        List<Long> globalIds = articleMapper.selectGlobalHotIds(100);
        refreshRedisList(RedisConstant.GLOBAL_HOT_KEY, globalIds);

        // 2. 刷新各分类列表
        // 2.1 先查出系统有哪些分类 ID (例如: 1-科技, 2-生活, 3-情感)
        List<Integer> categoryIds = categoryMapper.selectAllCategoryIds();
        
        // 2.2 遍历刷新
        for (Integer catId : categoryIds) {
            // 查该分类下的 Top 5
            List<Long> catIds = articleCategoryMapper.selectCategoryHotIds(catId, 100);
            
            String key = String.format(RedisConstant.CAT_HOT_KEY_PREFIX, catId);
            refreshRedisList(key, catIds);
        }
        
        log.info("排行榜刷新结束");
    }

    /**
     * 通用的列表刷新方法：原子替换策略 (Temp -> Rename)
     */
    private void refreshRedisList(String finalKey, List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;

        String tempKey = finalKey + ":temp";
        List<String> strIds = ids.stream().map(String::valueOf).collect(Collectors.toList());

        // 使用 SessionCallback 保证 pipeline 或 事务 的完整性 (可选)
        redisTemplate.delete(tempKey);
        redisTemplate.opsForList().rightPushAll(tempKey, strIds);
        redisTemplate.rename(tempKey, finalKey);
    }
}