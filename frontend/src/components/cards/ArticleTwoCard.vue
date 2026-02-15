<template>
  <!-- 外层容器：Neomorphism 风格 + Blob 动画 -->
  <article class="group relative w-full h-full overflow-hidden cursor-pointer" @click="handleClick">
    
    <!-- 【核心】Blob 光球：旋转发光效果 -->
    <div class="blob-container absolute inset-0 z-0 pointer-events-none">
      <!-- 主光球：缓慢旋转，渐变发光 -->
      <div class="blob-main absolute top-1/3 left-1/3 w-32 h-32 
                  bg-gradient-to-br from-indigo-400 via-purple-500 to-blue-500
                  opacity-30 dark:opacity-50
                  rounded-full blur-xl
                  animate-blob-bounce
                  shadow-[0_0_40px_rgba(99,102,241,0.6)]
                  dark:shadow-[0_0_60px_rgba(147,51,234,0.8)]"></div>
      
      <!-- 辅助光球：跟随主球，微小偏移 -->
      <div class="blob-secondary absolute top-2/3 right-1/3 w-24 h-24 
                  bg-gradient-to-br from-purple-500 via-blue-500 to-indigo-600
                  opacity-20 dark:opacity-40
                  rounded-full blur-lg
                  animate-blob-bounce-delayed
                  shadow-[0_0_30px_rgba(147,51,234,0.5)]
                  dark:shadow-[0_0_50px_rgba(59,130,246,0.7)]"></div>
    </div>

    <!-- 【Neomorphism】卡片外壳：凹凸阴影 -->
    <div class="card-shell relative flex flex-col md:flex-row h-full 
                bg-white/95 dark:bg-[#1a1a2e]/95
                rounded-[18px] border border-white/20 dark:border-slate-800/40
                shadow-[20px_20px_60px_rgba(190,190,190,0.3),_-20px_-20px_60px_rgba(255,255,255,0.8)]
                dark:shadow-[20px_20px_60px_rgba(26,26,46,0.8),_-20px_-20px_60px_rgba(30,30,58,0.4)]
                transition-all duration-600 ease-out
                group-hover:shadow-[15px_15px_40px_rgba(99,102,241,0.2),_-15px_-15px_40px_rgba(255,255,255,0.6)]
                group-hover:-translate-y-1 group-hover:scale-[1.01]
                backdrop-blur-sm dark:backdrop-blur-md
                group-hover:border-indigo-300/30 dark:group-hover:border-indigo-600/30"
        :class="{ 'md:flex-row-reverse': imagePosition === 'left' }"
    >
      
      <!-- 图片区域：Neomorphism 边框 -->
      <div class="w-full md:w-5/12 relative overflow-hidden h-56 md:h-auto shrink-0">
        <!-- 图片容器：Neomorphism 内嵌效果 -->
        <div class="relative w-full h-full p-[2px] rounded-[16px] mx-[-2px] my-[-2px]
                    bg-white/90 dark:bg-[#1a1a2e]/90
                    shadow-inner shadow-slate-200/50 dark:shadow-slate-800/50
                    group-hover:bg-indigo-50/50 dark:group-hover:bg-indigo-900/20">
          
          <div class="w-full h-full overflow-hidden rounded-[14px] bg-slate-50 dark:bg-slate-900/50">
            <img 
              class="w-full h-full object-cover 
                     transition-all duration-800 ease-out 
                     group-hover:scale-110 group-hover:brightness-110
                     group-hover:contrast-105
                     group-hover:shadow-inner shadow-slate-100/50 dark:shadow-slate-800/50"
              :src="coverUrl" 
              :alt="title" 
            />
          </div>
        </div>
        
        <!-- 分类标签：Neomorphism 风格 -->
        <span class="absolute top-3 left-3 z-30 px-3 py-1.5 text-xs font-bold text-slate-700 dark:text-slate-200 
                     bg-white/90 dark:bg-slate-800/90 backdrop-blur-sm 
                     rounded-full border border-slate-200/50 dark:border-slate-700/50
                     shadow-[5px_5px_15px_rgba(190,190,190,0.3),_-5px_-5px_15px_rgba(255,255,255,0.8)]
                     dark:shadow-[5px_5px_15px_rgba(26,26,46,0.8),_-5px_-5px_15px_rgba(30,30,58,0.4)]
                     transform translate-y-2 group-hover:translate-y-0
                     group-hover:shadow-[3px_3px_10px_rgba(99,102,241,0.3),_-3px_-3px_10px_rgba(255,255,255,0.6)]
                     transition-all duration-300">
          {{ primaryTag }}
        </span>
      </div>

      <!-- 内容区域 -->
      <div class="flex-1 flex flex-col justify-between p-6 md:p-8 relative">
        
        <!-- 内容背景：Neomorphism 内嵌 -->
        <div class="absolute inset-0 -mx-6 -mb-6 md:-mx-8 md:-mb-8
                    bg-white/80 dark:bg-[#1a1a2e]/80
                    rounded-b-[18px] md:rounded-r-[18px] md:rounded-bl-none
                    shadow-inner shadow-slate-200/30 dark:shadow-slate-800/30
                    group-hover:bg-indigo-50/30 dark:group-hover:bg-indigo-900/10"></div>

        <div class="relative z-10">
          <!-- Meta 信息：Neomorphism 标签 -->
          <div class="flex flex-wrap items-center gap-3 text-xs md:text-sm 
                      text-slate-600 dark:text-slate-300 mb-4
                      opacity-90 group-hover:opacity-100">
            
            <!-- 日期标签 -->
            <span class="flex items-center bg-white/90 dark:bg-slate-800/80 
                         px-3.5 py-1.5 rounded-full text-slate-700 dark:text-slate-200 
                         backdrop-blur-sm border border-slate-200/50 dark:border-slate-700/50
                         shadow-[3px_3px_10px_rgba(190,190,190,0.3),_-3px_-3px_10px_rgba(255,255,255,0.8)]
                         dark:shadow-[3px_3px_10px_rgba(26,26,46,0.6),_-3px_-3px_10px_rgba(30,30,58,0.3)]
                         transform scale-95 group-hover:scale-100
                         group-hover:shadow-[2px_2px_8px_rgba(99,102,241,0.2),_-2px_-2px_8px_rgba(255,255,255,0.5)]
                         transition-all duration-300">
              📅 {{ publishTime.split('T')[0] }}
            </span>
            
            <!-- 热度标签 -->
            <span class="flex items-center space-x-1.5 
                         text-orange-600 dark:text-orange-400 
                         bg-orange-100/90 dark:bg-orange-900/40 
                         px-3 py-1.5 rounded-full backdrop-blur-sm
                         border border-orange-200/50 dark:border-orange-800/50
                         shadow-[3px_3px_10px_rgba(251,146,60,0.3),_-3px_-3px_10px_rgba(255,255,255,0.8)]
                         dark:shadow-[3px_3px_10px_rgba(251,146,60,0.4),_-3px_-3px_10px_rgba(30,30,58,0.3)]
                         group-hover:scale-105 transition-all duration-300
                         animate-pulse-slow">
              <span>🔥</span>
              <span class="font-medium">{{ heat }}</span>
            </span>
          </div>

          <!-- 标题：Neomorphism 文字效果 -->
          <h3 class="text-xl md:text-2xl lg:text-3xl font-bold 
                     text-slate-800 dark:text-slate-50 
                     mb-4 leading-tight line-clamp-2
                     group-hover:text-indigo-700 dark:group-hover:text-indigo-300
                     transition-all duration-400
                     shadow-[0_2px_8px_rgba(190,190,190,0.2)]
                     dark:shadow-[0_2px_8px_rgba(26,26,46,0.4)]
                     group-hover:shadow-[0_3px_12px_rgba(99,102,241,0.3)]">
            {{ title }}
          </h3>

          <!-- 摘要 -->
          <p class="text-slate-600 dark:text-slate-300 text-sm md:text-base 
                    leading-6 line-clamp-3 mb-6
                    opacity-85 group-hover:opacity-100
                    transition-all duration-400
                    shadow-sm dark:shadow-md">
            {{ excerpt }}
          </p>
        </div>

        <!-- 底部统计：Neomorphism 按钮组 -->
        <div class="relative z-10 pt-5 border-t border-slate-200/40 dark:border-slate-700/60">
          
          <!-- 互动数据 -->
          <div class="flex flex-wrap gap-3 text-xs md:text-sm text-slate-500 dark:text-slate-400 mb-3">
            <!-- 评论按钮 -->
            <div class="flex items-center space-x-1.5 bg-white/90 dark:bg-slate-800/80 
                        px-3.5 py-2.5 rounded-xl backdrop-blur-sm 
                        border border-slate-200/50 dark:border-slate-700/50
                        shadow-[4px_4px_12px_rgba(190,190,190,0.3),_-4px_-4px_12px_rgba(255,255,255,0.8)]
                        dark:shadow-[4px_4px_12px_rgba(26,26,46,0.6),_-4px_-4px_12px_rgba(30,30,58,0.3)]
                        group-hover:scale-102 group-hover:shadow-[2px_2px_8px_rgba(99,102,241,0.2),_-2px_-2px_8px_rgba(255,255,255,0.6)]
                        transition-all duration-300 cursor-default">
              <i class="text-green-500 dark:text-green-400 text-base">💬</i>
              <span class="font-medium">{{ comments }}</span>
            </div>
            
            <!-- 点赞按钮 -->
            <div class="flex items-center space-x-1.5 bg-white/90 dark:bg-slate-800/80 
                        px-3.5 py-2.5 rounded-xl backdrop-blur-sm 
                        border border-slate-200/50 dark:border-slate-700/50
                        shadow-[4px_4px_12px_rgba(190,190,190,0.3),_-4px_-4px_12px_rgba(255,255,255,0.8)]
                        dark:shadow-[4px_4px_12px_rgba(26,26,46,0.6),_-4px_-4px_12px_rgba(30,30,58,0.3)]
                        group-hover:scale-102 group-hover:shadow-[2px_2px_8px_rgba(99,102,241,0.2),_-2px_-2px_8px_rgba(255,255,255,0.6)]
                        transition-all duration-300 cursor-default">
              <i class="text-red-500 dark:text-red-400 text-base animate-pulse">❤</i>
              <span class="font-medium">{{ likes }}</span>
            </div>
          </div>

          <!-- 底部操作 -->
          <div class="flex items-center justify-between">
            <!-- 标签 -->
            <span class="px-4 py-2.5 rounded-xl text-sm font-semibold 
                         bg-white/90 dark:bg-slate-800/80 
                         text-indigo-700 dark:text-indigo-300
                         border border-slate-200/50 dark:border-slate-700/50
                         shadow-[3px_3px_10px_rgba(190,190,190,0.3),_-3px_-3px_10px_rgba(255,255,255,0.8)]
                         dark:shadow-[3px_3px_10px_rgba(26,26,46,0.6),_-3px_-3px_10px_rgba(30,30,58,0.3)]
                         group-hover:scale-102 group-hover:shadow-[2px_2px_8px_rgba(99,102,241,0.2),_-2px_-2px_8px_rgba(255,255,255,0.6)]
                         transition-all duration-300">
              # {{ secondaryTag }}
            </span>
            
            <!-- 阅读按钮：Neomorphism 风格 -->
            <button class="relative w-12 h-12 flex items-center justify-center rounded-xl 
                          bg-white/90 dark:bg-slate-800/80 
                          text-slate-600 dark:text-slate-300 
                          border border-slate-200/50 dark:border-slate-700/50
                          shadow-[5px_5px_15px_rgba(190,190,190,0.3),_-5px_-5px_15px_rgba(255,255,255,0.8)]
                          dark:shadow-[5px_5px_15px_rgba(26,26,46,0.6),_-5px_-5px_15px_rgba(30,30,58,0.3)]
                          group-hover:scale-110 group-hover:shadow-[3px_3px_12px_rgba(99,102,241,0.3),_-3px_-3px_12px_rgba(255,255,255,0.6)]
                          group-hover:text-indigo-600 dark:group-hover:text-indigo-400
                          transition-all duration-300 transform rotate-0
                          group-hover:rotate-[-10deg]">
              <span class="text-xl transition-transform duration-300 group-hover:translate-x-1">➔</span>
            </button>
          </div>
        </div>
      </div>

    </div>
  </article>
</template>

<script setup lang="ts">
import { withDefaults, defineProps } from 'vue';

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
/* 【核心】Blob 动画：基于你提供的样式 */
@keyframes blob-bounce {
  0% {
    transform: translate(-50%, -50%) translate3d(0, 0, 0);
  }
  25% {
    transform: translate(-50%, -50%) translate3d(50px, 0, 0);
  }
  50% {
    transform: translate(-50%, -50%) translate3d(50px, 50px, 0);
  }
  75% {
    transform: translate(-50%, -50%) translate3d(0, 50px, 0);
  }
  100% {
    transform: translate(-50%, -50%) translate3d(0, 0, 0);
  }
}

@keyframes blob-bounce-delayed {
  0% {
    transform: translate(-50%, -50%) translate3d(0, 0, 0);
  }
  25% {
    transform: translate(-50%, -50%) translate3d(-30px, 0, 0);
  }
  50% {
    transform: translate(-50%, -50%) translate3d(-30px, -30px, 0);
  }
  75% {
    transform: translate(-50%, -50%) translate3d(0, -30px, 0);
  }
  100% {
    transform: translate(-50%, -50%) translate3d(0, 0, 0);
  }
}

.animate-blob-bounce {
  animation: blob-bounce 6s infinite ease-in-out;
}

.animate-blob-bounce-delayed {
  animation: blob-bounce-delayed 6s infinite ease-in-out 2s;
}

/* 【新增】慢速心跳动画 */
@keyframes pulse-slow {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.08); }
}
.animate-pulse-slow {
  animation: pulse-slow 3s ease-in-out infinite;
}

/* 基础样式 */
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

/* 移动端优化 */
@media (max-width: 768px) {
  .card-shell {
    flex-direction: column !important;
  }
  
  .card-shell > div:first-child {
    height: 200px !important;
  }
  
  /* 移动端 blob 尺寸调整 */
  .blob-main {
    width: 24px !important;
    height: 24px !important;
  }
  
  .blob-secondary {
    width: 18px !important;
    height: 18px !important;
  }
}

/* 深色模式 Neomorphism 优化 */
:global(.dark) .card-shell {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f0f23 100%);
  border-color: rgba(99, 102, 241, 0.15);
}

/* 悬浮增强 */
.group:hover .card-shell {
  border-color: rgba(99, 102, 241, 0.25);
  box-shadow: 
    15px 15px 40px rgba(26, 26, 46, 0.6), 
    -15px -15px 40px rgba(30, 30, 58, 0.4),
    0 0 60px rgba(99, 102, 241, 0.2);
}
</style>