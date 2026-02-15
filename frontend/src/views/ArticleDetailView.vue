<script setup lang="ts" name="ArticleDetailView">
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();

const articleId = computed(() => route.params.id?.toString() ?? '');

const article = computed(() => {
  const fallback = {
    id: articleId.value || 'unknown',
    title: '文章详情',
    summary: '这里是文章详情内容示例。',
    coverImg: 'https://images.unsplash.com/photo-1498050108023-c5249f4df085?q=80&w=1600&auto=format&fit=crop',
    category: '默认分类',
    tags: ['示例', '文章'],
    publishTime: '2026-02-15',
    author: '云坛'
  };

  if (!articleId.value) return fallback;

  return {
    ...fallback,
    id: articleId.value,
    title: articleId.value === 'demo-1' ? '探索Vue 3组合式API的艺术' : `文章 #${articleId.value}`,
    summary: '本页为文章详情演示，后续可接入接口数据进行渲染。'
  };
});

const goBack = () => router.back();
</script>

<template>
  <div class="min-h-screen bg-[#F7F9FE] dark:bg-[#121212] transition-colors">
    <div class="max-w-5xl mx-auto px-4 md:px-6 py-10">
      <button
        class="inline-flex items-center gap-2 text-sm font-medium text-slate-600 dark:text-slate-300 hover:text-indigo-600 dark:hover:text-indigo-300 transition-colors"
        @click="goBack"
      >
        <span>←</span>
        <span>返回</span>
      </button>

      <div class="mt-6 overflow-hidden rounded-3xl shadow-2xl bg-white dark:bg-[#1E1E1E] border border-slate-100 dark:border-slate-800">
        <div
          class="h-72 md:h-96 bg-center bg-cover"
          :style="{ backgroundImage: `url(${article.coverImg})` }"
        />

        <div class="p-6 md:p-10">
          <div class="flex flex-wrap items-center gap-3 text-sm text-slate-500 dark:text-slate-400">
            <span class="px-3 py-1 rounded-full bg-indigo-50 text-indigo-600 dark:bg-indigo-950/40 dark:text-indigo-300">
              {{ article.category }}
            </span>
            <span>·</span>
            <span>{{ article.publishTime }}</span>
            <span>·</span>
            <span>{{ article.author }}</span>
          </div>

          <h1 class="mt-4 text-3xl md:text-4xl font-bold text-slate-900 dark:text-slate-100">
            {{ article.title }}
          </h1>

          <p class="mt-4 text-base md:text-lg leading-7 text-slate-600 dark:text-slate-300">
            {{ article.summary }}
          </p>

          <div class="mt-6 flex flex-wrap gap-2">
            <span
              v-for="tag in article.tags"
              :key="tag"
              class="px-3 py-1 text-xs font-medium rounded-full bg-slate-100 text-slate-600 dark:bg-slate-800 dark:text-slate-300"
            >
              #{{ tag }}
            </span>
          </div>

          <div class="mt-8 space-y-4 text-slate-700 dark:text-slate-200 leading-7">
            <p>这里是文章正文内容的示例段落，可替换为真实富文本或 Markdown 渲染内容。</p>
            <p>你可以在此继续扩展目录、正文、代码块、引用等模块。</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
