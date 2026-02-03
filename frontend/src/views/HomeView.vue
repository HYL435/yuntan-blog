<script setup>
import Header from '@/components/layout/Header.vue';
import GridBackground from '@/components/background/GridBackground.vue';
</script>

<template>
  <div>
    <!-- 全屏背景容器 -->
    <GridBackground>
      
      <!-- 这里放你的 Hero 区域内容 -->
      <main class="container mx-auto px-4 pt-32 pb-12 text-center">
        <h1 class="text-5xl font-bold text-gray-800 dark:text-gray-100 mb-6">
          欢迎来到云坛
        </h1>
        <p class="text-xl text-gray-600 dark:text-gray-300">
          探索技术的无限可能
        </p>
        
        <!-- 这里可以放更多内容，比如文章列表等 -->
        <!-- id="super_container" 对应导航栏的锚点 -->
        <div id="super_container" class="mt-20 h-250">
           文章列表区域...
        </div>
      </main>

    </GridBackground>
  </div>
</template>