<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import Header from '@/components/layout/Header.vue';
import GridBackground from '@/components/background/GridBackground.vue';
import Rain from '@/components/background/Rain.vue';
import ArticleCart from '@/components/carts/ArticleCart.vue';
import SeparateLine from '@/components/separate/SeparateLine.vue';

const titleScale = ref(1);
const titleOffset = ref(0);
const isDarkMode = ref(false);

const demoArticle = {
  id: 'demo-1',
  title: '示例文章标题',
  coverImg: '',
  category: '未分类',
  tags: ['示例', '前端'],
  isOriginal: 1,
  isTop: 0,
  likeCount: 128,
  commentCount: 24,
  viewCount: 1024,
  publishTime: '2026-02-04T10:00:00',
};

const handleScroll = () => {
  const scrollTop = window.scrollY || document.documentElement.scrollTop;
  // 从第 100px 开始计算缩放，每滚动 300px 缩小到 0.6
  const startScroll = 100;
  if (scrollTop > startScroll) {
    const scrollDistance = scrollTop - startScroll;
    titleScale.value = Math.max(0.6, 1 - scrollDistance / 1000);
    // 固定在顶部，不跟着滚动
    titleOffset.value = scrollTop;
  } else {
    titleScale.value = 1;
    titleOffset.value = 0;
  }
};

onMounted(() => {
  handleScroll();
  window.addEventListener('scroll', handleScroll);
  isDarkMode.value = document.documentElement.classList.contains('dark');
  const observer = new MutationObserver(() => {
    isDarkMode.value = document.documentElement.classList.contains('dark');
  });
  observer.observe(document.documentElement, { attributes: true, attributeFilter: ['class'] });
});

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
});
</script>

<template>
  <div>
    <!-- 全屏背景容器 -->
    <GridBackground>
      <main class="container mx-auto px-4 pt-70 pb-32 text-center min-h-screen flex flex-col justify-center" :style="{ position: 'fixed', top: 0, left: 0, right: 0, width: '100%', transform: `scale(${titleScale})`, transformOrigin: 'top center', transition: 'transform 0.1s ease-out', pointerEvents: 'none', zIndex: 5 }">
        <h1 class="text-5xl font-bold text-gray-800 dark:text-gray-100 mb-6">
          欢迎来到云坛
        </h1>
        <p class="text-xl text-gray-600 dark:text-gray-300">
          探索技术的无限可能
        </p>

        <div id="super_container" class="mt-20 h-40"></div>
      </main>
    </GridBackground>

    <!-- 底部主要内容盒子 - 下移125px -->
    <Rain />
    <div class="relative z-10 w-full pb-16">
      <!-- 卡片本体 -->
      <div class="bg-white dark:bg-black p-8 md:p-12 lg:p-16 shadow-none bottom-card border-0" :style="{ backgroundColor: isDarkMode ? '#000' : '#fff', borderWidth: '0px' }">
        <div class="max-w-7xl mx-auto">
          <!-- 文章卡片网格布局：一排三个 -->
          <div class="grid grid-cols-3 gap-6 justify-items-start">

            <SeparateLine title="最新文章" />

            <ArticleCart :article="demoArticle" />

            <ArticleCart :article="demoArticle" />
          </div>
        </div>
      </div>
      
    </div>
  </div>
</template>

