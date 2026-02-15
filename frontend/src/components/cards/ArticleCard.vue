<template>
  <div class="main" @click="handleCardClick">
    <!-- 顶部卡片区域：封面图 -->
    <!-- 使用可选链和默认图防止 coverImg 为空 -->
    <div class="card" :style="{ backgroundImage: `url(${article.coverImg || defaultCover})` }">
      
      <!-- 状态角标区域 -->
      <div class="badges">
        <span v-if="article.isTop === 1" class="badge top">置顶</span>
        <span v-if="article.isOriginal === 1" class="badge original">原创</span>
        <span v-else class="badge reprint">转载</span>
      </div>

      <!-- 鼠标悬浮時的简介遭罩 -->
      <div class="summary-overlay">
        <p class="summary-text">{{ article.summary || '暂无简介' }}</p>
      </div>
    </div>

    <!-- 中间数据区域：标题与元信息 -->
    <div class="data">
      <div class="text">
        <h3 class="text_title" :title="article.title">{{ article.title }}</h3>
        <div class="text_meta">
          <!-- 显示分类和格式化后的时间 -->
          <span class="category">{{ article.category || '未分类' }}</span>
          <span class="separator">·</span>
          <span class="date">{{ formatDate(article.publishTime) }}</span>
        </div>
      </div>
    </div>

    <!-- 标签区域 -->
    <div class="tags-container">
      <template v-if="article.tags && article.tags.length > 0">
        <span 
          v-for="(tag, index) in article.tags" 
          :key="index" 
          class="tag" 
          @click.stop="handleTagClick(tag)"
        >
          #{{ tag }}
        </span>
      </template>
      <span v-else class="tag empty">无标签</span>
    </div>

    <!-- 底部按钮区域：数据统计 -->
    <div class="btns">
      <!-- 点赞数 -->
      <div class="stats-item likes" title="点赞">
        <svg viewBox="-2 0 105 92" class="icon_svg"><path d="M85.24 2.67C72.29-3.08 55.75 2.67 50 14.9 44.25 2 27-3.8 14.76 2.67 1.1 9.14-5.37 25 5.42 44.38 13.33 58 27 68.11 50 86.81 73.73 68.11 87.39 58 94.58 44.38c10.79-18.7 4.32-35.24-9.34-41.71Z"></path></svg>
        <span class="stats_text">{{ formatNumber(article.likeCount) }}</span>
      </div>
      <!-- 评论数 -->
      <div class="stats-item comments" title="评论">
        <svg viewBox="-405.9 238 56.3 54.8" class="icon_svg"><path d="M-391 291.4c0 1.5 1.2 1.7 1.9 1.2 1.8-1.6 15.9-14.6 15.9-14.6h19.3c3.8 0 4.4-.8 4.4-4.5v-31.1c0-3.7-.8-4.5-4.4-4.5h-47.4c-3.6 0-4.4.9-4.4 4.5v31.1c0 3.7.7 4.4 4.4 4.4h10.4v13.5z"></path></svg>
        <span class="stats_text">{{ formatNumber(article.commentCount) }}</span>
      </div>
      <!-- 浏览量 -->
      <div class="stats-item views" title="浏览">
        <svg viewBox="0 0 30.5 16.5" class="icon_svg"><path d="M15.3 0C8.9 0 3.3 3.3 0 8.3c3.3 5 8.9 8.3 15.3 8.3s12-3.3 15.3-8.3C27.3 3.3 21.7 0 15.3 0zm0 14.5c-3.4 0-6.2-2.8-6.2-6.2C9 4.8 11.8 2 15.3 2c3.4 0 6.2 2.8 6.2 6.2 0 3.5-2.8 6.3-6.2 6.3z"></path></svg>
        <span class="stats_text">{{ formatNumber(article.viewCount) }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';

// 1. 定义与 Java VO 对应的接口
interface ArticleVO {
  id: string | number;
  title: string;
  summary?: string;
  coverImg?: string;
  keywords?: string;
  category?: string;
  tags?: string[];
  isOriginal?: number; // 0-转载，1-原创
  isTop?: number;      // 0-否，1-是
  likeCount?: number;
  commentCount?: number;
  collectCount?: number; // 虽然 UI 没展示，但保留定义
  viewCount?: number;
  publishTime?: string; // LocalDateTime 通常序列化为字符串
  updateTime?: string;
}

// 2. Props 定义
const props = defineProps<{
  article: ArticleVO
}>();

const emit = defineEmits(['click', 'tag-click']);

// 3. 默认封面图 (可换成你自己的占位图)
const defaultCover = 'https://images.unsplash.com/photo-1555099962-4199c345e5dd?q=80&w=2070&auto=format&fit=crop';

// 4. 工具函数：格式化数字 (1200 -> 1.2k)
const formatNumber = (num?: number) => {
  if (!num) return 0;
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k';
  }
  return num;
};

