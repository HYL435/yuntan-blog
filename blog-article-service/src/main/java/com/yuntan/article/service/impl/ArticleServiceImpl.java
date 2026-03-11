package com.yuntan.article.service.impl;

import com.alibaba.nacos.common.utils.StringUtils;
import com.alibaba.nacos.shaded.io.grpc.netty.shaded.io.netty.util.internal.ThreadLocalRandom;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuntan.api.dto.ArticleInfoDTO;
import com.yuntan.article.constant.QueryType;
import com.yuntan.article.constant.RedisConstant;
import com.yuntan.article.domain.doc.ArticleContentDoc;
import com.yuntan.article.domain.dto.admin.ArticleSaveDTO;
import com.yuntan.article.domain.dto.admin.ArticleStatusDTO;
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
import com.yuntan.article.utils.ArticleOssUtil;
import com.yuntan.common.constant.DefaultImageURLConstant;
import com.yuntan.common.constant.FilePathConstant;
import com.yuntan.common.constant.MessageConstant;
import com.yuntan.common.context.BaseContext;
import com.yuntan.common.domain.PageDTO;
import com.yuntan.common.domain.PageQuery;
import com.yuntan.common.exception.BusinessException;
import com.yuntan.common.utils.BeanUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
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

    public final ArticleOssUtil articleOssUtil;

    public final StringRedisTemplate redisTemplate;
    private final MongoTemplate mongoTemplate;



    /**
     * 根据文章ID获取文章信息
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ArticleFrontVO getArticleById(Long id) {
        String key = RedisConstant.CACHE_KEY_PREFIX + id;
        ArticleFrontVO articleFrontVO;

        // 1. 尝试从缓存获取
        Map<Object, Object> cacheMap = redisTemplate.opsForHash().entries(key);

        if (!cacheMap.isEmpty()) {
            // 缓存命中
            articleFrontVO = BeanUtils.mapToBean(cacheMap, ArticleFrontVO.class);
        } else {
            // 缓存未命中，查库
            Article article = this.getById(id);
            if (article == null) {
                throw new RuntimeException(MessageConstant.ARTICLE_NOT_FOUND);
            }
            articleFrontVO = BeanUtils.copyBean(article, ArticleFrontVO.class);

            // 填充分类和标签（这些是公共数据，可以放缓存）
            setCategoryAndTags(articleFrontVO);

            // 写入缓存（注意：这里不应该写入 isLike/isCollect 这种个性化字段，或者写入默认 false）
            // 建议在 dtoToMap 之前显式设置 false，避免污染公共缓存
            articleFrontVO.setIsLike(false);
            articleFrontVO.setIsCollect(false);

            Map<String, Object> dataMap = BeanUtils.dtoToMap(articleFrontVO);
            Map<String, String> stringMap = dataMap.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> String.valueOf(entry.getValue())));

            redisTemplate.opsForHash().putAll(key, stringMap);
            Long ttl = calculateTTL(articleFrontVO);
            redisTemplate.expire(key, ttl, TimeUnit.SECONDS);
        }

        // 2. 填充文章内容（从 Mongo 获取）
        ArticleContentDoc contentDoc = mongoTemplate.findById(articleFrontVO.getMongoId(), ArticleContentDoc.class);
        if (contentDoc != null) {
            articleFrontVO.setContent(contentDoc.getContent());
        }

        // 3. 【核心修复】无论缓存是否命中，都要处理个性化状态（点赞/收藏）
        Long userId = BaseContext.getUserId(); // 假设 BaseContext 能安全处理未登录返回 null
        if (userId != null) {
            // 查询当前用户的交互状态
            articleFrontVO.setIsLike(interactMapper.isLike(id, userId) > 0);
            articleFrontVO.setIsCollect(interactMapper.isCollect(id, userId) > 0); // ✅ 修正变量名
        } else {
            // 未登录默认 false
            articleFrontVO.setIsLike(false);
            articleFrontVO.setIsCollect(false);
        }

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
            return getFromRedisCache(cacheKey, pageQuery, null, null);
        } else {
            // --- 走 MySQL 深分页模式 ---
            // 超过第10页，用户流量极低，直接查库，避免浪费 Redis 内存
            return getFromMySQLDirectly(pageQuery, null, null);
        }
    }

    // 获取从 Redis 中获取数据
    private PageDTO<ArticleExhibitFrontVO> getFromRedisCache(String cacheKey, PageQuery pageQuery, Long categoryId, Long tagId) {
        // 1. 计算 Redis List 的下标范围 (LRANGE start end)
        // page=1 -> 0~9, page=2 -> 10~19
        long start = (long) (pageQuery.getPageNo() - 1) * pageQuery.getPageSize();
        long end = start + pageQuery.getPageSize() - 1;

        // 2. 从 Redis 取 ID 列表
        List<String> idStrList = redisTemplate.opsForList().range(cacheKey, start, end);

        // 3. 缓存击穿兜底：如果 Redis 挂了，或者定时任务还没跑，List 是空的
        if (idStrList == null || idStrList.isEmpty()) {
            // 降级为直接查库
            return getFromMySQLDirectly(pageQuery, categoryId, tagId);
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
    private PageDTO<ArticleExhibitFrontVO> getFromMySQLDirectly(PageQuery pageQuery, Long categoryId, Long tagId) {
        // --- 走 MySQL 深分页模式 ---
        // 超过第10页，用户流量极低，直接查库，避免浪费 Redis 内存
        // 构建分页参数，获取page对象
        Page<Article> page = pageQuery.toMpPage();

        // 直接调用你原来深分页用的那个 mapper 方法（已经完美处理了分类/标签/状态/排序）
        Page<ArticleExhibitFrontVO> resultPage = articleMapper.selectArticleWithCategory(
                page,
                categoryId,
                tagId
        );

        // 转 PageDTO + 设置分类标签
        PageDTO<ArticleExhibitFrontVO> articleExhibitFrontVOPageDTO = PageDTO.of(resultPage, ArticleExhibitFrontVO.class);

        // 获取并设置分类和标签
        articleExhibitFrontVOPageDTO.getList().forEach(this::setCategoryAndTags);

        return articleExhibitFrontVOPageDTO;
    }

    // 确定缓存Key
    private String determineCacheKey(String type, Long id) {
        if (type == null) {
            throw new IllegalArgumentException("列表类型不能为空");
        }

        switch (type) {
            case QueryType.RECOMMEND:
                return RedisConstant.GLOBAL_HOT_KEY;

            case QueryType.CATEGORY:
                validateId(id, "分类查询必须提供 categoryId");
                return String.format(RedisConstant.CAT_HOT_KEY_PREFIX, id);

            case QueryType.TAG:
                validateId(id, "标签查询必须提供 tagId");
                return String.format(RedisConstant.QUERY_TYPE_TAG, id);

            default:
                throw new IllegalArgumentException("不支持的列表类型: " + type);
        }
    }

    private void validateId(Long id, String message) {
        if (id == null) {
            throw new IllegalArgumentException(message);
        }
    }


    /**
     * 分页查询文章列表通过分类或标签
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PageDTO<ArticleExhibitFrontVO> pageExhibitByCategoryOrTags(ArticleExhibitQuery query) {
        // 1. 参数校验与 Key 生成
        Long categoryId = query.getCategoryId();
        Long tagId = query.getTagId();
        int pageNo = query.getPageNo();
        int maxCachePage = RedisConstant.MAX_CACHE_PAGE;

        // 分类查询
        if (categoryId != null) {
            String cacheKey = determineCacheKey(RedisConstant.QUERY_TYPE_CATEGORY, categoryId);
            if (pageNo <= maxCachePage) {
                // Redis缓存
                return getFromRedisCache(cacheKey, query, categoryId, null);
            } else {
                // MySQL深分页
                Page<Article> page = query.toMpPage();
                Page<ArticleExhibitFrontVO> resultPage = articleMapper.selectArticleWithCategory(
                        page,
                        categoryId,
                        null // 标签ID为null
                );
                PageDTO<ArticleExhibitFrontVO> articleExhibitFrontVOPageDTO = PageDTO.of(resultPage, ArticleExhibitFrontVO.class);
                articleExhibitFrontVOPageDTO.getList().forEach(this::setCategoryAndTags);
                return articleExhibitFrontVOPageDTO;
            }
        }
        // 标签查询
        else if (tagId != null) {
            String cacheKey = determineCacheKey(RedisConstant.QUERY_TYPE_TAG, tagId);
            if (pageNo <= maxCachePage) {
                // Redis缓存
                return getFromRedisCache(cacheKey, query, null, tagId);
            } else {
                // MySQL深分页
                Page<Article> page = query.toMpPage();
                Page<ArticleExhibitFrontVO> resultPage = articleMapper.selectArticleWithCategory(
                        page,
                        null, // 分类ID为null
                        tagId
                );
                PageDTO<ArticleExhibitFrontVO> articleExhibitFrontVOPageDTO = PageDTO.of(resultPage, ArticleExhibitFrontVO.class);
                articleExhibitFrontVOPageDTO.getList().forEach(this::setCategoryAndTags);
                return articleExhibitFrontVOPageDTO;
            }
        }
        // 分类和标签都为空，返回空页或抛异常
        else {
            return new PageDTO<>();
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
                .eq(query.getStatus() != null, Article::getStatus, query.getStatus()) // 状态条件
                .like(StringUtils.isNotBlank(query.getTitle()), Article::getTitle, query.getTitle()) // 标题模糊查询
                .eq(query.getIsOriginal() != null, Article::getIsOriginal, query.getIsOriginal()) // 是否原创

                // 可选择的排序字段和顺序
                .orderBy(query.getSortBy() != null, query.getIsAsc(), Article::getUpdateTime); // 动态排序

        // 执行查询
        Page<Article> resultPage = pageQuery.page(page);

        // 转换为VO对象
        PageDTO<ArticleAdminVO> articleAdminVOPageDTO = PageDTO.of(resultPage, ArticleAdminVO.class);

        // 获取并设置分类和标签
        articleAdminVOPageDTO.getList().forEach(this::setCategoryAndTags);

        // 额外的过滤：如果前端传了 category 字段，则在内存中过滤一次（因为管理列表的查询条件比较复杂，直接在SQL里加分类条件会很麻烦）
        if (query.getCategory() != null) {
            List<ArticleAdminVO> list = articleAdminVOPageDTO.getList()
                    .stream()
                    .filter(articleAdminVO -> Objects.equals(articleAdminVO.getCategory(), query.getCategory()))
                    .toList();

            articleAdminVOPageDTO.setList(list);
        }

        return articleAdminVOPageDTO;
    }


    /**
     * 文章置顶
     */
    @Override
    public void articleTop(Long id, Integer top) {
        // 构建 Redis 缓存 Key
        String key = RedisConstant.CACHE_KEY_PREFIX + id;

        top = Objects.equals(top, ArticleTopStatusEnum.TOP.getValue()) ? ArticleTopStatusEnum.NOT_TOP.getValue() : ArticleTopStatusEnum.TOP.getValue();

        this.lambdaUpdate()
                .eq(Article::getId, id)
                .set(Article::getIsTop, top)
                .update();

        // 删除缓存
        redisTemplate.delete(key);

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

        // 获取当前登录用户的作者ID
        Long currentUserId = BaseContext.getUserId(); // 根据实际项目中的登录体系来获取

        // 构建文章对象
        Article article = BeanUtils.copyBean(articleSaveDTO, Article.class);

        // 设置作者ID
        article.setAuthorId(currentUserId); // 确保文章的作者ID不为null

        // 判断文章是否存在，存在则更新，否则插入
        if (article.getId() == null) {
            // 判断图片文件是否为空，如果不为空，则上传图片，如果为空，则使用默认图片
            if (articleSaveDTO.getImageFile() != null) {
                try {
                    String image = articleOssUtil.uploadFile(articleSaveDTO.getImageFile(), FilePathConstant.ARTICLE_COVER_PATH);
                    article.setCoverImg(image);
                } catch (IOException e) {
                    throw new BusinessException(MessageConstant.UPLOAD_FAILED);
                }
            } else {
                article.setCoverImg(DefaultImageURLConstant.DEFAULT_BLOG_COVER_URL);
            }

            // 保存文章
            this.save(article);
        } else {
            String key;
            try {
                Article byId = this.getById(article.getId());
                if (byId == null) {
                    throw new BusinessException(MessageConstant.ARTICLE_NOT_FOUND);
                }
                key = RedisConstant.CACHE_KEY_PREFIX + byId.getId();
                if (byId.getCoverImg() != null && !byId.getCoverImg().equals(DefaultImageURLConstant.DEFAULT_BLOG_COVER_URL)) {
                    // 先删除原来的图片
                    articleOssUtil.deleteFile(byId.getCoverImg());
                    String image = articleOssUtil.uploadFile(articleSaveDTO.getImageFile(), FilePathConstant.ARTICLE_COVER_PATH);
                    // 将新的图片上传到OSS中
                    article.setCoverImg(image);
                }
            } catch (IOException e) {
                throw new BusinessException(MessageConstant.UPLOAD_FAILED);
            }
            // 更新文章
            this.updateById(article);
            // 删除缓存
            redisTemplate.delete(key);
        }

        // 获取文章id
        Long articleId = article.getId();
        // 获取标签ids
        List<Long> tagIds = articleSaveDTO.getTagIds();

        // 确保 categoryId 和 tagIds 不为 null
        if (articleSaveDTO.getCategoryId() == null) {
            throw new BusinessException("分类ID不能为空");
        }
        if (tagIds == null || tagIds.isEmpty()) {
            throw new BusinessException("标签ID不能为空");
        }

        // 将文章分类和标签存储到数据库中
        // 删除原本的文章和分类关系
        articleCategoryMapper.deleteByArticleId(articleId);
        // 将新的文章和分类关系存入数据库
        articleCategoryMapper.insert(new ArticleCategory(articleId, articleSaveDTO.getCategoryId()));

        // 删除原本的文章和标签关系
        articleTagMapper.deleteByArticleId(articleId);
        // 将新的文章和标签关系存入数据库
        tagIds.forEach(tagId -> articleTagMapper.insert(new ArticleTag(articleId, tagId)));

        // 将正文内容存入 MongoDB
        // 构建文档对象
        ArticleContentDoc contentDoc = ArticleContentDoc.builder()
                .id(articleSaveDTO.getMongoId()) // 如果有 MongoId，使用它，否则使用自动生成的
                .articleId(articleId)
                .content(articleSaveDTO.getContent())
                .build();

        // 保存文档对象
        ArticleContentDoc savedDoc;
        try {
            savedDoc = mongoTemplate.save(contentDoc);
        } catch (Exception e) {
            // 如果保存失败，则抛出业务异常
            throw new BusinessException(MessageConstant.MONGODB_SAVE_ERROR);
        }

        // 判断文章是不是第一次保存 MongoId
        if (articleSaveDTO.getMongoId() == null) {
            // 如果是第一次保存，则将文章的 MongoId 回填到数据库
            this.lambdaUpdate()
                    .eq(Article::getId, articleId)
                    .set(Article::getMongoId, savedDoc.getId()) // 使用 MongoDB 返回的 ID
                    .update();
        }
    }

    /**
     * 更新文章状态
     */
    @Override
    public void updateArticleStatus(ArticleStatusDTO articleStatusDTO) {
        // 构建 Redis 缓存 Key
        String key = RedisConstant.CACHE_KEY_PREFIX + articleStatusDTO.getId();

        // 更新文章状态
        this.lambdaUpdate()
                .eq(Article::getId, articleStatusDTO.getId())
                .set(Article::getStatus, articleStatusDTO.getStatus())
                .update();

        // 删除缓存
        redisTemplate.delete(key);
    }

    /**
     * 根据文章ID获取文章信息
     */
    @Override
    public ArticleInfoDTO getArticleInfoById(Long id) {
        Article article = this.getById(id);
        if (article == null) {
            throw new RuntimeException(MessageConstant.ARTICLE_NOT_FOUND);
        }
        return BeanUtils.copyBean(article, ArticleInfoDTO.class);
    }

    // 获取并设置文章分类和标签
    private void setCategoryAndTags(CategorizableVO categorizableVO) {
        // 获取文章分类
        String category = categoryMapper.getCategoryByArticleId(categorizableVO.getId());
        // 如果分类为空，设置默认分类
        if (category == null) {
            category = "暂未分类";  // 你可以选择适合的默认分类名称
        }

        // 获取文章标签
        List<String> tags = tagMapper.getTagsByArticleId(categorizableVO.getId());

        categorizableVO.setCategory(category);
        categorizableVO.setTags(tags);
    }

}
