<script setup lang="ts" name="TagView">
import { computed, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import TagArticleCard from '@/components/cards/TagArticleCard.vue';

const route = useRoute();
const router = useRouter();

const tagName = computed(() => route.params.name?.toString() ?? 'JS');

const tabs = ref([
  { name: 'JS', count: 3 },
  { name: 'CSS', count: 0 },
  { name: 'Vue', count: 10 },
  { name: 'NodeJS', count: 1 },
  { name: '博客', count: 3 },
  { name: '浏览器', count: 3 },
  { name: '优化', count: 1 },
  { name: '前端', count: 0 },
  { name: 'axios', count: 0 },
  { name: '渡一面试题(JS)', count: 15 },
  { name: '渡一面试题(Promise)', count: 1 },
  { name: '网络', count: 10 },
  { name: '渡一面试题(工程化)', count: 2 },
  { name: 'Django', count: 3 },
  { name: '高仿网易云', count: 1 }
]);

const articles = computed(() => [
  {
    id: 'tag-1',
    title: 'js实现粒子时钟特效',
    cover: 'https://images.unsplash.com/photo-1489515217757-5fd1be406fef?q=80&w=1200&auto=format&fit=crop',
    category: '学习笔记',
    tag: tagName.value,
    publishTime: '2023-08-27 14:37:35'
  },
  {
    id: 'tag-2',
    title: '几步让你记住JS中等号运算符的运算和转换规则',
    cover: 'https://images.unsplash.com/photo-1484417894907-623942c8ee29?q=80&w=1200&auto=format&fit=crop',
    category: '学习笔记',
    tag: tagName.value,
    publishTime: '2023-08-27 14:25:37'
  },
  {
    id: 'tag-3',
    title: '几行代码教你彻底明白自属性到底存在与否',
    cover: 'https://images.unsplash.com/photo-1515879218367-8466d910aaa4?q=80&w=1200&auto=format&fit=crop',
    category: '学习笔记',
    tag: tagName.value,
    publishTime: '2023-08-27 14:18:17'
  }
]);

const goToTag = (name: string) => {
  router.push(`/tag/${encodeURIComponent(name)}`);
};

const goToArticle = (id: string) => {
  router.push(`/article/${id}`);
};
</script>

<template>
  <div class="tag-page min-h-screen bg-[#F7F9FE] dark:bg-[#121212] text-slate-900 dark:text-white">
    <div class="max-w-6xl mx-auto px-4 md:px-6 pt-28 pb-16">
      <div class="tabs-wrap">
        <button
          v-for="tab in tabs"
          :key="tab.name"
          class="tab-chip"
          :class="{ 'is-active': tab.name === tagName }"
          @click="goToTag(tab.name)"
        >
          <span class="tab-name">{{ tab.name }}</span>
          <span class="tab-count">{{ tab.count }}</span>
        </button>
      </div>

      <div class="mt-10">
        <h2 class="text-3xl md:text-4xl font-semibold">标签 - {{ tagName }}</h2>
        <p class="mt-2 text-slate-500 dark:text-slate-400">共 {{ articles.length }} 篇文章</p>
      </div>

      <div class="mt-8 space-y-6">
        <TagArticleCard
          v-for="(article, index) in articles"
          :key="article.id"
          :index="index + 1"
          :title="article.title"
          :cover="article.cover"
          :category="article.category"
          :tag="article.tag"
          :publishTime="article.publishTime"
          @click="goToArticle(article.id)"
        />
      </div>

      <div class="mt-10 flex items-center justify-center gap-3">
        <button class="pager">‹</button>
        <button class="pager is-current">1</button>
        <button class="pager">›</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.tag-page {
  background-image: radial-gradient(circle at top, rgba(15, 23, 42, 0.08), transparent 45%);
}

:global(html.dark) .tag-page {
  background-image: radial-gradient(circle at top, rgba(255, 255, 255, 0.08), transparent 45%);
}

.tabs-wrap {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.tab-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: 999px;
  border: 1px solid rgba(15, 23, 42, 0.12);
  color: rgba(15, 23, 42, 0.85);
  background: rgba(255, 255, 255, 0.8);
  transition: all 0.2s ease;
}

:global(html.dark) .tab-chip {
  border-color: rgba(255, 255, 255, 0.15);
  color: rgba(255, 255, 255, 0.85);
  background: rgba(255, 255, 255, 0.05);
}

.tab-chip:hover {
  border-color: rgba(99, 102, 241, 0.5);
  color: #4f46e5;
}

:global(html.dark) .tab-chip:hover {
  border-color: rgba(255, 200, 86, 0.6);
  color: #ffd36b;
}

.tab-chip.is-active {
  border-color: #4f46e5;
  box-shadow: 0 0 16px rgba(99, 102, 241, 0.35);
  color: #4f46e5;
}

:global(html.dark) .tab-chip.is-active {
  border-color: #ffd36b;
  box-shadow: 0 0 18px rgba(255, 211, 107, 0.4);
  color: #ffd36b;
}

.tab-count {
  font-size: 12px;
  background: rgba(15, 23, 42, 0.08);
  padding: 2px 6px;
  border-radius: 999px;
}

:global(html.dark) .tab-count {
  background: rgba(255, 255, 255, 0.12);
}


.pager {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.9);
  color: #475569;
  border: 1px solid rgba(148, 163, 184, 0.4);
  transition: all 0.2s ease;
}

:global(html.dark) .pager {
  background: rgba(255, 255, 255, 0.1);
  color: #e2e8f0;
  border-color: transparent;
}

.pager:hover {
  border-color: rgba(99, 102, 241, 0.5);
  color: #4f46e5;
}

:global(html.dark) .pager:hover {
  border-color: rgba(255, 211, 107, 0.6);
  color: #ffd36b;
}

.pager.is-current {
  background: #4f46e5;
  color: #ffffff;
  border-color: transparent;
}

:global(html.dark) .pager.is-current {
  background: #ffd36b;
  color: #1f1f1f;
}

</style>
