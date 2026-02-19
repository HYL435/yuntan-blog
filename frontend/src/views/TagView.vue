<script setup lang="ts" name="TagView">
import { computed, ref, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import TagArticleCard from '@/components/cards/TagArticleCard.vue';
import HotTagsSidebar from '@/components/common/HotTagsSidebar.vue';

const route = useRoute();
const router = useRouter();

const tagName = computed(() => route.params.name?.toString() ?? 'JS');
const tagId = computed(() => {
  const found = tabs.value.find(t => t.tagName === tagName.value);
  return found ? found.id : undefined;
});

// 标签类型
interface TagFrontVO {
  id: number;
  tagName: string;
  articleCount?: number;
}

const tabs = ref<TagFrontVO[]>([]);

onMounted(async () => {
  try {
    const tagRes = await import('@/api/http').then(m => m.default.get('/front/tags'));
    if (Array.isArray(tagRes.data?.data)) {
      // 兼容后端返回没有文章数的情况
      tabs.value = tagRes.data.data.map((tag: any) => ({
        ...tag,
        articleCount: typeof tag.articleCount === 'number' ? tag.articleCount : 0
      }));
    } else {
      tabs.value = [];
    }
    // 初次加载时若路径为/tag/标签，自动跳转到第一个标签
    if (route.params.name === '标签' && tabs.value.length > 0) {
      router.replace(`/tag/${encodeURIComponent(tabs.value[0].tagName)}`);
    }
  } catch (e) {
    tabs.value = [];
  }
});

const articles = ref<any[]>([]);
const total = ref(0);
const pageNo = ref(1);
const pageSize = ref(5);
const loading = ref(false);

const fetchArticles = async () => {
  if (!tagId.value) {
    articles.value = [];
    total.value = 0;
    return;
  }
  loading.value = true;
  try {
    const res = await import('@/api/http').then(m => m.default.get('/front/articles/page/categoryOrTags', {
      params: {
        pageNo: pageNo.value,
        pageSize: pageSize.value,
        tagId: tagId.value,
      }
    }));
    const data = res.data?.data;
    if (data) {
      if (Array.isArray(data.records)) {
        articles.value = data.records;
        total.value = data.total || 0;
      } else if (Array.isArray(data.list)) {
        articles.value = data.list;
        total.value = data.total || 0;
      } else if (Array.isArray(data)) {
        articles.value = data;
        total.value = data.length;
      } else {
        articles.value = [];
        total.value = 0;
      }
    } else {
      articles.value = [];
      total.value = 0;
    }
  } catch (e) {
    articles.value = [];
    total.value = 0;
  }
  loading.value = false;
};

// 监听标签变化和分页变化
watch([tagId, pageNo, pageSize], fetchArticles, { immediate: true });

// 监听路由变化，若路径为/tag/标签则自动跳转到第一个标签
watch(
  () => route.params.name,
  (name) => {
    if (name === '标签' && tabs.value.length > 0) {
      router.replace(`/tag/${encodeURIComponent(tabs.value[0].tagName)}`);
    }
  }
);

const goToTag = (name: string) => {
  // 顶部导航栏按钮调用时，name为'标签'或空，需跳转到第一个标签
  if (!name || name === '标签') {
    if (tabs.value.length > 0) {
      router.push(`/tag/${encodeURIComponent(tabs.value[0].tagName)}`);
    }
  } else {
    router.push(`/tag/${encodeURIComponent(name)}`);
  }
};

const goToArticle = (id: string) => {
  router.push(`/article/${id}`);
};
</script>

<template>
  <div class="tag-page min-h-screen bg-[#F7F9FE] dark:bg-[#121212] text-slate-900 dark:text-white flex">
    <div class="hidden md:block w-64 flex-shrink-0 pt-28">
      <HotTagsSidebar :tags="tabs" :goToTag="goToTag" />
    </div>
    <div class="flex-1 max-w-6xl mx-auto px-4 md:px-6 pt-28 pb-16">
      <div class="mt-10">
        <h2 class="text-3xl md:text-4xl font-semibold">标签 - {{ tagName }}</h2>
        <p class="mt-2 text-slate-500 dark:text-slate-400">共 {{ total }} 篇文章</p>
      </div>

      <div class="mt-8 space-y-6">
        <div v-if="loading" class="text-center text-gray-400 py-8">加载中...</div>
        <div v-else-if="!articles.length" class="text-center text-gray-400 py-8">暂无文章</div>
        <TagArticleCard
          v-for="(article, index) in articles"
          :key="article.id"
          :index="(pageNo - 1) * pageSize + index + 1"
          :title="article.title"
          :cover="article.coverImg || article.cover"
          :category="article.category"
          :tag="article.tagName || article.tag"
          :publishTime="article.publishTime"
          @click="goToArticle(article.id)"
        />
      </div>

      <div class="mt-10 flex items-center justify-center gap-3" v-if="total > pageSize">
        <button class="pager" :disabled="pageNo === 1" @click="pageNo > 1 && (pageNo--)">‹</button>
        <button class="pager is-current">{{ pageNo }}</button>
        <button class="pager" :disabled="pageNo >= Math.ceil(total / pageSize)" @click="pageNo < Math.ceil(total / pageSize) && (pageNo++)">›</button>
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
