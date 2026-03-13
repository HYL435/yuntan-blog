<script setup lang="ts" name="HomeView">
import { ref, onMounted, onUnmounted, reactive } from 'vue';
import { useRouter } from 'vue-router';
import Header from '@/layouts/Header.vue';
import GridBackground from '@/components/background/GridBackground.vue';
import Rain from '@/components/background/Rain.vue';
import BloggerCard from '@/components/cards/BloggerCard.vue';
import SeparateLine from '@/components/separate/SeparateLine.vue';
import ArticleTwoCard from '@/components/cards/ArticleTwoCard.vue';
import LatestCarousel from '@/components/separate/LatestCarousel.vue';
import { getCategories, CategoryFrontVO } from '@/api/category';
import http from '@/api/http';
import AnnouncementBar from '@/components/common/AnnouncementBar.vue';
import StatsSidebar from '@/components/common/StatsSidebar.vue';
import { ElMessage } from 'element-plus'
import { ArrowRight } from '@element-plus/icons-vue'
// 标签类型
interface TagFrontVO {
  id: number;
  tagName: string;
  articleCount?: number;
}

const tags = ref<TagFrontVO[]>([]);

// 公告栏文本
const announcementTitle = ref('网站公告')
const announcementContent = ref('欢迎来到云坛 — 我们已升级界面与评论管理功能，体验更顺畅。')
const announcementLink = ref('')

const titleScale = ref(1);
const titleOffset = ref(0);
const isDarkMode = ref(false);
const router = useRouter();

const categories = ref<CategoryFrontVO[]>([]);
const categoryArticles = ref<Record<string, any[]>>({});
// 分页设置：主页面每个分类每页显示数量
const PAGE_SIZE = 3
const categoryPage = reactive<Record<string, number>>({})
const categoryTotal = reactive<Record<string, number>>({})
const categoryHasMore = reactive<Record<string, boolean>>({})

const fetchCategoryPage = async (catId: number | string, pageNo = 1) => {
  const catKey = String(catId)
  const numericCatId = Number(catId)
  if (Number.isNaN(numericCatId)) {
    return []
  }
  try {
    const articlesRes = await http.get('/front/articles/page/categoryOrTags', {
      params: {
        pageNo,
        pageSize: PAGE_SIZE,
        categoryId: numericCatId,
      },
    })
    const body = articlesRes.data || articlesRes
    const data = body.data || body
    const records = data.records || data.list || data.rows || (Array.isArray(data) ? data : [])
    const totalCount = data.total || data.totalCount || (Array.isArray(data) ? data.length : records.length)
    categoryTotal[catKey] = Number(totalCount) || 0
    categoryHasMore[catKey] = pageNo * PAGE_SIZE < (categoryTotal[catKey] || records.length)
    return records
  } catch (err) {
    console.error('获取分类文章失败', err)
    ElMessage.error('加载文章失败')
    return []
  }
}

const loadNext = async (catId: number | string) => {
  const catKey = String(catId)
  const nextPage = (categoryPage[catKey] || 1) + 1
  const records = await fetchCategoryPage(catId, nextPage)
  if (!records || records.length === 0) {
    ElMessage.info('没有更多文章了')
    return
  }
  categoryPage[catKey] = nextPage
  categoryArticles.value[catKey] = records
}

onMounted(async () => {
  // 分类及文章（每页显示 PAGE_SIZE 条）
  const res = await getCategories();
  categories.value = res;
  await Promise.all(
    res.map(async (cat) => {
      const catKey = String(cat.id)
      categoryPage[catKey] = 1
      const records = await fetchCategoryPage(cat.id, 1)
      categoryArticles.value[catKey] = records || []
    })
  )

  // 标签及每个标签的文章总数
  try {
    const tagRes = await http.get('/front/tags');
    if (Array.isArray(tagRes.data?.data)) {
      const tagList = tagRes.data.data;
      // 并发请求每个标签的文章总数
      const tagWithCount = await Promise.all(tagList.map(async (tag: TagFrontVO) => {
        try {
          const res = await http.get('/front/articles/page/categoryOrTags', {
            params: {
              pageNo: 1,
              pageSize: 1,
              tagId: tag.id,
            },
          });
          let total = 0;
          if (res.data?.data) {
            if (typeof res.data.data.total === 'number') {
              total = res.data.data.total;
            } else if (Array.isArray(res.data.data)) {
              total = res.data.data.length;
            }
          }
          return { ...tag, articleCount: total };
        } catch {
          return { ...tag, articleCount: 0 };
        }
      }));
      tags.value = tagWithCount;
    } else {
      tags.value = [];
    }
  } catch (e) {
    tags.value = [];
  }
});

const handleScroll = () => {
  const scrollTop = window.scrollY || document.documentElement.scrollTop;
  const startScroll = 0;
  
  if (scrollTop > startScroll) {
    titleScale.value = Math.max(0.6, 1 - scrollTop / 800);
    titleOffset.value = scrollTop;
  } else {
    titleScale.value = 1;
    titleOffset.value = 0;
  }
};

