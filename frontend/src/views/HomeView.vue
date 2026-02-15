<script setup lang="ts" name="HomeView">
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import Header from '@/components/layout/Header.vue';
import GridBackground from '@/components/background/GridBackground.vue';
import Rain from '@/components/background/Rain.vue';
import ArticleCard from '@/components/cards/ArticleCard.vue';
import BloggerCard from '@/components/cards/BloggerCard.vue';
import SeparateLine from '@/components/separate/SeparateLine.vue';
import ArticleTwoCard from '@/components/cards/ArticleTwoCard.vue';

const titleScale = ref(1);
const titleOffset = ref(0);
const isDarkMode = ref(false);
const router = useRouter();

const demoArticle = {
  id: 'demo-1',
  title: '探索Vue 3组合式API的艺术',
  excerpt: 'Vue 3 的 Composition API 为代码组织带来了全新的范式...',
  coverImg: 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?q=80&w=1200&auto=format&fit=crop',
  category: '前端技术',
  tags: ['Vue3', '架构'],
  isOriginal: 1,
  isTop: 0,
  likeCount: 128,
  commentCount: 24,
  viewCount: 1024,
  publishTime: '2026-02-04T10:00:00'
};

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
                <SeparateLine title="最新发布" class="mb-8 elegant-separator" />
                <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 justify-items-center">
                  <ArticleCard
                    v-for="i in 6"
                    :key="i"
                    :article="demoArticle"
                    class="w-full"
                    @click="goToArticle"
                    @tag-click="goToTag"
                  />
                </div>
              </section>

              <!-- 第二部分：深度精选 -->
              <section>
                <SeparateLine title="深度精选" class="mb-8 elegant-separator" />
                <div class="grid grid-cols-1 gap-8">
                  <ArticleTwoCard
                    v-for="index in 3"
                    :key="`two-${index}`"
                    :articleId="demoArticle.id"
                    :title="demoArticle.title"
                    :coverUrl="demoArticle.coverImg"
                    :publishTime="demoArticle.publishTime"
                    :heat="demoArticle.viewCount"
                    :comments="demoArticle.commentCount"
                    :likes="demoArticle.likeCount"
                    :excerpt="demoArticle.excerpt"
                    :primaryTag="demoArticle.category"
                    :secondaryTag="demoArticle.tags[0]"
                    :imagePosition="index % 2 === 0 ? 'left' : 'right'"
                    @click="goToArticle"
                  />
                </div>
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
                    <span class="px-3 py-1 bg-white dark:bg-[#333] 
                               border border-gray-200 dark:border-slate-600 
                               rounded-full text-xs 
                               text-gray-700 dark:text-slate-300
                               hover:bg-indigo-500 hover:text-white 
                               dark:hover:bg-indigo-600 transition-colors cursor-pointer">
                      #Vue3
                    </span>
                    <span class="px-3 py-1 bg-white dark:bg-[#333] 
                               border border-gray-200 dark:border-slate-600 
                               rounded-full text-xs 
                               text-gray-700 dark:text-slate-300
                               hover:bg-indigo-500 hover:text-white 
                               dark:hover:bg-indigo-600 transition-colors cursor-pointer">
                      #设计
                    </span>
                  </div>
                </div>
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