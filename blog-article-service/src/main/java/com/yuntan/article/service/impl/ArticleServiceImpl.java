package com.yuntan.article.service.impl;

import com.alibaba.nacos.shaded.io.grpc.netty.shaded.io.netty.util.internal.ThreadLocalRandom;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuntan.article.constant.RedisConstant;
import com.yuntan.article.domain.doc.ArticleContentDoc;
import com.yuntan.article.domain.dto.admin.ArticleSaveDTO;
import com.yuntan.article.domain.po.Article;
import com.yuntan.article.domain.po.ArticleCategory;
import com.yuntan.article.domain.po.ArticleTag;
import com.yuntan.article.domain.query.ArticleExhibitQuery;
import com.yuntan.article.domain.query.ArticleManageQuery;
import com.yuntan.article.domain.vo.CategorizableVO;
import com.yuntan.article.domain.vo.admin.ArticleAdminVO;
import com.yuntan.article.domain.vo.admin.ArticleDetailVO;
import com.yuntan.article.domain.vo.front.ArticleExhibitFrontVO;
import com.yuntan.article.domain.vo.front.ArticleFrontVO;
import com.yuntan.article.enums.ArticleStatusEnum;
import com.yuntan.article.enums.ArticleTopStatusEnum;
import com.yuntan.article.mapper.*;
import com.yuntan.article.service.IArticleService;
import com.yuntan.common.constant.MessageConstant;
import com.yuntan.common.context.BaseContext;
import com.yuntan.common.domain.PageDTO;
import com.yuntan.common.domain.PageQuery;
import com.yuntan.common.exception.BusinessException;
import com.yuntan.common.utils.BeanUtils;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements IArticleService {

    public final ArticleMapper articleMapper;
    public final CategoryMapper categoryMapper;
    public final TagMapper tagMapper;
    public final ArticleCategoryMapper articleCategoryMapper;
    public final ArticleTagMapper articleTagMapper;
    public final InteractMapper interactMapper;

    public final StringRedisTemplate redisTemplate;
    private final MongoTemplate mongoTemplate;



    /**
     * 根据文章ID获取文章信息
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ArticleFrontVO getArticleById(Long id) {
        String key = RedisConstant.CACHE_KEY_PREFIX + id;

        // 先尝试从缓存中获取文章信息
        // 如果缓存中存在，则直接返回，如果不存在，则从数据库中获取
        Map<Object, Object> cacheMap = redisTemplate.opsForHash().entries(key);

        if (!cacheMap.isEmpty()) {
            // 缓存命中，查找文章信息，组装并返回
            ArticleFrontVO articleFrontVO = BeanUtils.mapToBean(cacheMap, ArticleFrontVO.class);
            ArticleContentDoc contentDoc = mongoTemplate.findById(articleFrontVO.getMongoId(), ArticleContentDoc.class);
            assert contentDoc != null;  // 断言非空
            articleFrontVO.setContent(contentDoc.getContent());
            return articleFrontVO;
        }

        // 获取文章信息
        Article article = this.getById(id);
        // 转换为前台VO对象
        if (article == null) {
            throw new RuntimeException(MessageConstant.ARTICLE_NOT_FOUND);
        }
        ArticleFrontVO articleFrontVO = BeanUtils.copyBean(article, ArticleFrontVO.class);
        // 填充分类和标签
        setCategoryAndTags(articleFrontVO);
        // 判断用户是否登录
        if (Objects.nonNull(BaseContext.getUserId())) {
            Long userId = BaseContext.getUserId();
            // 判断用户是否点赞
            articleFrontVO.setIsLike(interactMapper.isLike(id, userId) > 0);
            // 判断用户是否收藏
            articleFrontVO.setIsLike(interactMapper.isCollect(id, userId) > 0);
        } else {
            // 否则，设置用户是否点赞和收藏为false
            articleFrontVO.setIsLike(false);
            articleFrontVO.setIsCollect(false);
        }

        // 转换数据类型，并写入缓存
        Map<String, Object> dataMap = BeanUtils.dtoToMap(articleFrontVO);
        Map<String, String> stringMap = dataMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> String.valueOf(entry.getValue())
                ));

        // 回写缓存
        redisTemplate.opsForHash().putAll(key, stringMap);
        // 设置缓存过期时间
        Long ttl = calculateTTL(articleFrontVO);
        redisTemplate.expire(key, ttl, TimeUnit.SECONDS);

        // 获取文章内容
        ArticleContentDoc contentDoc = mongoTemplate.findById(articleFrontVO.getMongoId(), ArticleContentDoc.class);
        assert contentDoc != null;  // 断言非空
        // 填入文章内容
        articleFrontVO.setContent(contentDoc.getContent());

        // 返回
        return articleFrontVO;
    }

    // 计算过期时间：基础时间 + 随机抖动
    private Long calculateTTL(ArticleFrontVO article) {
        long baseTTL;

        // 判断是否为热点文章（这里简单演示，实际可能根据 viewCount > 100 或 置顶字段）
        if (article.getViewCount() > 100 || Objects.equals(article.getIsTop(), ArticleTopStatusEnum.TOP.getValue())) {
            baseTTL = RedisConstant.HOT_ARTICLE_TTL;
        } else {
            baseTTL = RedisConstant.NORMAL_ARTICLE_TTL;
        }

        // 生成随机抖动值：±60秒 (-60 到 +60)
        // ThreadLocalRandom 性能优于 Random
        long jitter = ThreadLocalRandom.current().nextLong(-60, 61);

        return baseTTL + jitter;
    }

    /**
     * 分页查询文章列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PageDTO<ArticleExhibitFrontVO> pageExhibit(PageQuery pageQuery) {

        // 1. 参数校验与 Key 生成
        String cacheKey = determineCacheKey(RedisConstant.QUERY_TYPE_HOT, null);

        // 2. 策略分流：判断是否走缓存 (前10页) 还是 走数据库 (深分页)
        if (pageQuery.getPageNo() <= RedisConstant.MAX_CACHE_PAGE) {
            // --- 走 Redis 缓存模式 ---
            return getFromRedisCache(cacheKey, pageQuery, RedisConstant.QUERY_TYPE_HOT);
        } else {
            // --- 走 MySQL 深分页模式 ---
            // 超过第10页，用户流量极低，直接查库，避免浪费 Redis 内存
            return getFromMySQLDirectly(pageQuery);
        }
    }

    // 获取从 Redis 中获取数据
    private PageDTO<ArticleExhibitFrontVO> getFromRedisCache(String cacheKey, PageQuery pageQuery, String type) {
        // 1. 计算 Redis List 的下标范围 (LRANGE start end)
        // page=1 -> 0~9, page=2 -> 10~19
        long start = (long) (pageQuery.getPageNo() - 1) * pageQuery.getPageSize();
        long end = start + pageQuery.getPageSize() - 1;

        // 2. 从 Redis 取 ID 列表
        List<String> idStrList = redisTemplate.opsForList().range(cacheKey, start, end);

        // 3. 缓存击穿兜底：如果 Redis 挂了，或者定时任务还没跑，List 是空的
        if (idStrList == null || idStrList.isEmpty()) {
            // 降级为直接查库
            return getFromMySQLDirectly(pageQuery);
        }

        // 4. 将 ID 字符串转为 Long
        List<Long> ids = idStrList.stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());

        // 5. 【关键步】拿着 ID 列表去查文章详情 (聚合操作)
        return batchGetArticleDetails(ids);
    }

    // 根据ID列表批量获取文章详情
    private PageDTO<ArticleExhibitFrontVO> batchGetArticleDetails(List<Long> ids) {
        // 这里复用了 articleService.getArticleInfo(id)
        // 该方法内部已经实现了：先查 Redis Hash -> 没命中查 DB -> 回写 Redis 的闭环逻辑
        // 使用并行流 (parallelStream) 可以提高并发获取效率
        List<ArticleFrontVO> list = ids.stream()
                // .parallel() // 如果并发量极大，可以开启并行流
                .map(this::getArticleById)
                .filter(Objects::nonNull) // 过滤掉可能已被删除的文章
                .collect(Collectors.toList());
        // 批量转换
        List<ArticleExhibitFrontVO> result = BeanUtils.copyList(list, ArticleExhibitFrontVO.class);
        // 封装 PageDTO
        PageDTO<ArticleExhibitFrontVO> pageDTO = new PageDTO<>();
        pageDTO.setList(result);

        return pageDTO;
    }

    // 获取从 MySQL 中获取数据
    private PageDTO<ArticleExhibitFrontVO> getFromMySQLDirectly(PageQuery pageQuery) {
        // --- 走 MySQL 深分页模式 ---
        // 超过第10页，用户流量极低，直接查库，避免浪费 Redis 内存
        // 构建分页参数，获取page对象
        Page<Article> page = pageQuery.toMpPage();

        // 设置查询条件
        LambdaQueryChainWrapper<Article> query = new LambdaQueryChainWrapper<>(articleMapper)
                .eq(Article::getStatus, ArticleStatusEnum.PUBLISHED.getValue())
                .orderByDesc(Article::getIsTop)
                .orderByDesc(Article::getUpdateTime);
        // 查询
        Page<Article> result = query.page(page);
        // 转换为VO对象
        PageDTO<ArticleExhibitFrontVO> articleExhibitFrontVOPageDTO = PageDTO.of(result, ArticleExhibitFrontVO.class);

        // 获取并设置分类和标签
        articleExhibitFrontVOPageDTO.getList().forEach(this::setCategoryAndTags);

        return articleExhibitFrontVOPageDTO;
    }

    // 确定缓存Key
    private String determineCacheKey(String type, Long categoryId) {
        if ("RECOMMEND".equals(type)) {
            return RedisConstant.GLOBAL_HOT_KEY;
        } else if ("CATEGORY".equals(type)) {
            if (categoryId == null) {
                throw new IllegalArgumentException("分类查询必须提供 categoryId");
            }
            return String.format(RedisConstant.CAT_HOT_KEY_PREFIX, categoryId);
        }
        throw new IllegalArgumentException("不支持的列表类型: " + type);
    }

    /**
     * 分页查询文章列表通过分类或标签
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PageDTO<ArticleExhibitFrontVO> pageExhibitByCategoryOrTags(ArticleExhibitQuery query) {

        // 1. 参数校验与 Key 生成
        String cacheKey = determineCacheKey(RedisConstant.QUERY_TYPE_CATEGORY, query.getCategoryId());

        // 2. 策略分流：判断是否走缓存 (前10页) 还是 走数据库 (深分页)
        if (query.getPageNo() <= RedisConstant.MAX_CACHE_PAGE && query.getCategoryId() != null) {
            // --- 走 Redis 缓存模式 ---
            return getFromRedisCache(cacheKey, query, RedisConstant.QUERY_TYPE_CATEGORY);
        } else {
            // --- 走 MySQL 深分页模式 ---
            // 超过第10页，用户流量极低，直接查库，避免浪费 Redis 内存
            // 构建分页参数，获取page对象
            Page<Article> page = query.toMpPage();

            // 调用Mapper联表分页查询（分页插件自动拼接LIMIT）
            Page<ArticleExhibitFrontVO> resultPage = articleMapper.selectArticleWithCategory(
                    page,
                    query.getCategoryId(),
                    query.getTagId()
            );

            // 转换为VO对象
            PageDTO<ArticleExhibitFrontVO> articleExhibitFrontVOPageDTO = PageDTO.of(resultPage, ArticleExhibitFrontVO.class);

            // 获取并设置分类和标签
            articleExhibitFrontVOPageDTO.getList().forEach(this::setCategoryAndTags);

            return articleExhibitFrontVOPageDTO;
        }
    }

    /**
     * 分页查询文章管理列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PageDTO<ArticleAdminVO> pageManage(ArticleManageQuery query) {

        // 构建分页参数，获取page对象
        Page<Article> page = query.toMpPage();


        // 设置查询条件
        LambdaQueryChainWrapper<Article> pageQuery = new LambdaQueryChainWrapper<>(articleMapper)
                .orderByDesc(Article::getIsTop)
                .orderByDesc(Article::getUpdateTime);

        // 查询
        Page<Article> resultPage = pageQuery.page(page);

        // 转换为VO对象
        PageDTO<ArticleAdminVO> articleAdminVOPageDTO = PageDTO.of(resultPage, ArticleAdminVO.class);

        // 获取并设置分类和标签
        articleAdminVOPageDTO.getList().forEach(this::setCategoryAndTags);

        return articleAdminVOPageDTO;
    }

    /**
     * 文章置顶
     */
    @Override
    public void articleTop(Long id, Integer top) {

        top = Objects.equals(top, ArticleTopStatusEnum.TOP.getValue()) ? ArticleTopStatusEnum.NOT_TOP.getValue() : ArticleTopStatusEnum.TOP.getValue();

        this.lambdaUpdate()
                .eq(Article::getId, id)
                .set(Article::getIsTop, top)
                .update();

    }

    /**
     * 获取文章详情
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArticleDetailVO getArticleDetail(Long id) {

        boolean flag = true;

        // 获取文章详情
        Article article = this.getById(id);
        if (article == null) {
            throw new RuntimeException(MessageConstant.ARTICLE_NOT_FOUND);
        }
        // 如果文章没有发布时间，则设置发布时间为当前时间，解决hutool的bug
        if (article.getPublishTime() == null) {
            flag = false;
            article.setPublishTime(LocalDateTime.now());
        }

        // 转换为VO对象
        ArticleDetailVO articleDetailVO = BeanUtils.copyBean(article, ArticleDetailVO.class);
        // 重新设置发布时间为 null
        if (!flag) {
            article.setPublishTime(null);
        }

        // 获取并设置分类和标签
        setCategoryAndTags(articleDetailVO);

        // 从MongoDB中获取文章内容
        Query query = Query.query(Criteria.where("articleId").is(id));
        ArticleContentDoc doc = mongoTemplate.findOne(query, ArticleContentDoc.class);

        // 设置文章内容
        assert doc != null; // 断言doc不为空
        articleDetailVO.setContent(doc.getContent());

        return articleDetailVO;
    }

    /**
     * 保存文章
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveArticle(ArticleSaveDTO articleSaveDTO) {

        // 构建文章对象
        Article article = BeanUtils.copyBean(articleSaveDTO, Article.class);

        // 将文章内容存储到数据库中
        // 判断文章是否存在，存在则更新，否则插入
        if (article.getId() == null) {
            this.save(article);
        } else {
            this.updateById(article);
        }
        // 获取文章id
        Long articleId = article.getId();
        // 获取标签ids
        List<Long> tagIds = articleSaveDTO.getTagIds();

        // 将文章分类和标签存储到数据库中
        // 删除原本的文章和分类关系
        articleCategoryMapper.deleteByArticleId(articleId);
        // 将新的文章和分类关系存入数据库中
        articleCategoryMapper.insert(new ArticleCategory(articleId, articleSaveDTO.getCategoryId()));

        // 删除原本的文章和标签关系
        articleTagMapper.deleteByArticleId(articleId);
        // 将新的文章和标签关系存入数据库中
        tagIds.forEach(tagId -> articleTagMapper.insert(new ArticleTag(articleId, tagId)));

        // 将正文内容存入 MongoDB
        // 构建文档对象
        ArticleContentDoc contentDoc = ArticleContentDoc.builder()
                .id(articleSaveDTO.getMongoId())
                .articleId(articleId)
                .content(articleSaveDTO.getContent())
                .build();
        // 保存文档对象
        try {
            mongoTemplate.save(contentDoc);
        } catch (Exception e) {
            // 如果保存失败，则抛出业务异常
            throw new BusinessException(MessageConstant.MONGODB_SAVE_ERROR);
        }

        // 判断文章是不是第一次保存
        if (articleSaveDTO.getMongoId() == null) {
            // 如果是第一次保存，则将文章id存入 数据库
            this.lambdaUpdate()
                    .eq(Article::getId, articleId)
                    .set(Article::getMongoId, articleSaveDTO.getMongoId())
                    .update();
        }


    }

    /**
     * 发布文章
     */
    @Override
    public void publishArticle(Long id) {

        this.lambdaUpdate()
                .eq(Article::getId, id)
                .set(Article::getStatus, ArticleStatusEnum.PUBLISHED.getValue())
                .set(Article::getPublishTime, LocalDateTime.now())
                .update();

    }

    /**
     * 私密文章
     */
    @Override
    public void privateArticle(Long id) {

        this.lambdaUpdate()
                .eq(Article::getId, id)
                .set(Article::getStatus, ArticleStatusEnum.PRIVATE.getValue())
                .update();

    }

    // 获取并设置文章分类和标签
    private void setCategoryAndTags(CategorizableVO categorizableVO) {

        // 获取文章分类
        String category = categoryMapper.getCategoryByArticleId(categorizableVO.getId());

        // 获取文章标签
        List<String> tags = tagMapper.getTagsByArticleId(categorizableVO.getId());

        categorizableVO.setCategory(category);
        categorizableVO.setTags(tags);

    }
}