const goToArticle = (id?: string | number) => {
  if (!id) return;
  router.push(`/article/${id}`);
};

const goToTag = (tag?: string) => {
  if (!tag) return;
  router.push(`/tag/${encodeURIComponent(tag)}`);
};

onMounted(() => {
  handleScroll();
  window.addEventListener('scroll', handleScroll);
  const checkDark = () => {
    const html = document.documentElement.classList;
    const body = document.body.classList;
    return (
      html.contains('dark') ||
      html.contains('dark-mode') ||
      body.contains('dark') ||
      body.contains('dark-mode')
    );
  };
  isDarkMode.value = checkDark();
  const observer = new MutationObserver(() => isDarkMode.value = checkDark());
  observer.observe(document.documentElement, { attributes: true, attributeFilter: ['class'] });
  observer.observe(document.body, { attributes: true, attributeFilter: ['class'] });
});

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
});
</script>

<template>
  <!-- 外层容器：控制整体背景色 -->
  <div class="home-view min-h-screen transition-colors duration-300 bg-[#F7F9FE] dark:bg-[#121212]">
    
    <!-- 0. 顶部导航 -->
    <div class="fixed top-0 w-full z-50">
      <Header />
    </div>

    <!-- 1. 背景与标题区域 -->
    <GridBackground>
      <main 
        class="container mx-auto px-4 text-center min-h-screen flex flex-col justify-center items-center" 
        :style="{ 
          position: 'fixed', 
          top: 0, 
          left: 0, 
          right: 0, 
          width: '100%', 
          transform: `scale(${titleScale})`, 
          opacity: Math.max(0, 1 - (titleOffset / 500)), 
          transformOrigin: 'center 40%', 
          transition: 'transform 0.1s ease-out', 
          pointerEvents: 'none', 
          zIndex: 0 
        }"
      >
        <!-- 【修复】标题文字颜色优化 -->
        <h1 class="text-6xl md:text-7xl font-bold mb-6 tracking-tight">
          <span class="bg-clip-text text-transparent bg-gradient-to-r 
                       from-gray-800 to-gray-600 
                       dark:from-white dark:to-slate-200">
            欢迎来到云坛
          </span>
        </h1>
        <!-- 【修复】副标题颜色提高对比度 -->
        <p class="text-xl md:text-2xl text-gray-600 dark:text-slate-300 font-medium">
          探索技术的无限可能
        </p>
      </main>
    </GridBackground>

    <!-- 2. 雨滴层 -->
    <Rain class="fixed inset-0 z-0 pointer-events-none opacity-60" />

    <!-- 3. 主要内容区域 -->
    <div class="relative z-10 w-full pb-16">
      
      <!-- 占位符：取消间隔，让内容紧贴雨滴层 -->
      <div class="w-full h-0"></div>

      <!-- 
         【修复】卡片样式优化
         1. 深色模式使用不透明深灰，避免文字模糊
         2. 增加毛玻璃效果，保持一致性
         3. 优化边框和阴影
      -->
      <div 
        class="home-panel max-w-[1400px] mx-auto p-6 md:p-10 lg:p-14 
               rounded-t-[2.5rem] md:rounded-[2.5rem] 
               shadow-2xl transition-all duration-300
               border-t border-x border-b-0 
               border-[#F7F9FE] dark:border-[#121212]
               bg-[#F7F9FE] dark:bg-[#1E1E1E]
               backdrop-blur-md"
      >
        <div class="max-w-7xl mx-auto">
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 lg:gap-10">
            
            <!-- 左边：文章列表 -->
            <div class="md:col-span-1 lg:col-span-3 space-y-12">
              
              <!-- 第一部分：最新发布 -->
              <section>
                <AnnouncementBar :title="announcementTitle" :content="announcementContent" :link="announcementLink" :closable="false" class="mb-10" />
                <LatestCarousel title="推荐文章" />
              </section>

              <!-- 第二部分：深度精选 -->
              <section>
                <template v-if="categories.length">
                  <div v-for="cat in categories" :key="cat.id">
                    <SeparateLine :title="cat" :data-category-id="cat.id" class="mb-8 elegant-separator" />
                    <div class="grid grid-cols-1 gap-8">
                      <div v-if="!categoryArticles[cat.id] || !categoryArticles[cat.id].length" class="text-center text-gray-400 py-8">
                        暂无文章
                      </div>
                      <div v-else>
                        <div v-for="article in categoryArticles[cat.id]" :key="article.id" class="mb-8 last:mb-0">
                          <ArticleTwoCard
                            :articleId="article.id"
                            :title="article.title"
                            :coverUrl="article.coverImg"
                            :publishTime="article.publishTime"
                            :heat="Number(article.viewCount)"
                            :comments="Number(article.commentCount)"
                            :likes="Number(article.likeCount)"
                            :collectCount="Number(article.collectCount)"
                            :excerpt="article.summary"
                            :primaryTag="article.category || cat.categoryName"
                            :secondaryTag="article.tags && article.tags.length ? article.tags[0] : ''"
                            :imagePosition="'right'"
                            @click="goToArticle"
                          />
                        </div>
                      </div>
                      <div class="flex justify-end mt-2">
                        <el-button
                          size="medium"
                          :disabled="!categoryHasMore[cat.id]"
                          @click="loadNext(cat.id)"
                          class="rounded-full px-6 py-2 flex items-center justify-center gap-3 text-base font-medium transition-shadow duration-200"
                          :style="{
                            backgroundColor: isDarkMode ? '#1E1E1E' : '#F7F9FE',
                            border: '1px solid ' + (isDarkMode ? '#2b2b2b' : '#F7F9FE'),
                            minWidth: '180px',
                            borderRadius: '9999px'
                          }"
                          :class="categoryHasMore[cat.id] ? 'shadow-md hover:shadow-lg' : 'opacity-60 cursor-not-allowed'"
                        >
                          <span>下一页</span>
                          <el-icon><ArrowRight /></el-icon>
                        </el-button>
                      </div>
                    </div>
                  </div>
                </template>
              </section>
              
            </div>
            
            <!-- 右边：侧边栏 -->
            <div class="md:col-span-1 lg:col-span-1">
              <div class="sticky top-24 space-y-6">
                <BloggerCard />
                
                <!-- 【修复】侧边栏标签云文字颜色 -->
                <div class="p-5 rounded-2xl bg-gray-50 dark:bg-[#252525] 
                           border border-gray-100 dark:border-slate-700/50">
                  <h3 class="font-bold mb-3 flex items-center gap-2
                             text-gray-800 dark:text-slate-200">
                    <span>🏷️</span> 热门话题
                  </h3>
                  <div class="flex flex-wrap gap-2">
                    <template v-if="tags.length">
                      <span
                        v-for="tag in tags"
                        :key="tag.id"
                        class="px-3 py-1 bg-white dark:bg-[#333] 
                               border border-gray-200 dark:border-slate-600 
                               rounded-full text-xs 
                               text-gray-700 dark:text-slate-300
                               hover:bg-indigo-500 hover:text-white 
                               dark:hover:bg-indigo-600 transition-colors cursor-pointer"
                        @click="goToTag(tag.tagName)"
                      >
                        #{{ tag.tagName }}
                      </span>
                    </template>
                    <template v-else>
                      <span class="text-gray-400 text-xs">暂无标签</span>
                    </template>
                  </div>
                </div>
                <StatsSidebar />
              </div>
            </div>

          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<style scoped>
/* 保障深色模式下主面板背景与边框生效 */
:global(html.dark) .home-panel,
:global(.dark) .home-panel,
:global(html.dark-mode) .home-panel,
:global(.dark-mode) .home-panel {
  background-color: #1E1E1E !important;
  border-color: #121212 !important;
}

/* 
  【修复】分割线配色优化
  1. 亮色模式：使用深靛青色，背景白色
  2. 深色模式：使用浅靛青色，背景深灰，增加对比度
*/

/* 亮色模式分割线 */
.elegant-separator :deep(.separator-line .line:first-child) {
  background: linear-gradient(to right, transparent, #4f46e5);
}
.elegant-separator :deep(.separator-line .line:last-child) {
  background: linear-gradient(to right, #4f46e5, transparent);
}
.elegant-separator :deep(.separator-title) {
  color: #3730a3; /* Indigo-800 */
  font-weight: 700;
  letter-spacing: 0.05em;
}

/* 深色模式分割线 - 提高对比度 */
:global(.dark) .elegant-separator :deep(.separator-line .line:first-child) {
  background: linear-gradient(to right, transparent, #a5b4fc);
}
:global(.dark) .elegant-separator :deep(.separator-line .line:last-child) {
  background: linear-gradient(to right, #a5b4fc, transparent);
}
:global(.dark) .elegant-separator :deep(.separator-title) {
  color: #c7d2fe; /* Indigo-100 */
  font-weight: 700;
}

/* 分割线装饰点 */
.elegant-separator :deep(.separator-title::before),
.elegant-separator :deep(.separator-title::after) {
  background: #7c3aed; /* Violet-600 */
  border-radius: 50%;
  width: 6px;
  height: 6px;
}
:global(.dark) .elegant-separator :deep(.separator-title::before),
:global(.dark) .elegant-separator :deep(.separator-title::after) {
  background: #a78bfa; /* Violet-400 */
}

/* 标题渐变动画 */
@keyframes gradient-move {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}
.animate-gradient {
  background-size: 200% auto;
  animation: gradient-move 4s linear infinite;
}
</style>