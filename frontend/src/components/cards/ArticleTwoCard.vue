<template>
  <article class="group relative w-full h-full cursor-pointer min-h-[220px]" @click="handleClick">
    <!-- 深色背景+红色网格 -->
    <div class="absolute inset-0 z-0 pointer-events-none">
      <div class="w-full h-full bg-gradient-to-br from-[#1E1E1E] via-[#1E1E1E] to-[#1E1E1E] opacity-95"></div>
      <svg class="absolute inset-0 w-full h-full opacity-10" xmlns="http://www.w3.org/2000/svg">
        <g>
          <rect fill="none" stroke="#ff0055" stroke-width="1" width="100%" height="100%"/>
          <g>
            <g v-for="i in 16" :key="i">
              <line :x1="i*40" y1="0" :x2="i*40" y2="100%" stroke="#ff0055" stroke-width="1" />
              <line x1="0" :y1="i*40" x2="100%" :y2="i*40" stroke="#ff0055" stroke-width="1" />
            </g>
          </g>
        </g>
      </svg>
    </div>
    <!-- 卡片主体 -->
    <div class="relative z-10 flex flex-col md:flex-row h-full rounded-2xl border border-[#333355] bg-[#1E1E1E] shadow-xl overflow-hidden group-hover:border-[#ff0055] transition-all duration-500"
         :class="{ 'md:flex-row-reverse': imagePosition === 'left' }">
      <!-- 图片区域 -->
      <div class="w-full md:w-5/12 relative overflow-hidden h-56 md:h-auto shrink-0">
        <img class="w-full h-full object-cover transition-all duration-700 ease-out group-hover:scale-105" :src="coverUrl" :alt="title" />
        <!-- 图片渐变遮罩 -->
        <div class="absolute inset-0 bg-gradient-to-t from-[#1E1E1E] via-transparent to-transparent"></div>
        <!-- 分类标签 -->
        <span class="absolute top-4 left-4 z-20 px-4 py-1.5 text-xs font-medium tracking-wide bg-[#1E1E1E] text-[#e6e6ff] shadow rounded-2xl border border-[#333355]">{{ primaryTag }}</span>
      </div>
      <!-- 内容区域 -->
      <div class="flex-1 flex flex-col justify-between p-6 md:p-8">
        <div>
          <!-- Meta 信息 -->
          <div class="flex items-center gap-3 mb-5">
            <span class="px-4 py-1.5 text-xs bg-[#1E1E1E] text-[#e6e6ff] rounded-2xl border border-[#333355]">{{ publishTime.split('T')[0] }}</span>
            <span class="px-4 py-1.5 text-xs font-medium bg-[#333355]/30 text-[#ff0055] border border-[#ff0055]/30 rounded-2xl">热度 {{ heat }}</span>
          </div>
          <!-- 标题 -->
          <h3 class="text-2xl md:text-3xl font-semibold leading-tight line-clamp-2 mb-4 text-[#ffffff] group-hover:text-[#ff0055] transition-colors duration-300">{{ title }}</h3>
          <!-- 摘要 -->
          <p class="text-[#e6e6ff] text-[15px] leading-relaxed line-clamp-3">{{ excerpt }}</p>
        </div>
        <!-- 底部 -->
        <div class="pt-6 mt-auto border-t border-[#333355]/60">
          <div class="flex items-center justify-between">
            <div class="flex gap-3">
              <div class="flex items-center gap-2 px-4 py-2 text-sm bg-[#1E1E1E] hover:bg-[#333355] transition-colors rounded-2xl border border-[#333355] text-[#e6e6ff]">
                 <span>💬</span>{{ comments }}
               </div>
               <div class="flex items-center gap-2 px-4 py-2 text-sm bg-[#1E1E1E] hover:bg-[#333355] transition-colors rounded-2xl border border-[#333355] text-[#ff0055]">
                 <span>❤</span>{{ likes }}
               </div>
             </div>
             <button class="w-10 h-10 flex items-center justify-center rounded-2xl bg-[#1E1E1E] hover:bg-[#ff0055] border border-[#333355] text-[#e6e6ff] hover:text-white shadow transition-all duration-300 text-xl hover:scale-110 hover:shadow-pink-500/30">→</button>
          </div>
        </div>
      </div>
    </div>
  </article>
</template>

<script setup lang="ts" name="ArticleTwoCard">
interface Props {
  articleId?: string | number;
  title?: string;
  publishTime?: string;
  heat?: number;
  comments?: number;
  likes?: number;
  excerpt?: string;
  primaryTag?: string;
  secondaryTag?: string;
  coverUrl?: string;
  imagePosition?: 'left' | 'right';
}

const props = withDefaults(defineProps<Props>(), {
  articleId: 'demo-1',
  title: '探索Vue 3组合式API的艺术：构建现代Web应用',
  publishTime: '2026-02-04',
  heat: 1205,
  comments: 24,
  likes: 186,
  excerpt: '在现代前端开发中，Vue 3 的 Composition API 带来了革命性的变化。本文将深入探讨如何利用这些新特性来组织代码，提升逻辑复用性，并构建出更加健壮的组件系统...',
  primaryTag: '前端技术',
  secondaryTag: 'Vue3',
  coverUrl: 'https://images.unsplash.com/photo-1498050108023-c5249f4df085?q=80&w=1200&auto=format&fit=crop',
  imagePosition: 'right'
});

const emit = defineEmits(['click']);

const handleClick = () => {
  emit('click', props.articleId);
};
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>