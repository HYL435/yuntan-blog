<script setup lang="ts" name="TagArticleCard">
import { ref, onMounted, onUnmounted } from 'vue';
interface Props {
  index?: number;
  title: string;
  cover: string;
  category: string;
  tag: string;
  publishTime: string;
}

const props = withDefaults(defineProps<Props>(), {
  index: 1
});

const emit = defineEmits(['click']);

const isDark = ref(false);

const syncDarkMode = () => {
  const html = document.documentElement.classList;
  const body = document.body.classList;
  isDark.value =
    html.contains('dark') ||
    html.contains('dark-mode') ||
    body.contains('dark') ||
    body.contains('dark-mode');
};

let observer: MutationObserver | null = null;
onMounted(() => {
  syncDarkMode();
  observer = new MutationObserver(syncDarkMode);
  observer.observe(document.documentElement, { attributes: true, attributeFilter: ['class'] });
  observer.observe(document.body, { attributes: true, attributeFilter: ['class'] });
});

onUnmounted(() => {
  observer?.disconnect();
});

const handleClick = () => {
  emit('click');
};
</script>

<template>
  <article class="tag-card" :class="{ 'is-dark': isDark }" @click="handleClick">
    <span class="card-index">{{ index }}</span>
    <div class="card-cover" :style="{ backgroundImage: `url(${cover})` }"></div>
    <div class="card-body">
      <h3 class="card-title">{{ title }}</h3>
      <div class="card-meta">
        <span>📚 {{ category }}</span>
        <span>🏷️ {{ tag }}</span>
        <span>🕒 {{ publishTime }}</span>
      </div>
    </div>
  </article>
</template>

<style scoped>
.tag-card {
  position: relative;
  display: grid;
  grid-template-columns: 160px 1fr;
  gap: 18px;
  align-items: center;
  padding: 16px 18px;
  border-radius: 18px;
  background: #ffffff;
  border: 1px solid rgba(148, 163, 184, 0.45);
  box-shadow: 0 10px 24px rgba(15, 23, 42, 0.08);
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

:global(html.dark) .tag-card,
:global(html.dark-mode) .tag-card,
:global(.dark) .tag-card,
:global(.dark-mode) .tag-card,
.tag-card.is-dark {
  background: rgba(22, 24, 32, 0.92);
  border-color: rgba(148, 163, 184, 0.25);
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.35);
}

.tag-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 16px 28px rgba(15, 23, 42, 0.12);
}

:global(html.dark) .tag-card:hover,
:global(html.dark-mode) .tag-card:hover,
:global(.dark) .tag-card:hover,
:global(.dark-mode) .tag-card:hover,
.tag-card.is-dark:hover {
  box-shadow: 0 16px 32px rgba(0, 0, 0, 0.45);
}

.card-index {
  position: absolute;
  top: 10px;
  right: 14px;
  font-size: 18px;
  color: #6366f1;
  font-weight: 700;
}

:global(html.dark) .card-index,
:global(html.dark-mode) .card-index,
:global(.dark) .card-index,
:global(.dark-mode) .card-index,
.tag-card.is-dark .card-index {
  color: rgba(148, 163, 184, 0.9);
}

.card-cover {
  width: 100%;
  height: 90px;
  border-radius: 12px;
  background-size: cover;
  background-position: center;
  border: 1px solid rgba(15, 23, 42, 0.12);
}

:global(html.dark) .card-cover,
:global(html.dark-mode) .card-cover,
:global(.dark) .card-cover,
:global(.dark-mode) .card-cover,
.tag-card.is-dark .card-cover {
  border-color: rgba(148, 163, 184, 0.2);
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #0f172a;
}

:global(html.dark) .card-title,
:global(html.dark-mode) .card-title,
:global(.dark) .card-title,
:global(.dark-mode) .card-title,
.tag-card.is-dark .card-title {
  color: #f1f5f9;
}

.card-meta {
  margin-top: 8px;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  color: rgba(15, 23, 42, 0.6);
  font-size: 13px;
}

:global(html.dark) .card-meta,
:global(html.dark-mode) .card-meta,
:global(.dark) .card-meta,
:global(.dark-mode) .card-meta,
.tag-card.is-dark .card-meta {
  color: rgba(226, 232, 240, 0.65);
}

@media (max-width: 640px) {
  .tag-card {
    grid-template-columns: 1fr;
  }

  .card-cover {
    height: 140px;
  }
}
</style>