// 5. 工具函数：格式化日期 (2024-01-15T10:30:00 -> 2024-01-15)
const formatDate = (timeStr?: string) => {
  if (!timeStr) return '';
  // 简单截取前10位，或者使用 dayjs 处理
  return timeStr.substring(0, 10);
};

// 事件处理
const handleCardClick = () => {
  emit('click', props.article.id);
};

const handleTagClick = (tag: string) => {
  emit('tag-click', tag);
};
</script>

<style scoped>
/* 深色模式变量定义 */
:global(html.dark) {
  --primary-color: #BE1C41;
}

:global(html.light) {
  --primary-color: #06b6d4;
}

:global(html):not(.dark) {
  --primary-color: #06b6d4;
}

/* 容器主样式 */
.main {
  display: flex;
  flex-direction: column;
  width: 17em; /* 宽度 */
  position: relative;
  padding-bottom: 0.5em; 
  transition: transform 0.3s;
}

:global(html.dark) .main {
  color: var(--text-primary);
}

/* ================== 顶部图片区域 ================== */
.card {
  width: 100%;
  height: 10.5em; /* 图片高度 */
  background-position: center;
  background-size: cover;
  background-repeat: no-repeat;
  border-radius: 12px;
  cursor: pointer;
  position: relative;
  z-index: 2;
  box-shadow: 0 4px 15px rgba(0,0,0,0.08);
  overflow: hidden; /* 防止角标溢出 */
}

:global(html.dark) .card {
  box-shadow: 0 4px 20px rgba(0,0,0,0.4);
}

/* === 简介遭罩层 === */
.summary-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.8);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1.5em;
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: 3;
}

:global(html.dark) .summary-overlay {
  background: rgba(15, 23, 42, 0.9);
}

.main:hover .summary-overlay {
  opacity: 1;
}

