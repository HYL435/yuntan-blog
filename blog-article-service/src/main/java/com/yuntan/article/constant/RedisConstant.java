package com.yuntan.article.constant;

public class RedisConstant {

    // 常量定义
    public static final String CACHE_KEY_PREFIX = "article:info:";
    public static final long HOT_ARTICLE_TTL = 24 * 60 * 60; // 24小时
    public static final long NORMAL_ARTICLE_TTL = 60 * 60;   // 1小时

    public static final int PAGE_SIZE = 10;
    public static final int MAX_CACHE_SIZE = 100; // 缓存只存前100条
    public static final int MAX_CACHE_PAGE = MAX_CACHE_SIZE / PAGE_SIZE; // 前10页走缓存

    // 定义 Key 模板
    public static final String GLOBAL_HOT_KEY = "article:recommend:hot";
    public static final String CAT_HOT_KEY_PREFIX = "article:category:%d:hot"; // %d 占位符

    // 查询类型
    public static final String QUERY_TYPE_HOT = "RECOMMEND";
    public static final String QUERY_TYPE_CATEGORY = "CATEGORY";

    public static final String ARTICLE_HASH_PREFIX = "article:info:"; // 文章缓存 Hash Key 前缀
    public static final String DIRTY_SET_KEY = "article:cnt:dirty"; // 脏数据集合 Key
    // 新增：记录某篇文章被哪些用户点赞了
    public static final String LIKED_USERS_KEY_PREFIX = "article:liked_users:";

}