.summary-text {
  color: white;
  font-size: 14px;
  line-height: 1.6;
  text-align: center;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 4;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* === 新增：角标样式 === */
.badges {
  position: absolute;
  top: 10px;
  left: 10px;
  display: flex;
  gap: 6px;
  z-index: 4; /* 在遮罩之上 */
}

.badge {
  font-size: 0.65em;
  padding: 3px 8px;
  border-radius: 4px;
  color: white;
  font-weight: bold;
  font-family: sans-serif;
  box-shadow: 0 2px 4px rgba(0,0,0,0.2);
}

.badge.top {
  background: linear-gradient(135deg, #ff6b6b, #ee5253);
}

.badge.original {
  background: linear-gradient(135deg, #48dbfb, #0abde3);
}

:global(html.dark) .badge.original {
  background: linear-gradient(135deg, #BE1C41, #a01535) !important;
}

.badge.reprint {
  background: linear-gradient(135deg, #a5b1c2, #778ca3);
}

/* ================== 中间文字区域 ================== */
.data {
  display: flex;
  flex-direction: column;
  margin-top: 0.8em;
  z-index: 3;
  padding: 0 0.5em;
}

/* 文字颜色适配 */
.text_title {
  font-family: 'Montserrat', sans-serif;
  font-weight: 700;
  font-size: 1.05em;
  line-height: 1.4;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  transition: color 0.2s;
  color: #1f2937; /* 浅色模式：深灰 */
}

:global(html.dark) .text_title {
  color: var(--text-primary);
}

.main:hover .text_title {
  color: var(--primary-color, #06b6d4); /* 使用 CSS 变量 */
}

:global(html.dark) .main:hover .text_title {
  color: var(--primary-color, #BE1C41);
}

.text_meta {
  font-size: 0.75em;
  color: #6b7280; /* 浅色模式：中灰 */
  margin-top: 0.5em;
  font-weight: 500;
  display: flex;
  align-items: center;
}

:global(html.dark) .text_meta {
  color: var(--text-secondary);
}

.category {
  color: var(--primary-color, #06b6d4); /* 使用 CSS 变量 */
  font-weight: 600;
}

:global(html.dark) .category {
  color: var(--primary-color, #BE1C41);
}

.separator {
  margin: 0 6px;
  font-weight: bold;
}

/* ================== 标签区域 ================== */
.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  padding: 0 0.5em;
  margin-top: 0.8em;
  opacity: 0.8;
  transition: 0.3s;
  min-height: 1.5em; /* 占位，防止抖动 */
}

.main:hover .tags-container {
  opacity: 1;
  transform: translateY(2px);
}

.tag {
  font-size: 0.65em;
  padding: 2px 6px;
  border-radius: 4px;
  background: #f3f4f6; /* 浅色模式：浅灰背景 */
  color: #4b5563; /* 浅色模式：深灰文字 */
  cursor: pointer;
  transition: all 0.2s;
  font-family: monospace;
}

:global(html.dark) .tag {
  background: #374151; /* 深色模式：深灰背景 */
  color: var(--text-secondary);
}

.tag:hover {
  background: var(--primary-color, #06b6d4); /* hover：使用 CSS 变量 */
  color: white;
}

:global(html.dark) .tag:hover {
  background: var(--primary-color, #BE1C41);
  color: white;
}

.tag.empty {
  font-style: italic;
  opacity: 0.5;
  background: transparent;
  cursor: default;
}

/* ================== 底部统计按钮 ================== */
.btns {
  display: flex;
  gap: 0.8em;
  margin-top: 0.8em;
  padding: 0 0.5em;
  opacity: 0;
  transform: translateY(-10px);
  transition: .3s ease-in-out;
}

.main:hover .btns {
  opacity: 1;
  transform: translateY(0);
}

.stats-item {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0.25em 0.5em;
  border-radius: 6px;
  background-color: #f9fafb; /* 浅色模式：极浅灰 */
  transition: .2s ease-in-out;
  cursor: pointer;
}

:global(html.dark) .stats-item {
  background-color: #374151; /* 深色模式：深灰 */
}

.stats_text {
  font-family: 'Montserrat', sans-serif;
  font-size: 0.7em;
  margin-left: 0.3em;
  color: #4b5563; /* 浅色模式：深灰 */
  font-weight: 600;
}

:global(html.dark) .stats_text {
  color: var(--text-secondary);
}

.icon_svg {
  width: 12px;
  height: 12px;
  fill: #6b7280; /* 浅色模式：中灰 */
}

:global(html.dark) .icon_svg {
  fill: var(--text-secondary);
}

.stats-item:hover {
  background-color: var(--primary-color, #06b6d4); /* hover：使用 CSS 变量 */
}

:global(html.dark) .stats-item:hover {
  background-color: var(--primary-color, #BE1C41);
}
.stats-item:hover .icon_svg {
  fill: white;
}
.stats-item:hover .stats_text {
  color: white;
}

/* ================== 阅读按钮 ================== */
.card_content {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  opacity: 0;
  transition: 0.3s;
  z-index: 3;
}

.main:hover .card_content {
  opacity: 1;
}

.read-btn {
  padding: 0.5em 1em;
  border-radius: 20px;
  font-size: 0.8em;
  font-weight: bold;
  background: rgba(0, 0, 0, 0.6); /* 浅色模式：半透明黑 */
  backdrop-filter: blur(4px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  color: white;
  cursor: pointer;
  transform: translateY(10px);
  transition: 0.3s;
}

:global(html.dark) .read-btn {
  background: rgba(15, 23, 42, 0.8); /* 深色模式：深蓝黑 */
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.main:hover .read-btn {
  transform: translateY(0);
}

.read-btn:hover {
  background: var(--primary-color, #06b6d4); /* hover：使用 CSS 变量 */
  color: white;
  border-color: var(--primary-color, #06b6d4);
}

:global(html.dark) .read-btn:hover {
  background: var(--primary-color, #BE1C41);
  color: white;
  border-color: var(--primary-color, #BE1C41);
}
</style>

<style>
/* 深色模式全局颜色覆盖 */
html.dark .category {
  color: #BE1C41 !important;
}

html.dark .main:hover .text_title {
  color: #BE1C41 !important;
}

html.dark .tag:hover {
  background: #BE1C41 !important;
}

html.dark .stats-item:hover {
  background-color: #BE1C41 !important;
}

html.dark .read-btn:hover {
  background: #BE1C41 !important;
  border-color: #BE1C41 !important;
}

html.dark .badge.original {
  background: linear-gradient(135deg, #BE1C41, #a01535) !important;
}
</style>