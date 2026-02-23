<template>
  <!-- 顶部加载进度条 -->
  <div class="top-progress fixed top-4 left-1/2 -translate-x-1/2 z-50 w-[60%] pointer-events-none" v-show="progress > 0">
    <div class="top-progress__bar" :style="{ width: progress + '%' }"></div>
  </div>
  <!-- 全屏背景图 +适度模糊遮罩 -->
  <div class="fixed inset-0 w-full h-full z-0">
    <img v-if="article.coverImg" :src="article.coverImg" alt="背景图"
      class="w-full h-full object-cover"
      style="filter: blur(12px) brightness(1.08) contrast(1.05);" />
    <div v-else class="w-full h-full bg-[#F7F9FE] dark:bg-[#121212]" style="filter: blur(12px) brightness(1.08) contrast(1.05);"></div>
    <!-- 半透明遮罩，增强层次 -->
    <div class="absolute inset-0 bg-white/60 dark:bg-[#18181c]/60"></div>
  </div>

  <!-- 内容区域加遮罩和居中 -->
  <div class="relative z-10 min-h-screen flex flex-col items-center justify-center">
    <!-- 加载中状态 (骨架屏) -->
    <div v-if="loading" class="max-w-4xl mx-auto px-4 py-10 mt-22 animate-pulse bg-white/80 dark:bg-[#18181c]/80 rounded-2xl shadow-lg">
      <div class="h-8 bg-slate-200 dark:bg-slate-800 rounded w-1/4 mb-6"></div>
      <div class="h-64 bg-slate-200 dark:bg-slate-800 rounded-2xl mb-8"></div>
      <div class="space-y-3">
        <div class="h-4 bg-slate-200 dark:bg-slate-800 rounded w-full"></div>
        <div class="h-4 bg-slate-200 dark:bg-slate-800 rounded w-5/6"></div>
      </div>
    </div>

    <!-- 文章主体内容 -->
    <div v-else class="max-w-5xl mx-auto px-4 md:px-6 py-8 mt-22 bg-white/90 dark:bg-[#18181c]/90 rounded-2xl shadow-lg">
      <!-- 1. 头部信息区域 -->
      <header class="mb-8 text-center md:text-left">
        <!-- 分类与标签 -->
        <div v-if="article.isTop === 1 || article.isOriginal !== undefined || article.category" class="flex flex-wrap items-center justify-center md:justify-start gap-3 mb-4">
          <span 
            v-if="article.isTop === 1"
            class="px-2 py-1 text-xs font-bold text-white bg-red-500 rounded-md shadow-sm transform -skew-x-12"
          >
            置顶
          </span>
          <span 
            v-if="article.isOriginal !== undefined"
            class="px-3 py-1 text-xs font-medium rounded-full"
            :class="article.isOriginal === 1 
              ? 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400' 
              : 'bg-gray-100 text-gray-600 dark:bg-gray-800 dark:text-gray-400'"
          >
            {{ article.isOriginal === 1 ? '原创' : '转载' }}
          </span>
          <span v-if="article.category" class="text-indigo-600 dark:text-indigo-400 font-medium text-sm">
            # {{ article.category }}
          </span>
        </div>

        <!-- 标题 -->
        <h1 v-if="article.title" class="text-3xl md:text-5xl font-extrabold text-slate-900 dark:text-slate-100 leading-tight mb-6">
          {{ article.title }}
        </h1>

        <!-- 元数据 -->
        <div v-if="article.author || article.publishTime || article.viewCount || article.content" class="flex flex-wrap items-center justify-center md:justify-start gap-4 text-sm text-slate-500 dark:text-slate-400">
          <div v-if="article.author" class="flex items-center gap-2">
            <div class="w-6 h-6 rounded-full bg-indigo-500 flex items-center justify-center text-white text-xs">
              云
            </div>
            <span class="font-medium text-slate-700 dark:text-slate-300">{{ article.author }}</span>
          </div>
          <span v-if="article.author && (article.publishTime || article.viewCount || article.content)" class="hidden md:inline">·</span>
          <time v-if="article.publishTime">{{ formatDate(article.publishTime) }}</time>
          <span v-if="article.publishTime && (article.viewCount || article.content)" class="hidden md:inline">·</span>
          <div v-if="article.viewCount || article.content" class="flex items-center gap-4">
            <span v-if="article.viewCount" class="flex items-center gap-1">
              👁️ {{ formatNumber(article.viewCount) }} 阅读
            </span>
            <span v-if="article.content" class="flex items-center gap-1">
              📝 {{ formatNumber((article.content || '').length) }} 字
            </span>
          </div>
        </div>
      </header>

      <!-- 2. 封面图（重点优化 hover 动画） -->
      <div 
        v-if="article.coverImg"
        class="relative w-full aspect-video md:aspect-[21/9] rounded-3xl overflow-hidden shadow-2xl mb-10 group cursor-pointer"
      >
        <img 
          :src="article.coverImg" 
          :alt="article.title || '文章封面'"
          class="w-full h-full object-cover 
                transition-all 
                duration-[1400ms] 
                delay-150 
                ease-[cubic-bezier(0.34,0.00,0.60,1.00)] 
                scale-[1.00] 
                group-hover:scale-[1.10] 
                group-hover:brightness-105
                will-change-transform"
        />
        <div class="absolute inset-0 bg-gradient-to-t from-black/30 to-transparent pointer-events-none"></div>
      </div>


      <div class="grid grid-cols-1 lg:grid-cols-12 gap-20 relative">
        <!-- 右侧目录栏 (Desktop) -->
        <ArticleTocSidebar
          v-if="!loading"
          :html="renderedContent"
          :visible="sidebarVisible"
          class="hidden lg:block fixed right-8 top-[196px] z-30 w-64 max-h-[70vh] bg-white/90 dark:bg-[#18181c]/90 rounded-2xl shadow border border-slate-100 dark:border-slate-800 p-4 overflow-y-auto"
        />
        <!-- 左侧悬浮操作栏 (Desktop) -->
        <aside 
          v-if="!loading"
          :style="sidebarVisible ? 'transform: translateX(0); opacity: 1;' : 'transform: translateX(-120%); opacity: 0;'"
          class="hidden lg:flex flex-col items-center gap-3 fixed left-[100px] top-[196px] z-30 w-20 py-6 bg-white/80 dark:bg-[#18181c]/80 rounded-3xl shadow border border-slate-100 dark:border-slate-800 transition-all duration-500"
        >
          <AnimatedActionButton
            :checked="article.isLike"
            icon="heart"
            id="like-checkbox"
            title="点赞"
            style="width:32px;height:32px;"
            @update:checked="handleLike"
          />
          <span class="text-xs text-slate-500 font-medium -mt-1">{{ formatNumber(article.likeCount) }}</span>

          <AnimatedActionButton
            :checked="article.isCollect"
            icon="star"
            id="collect-checkbox"
            title="收藏"
            style="width:32px;height:32px;"
            @update:checked="handleCollect"
          />
          <span class="text-xs text-slate-500 font-medium -mt-1">{{ formatNumber(article.collectCount) }}</span>

          <AnimatedActionButton
            :checked="false"
            icon="comment"
            id="comment-checkbox"
            title="评论"
            style="width:32px;height:32px;color:#06b6d4;"
            class="text-cyan-500"
            @change="scrollToComments"
          />
          <span class="text-xs text-slate-500 font-medium -mt-1">{{ formatNumber(article.commentCount) }}</span>
        </aside>


        <!-- 3. 文章正文区域 -->
        <div class="col-span-1 lg:col-span-12 bg-white dark:bg-[#1E1E1E] rounded-3xl p-6 md:p-10 shadow-sm border border-slate-100 dark:border-slate-800">
          <article 
            class="prose prose-lg prose-slate dark:prose-invert max-w-none
                   prose-a:text-indigo-600 dark:prose-a:text-indigo-400 prose-a:no-underline hover:prose-a:underline
                   prose-img:rounded-xl prose-img:shadow-lg"
            v-html="renderedContent"
          ></article>

          <!-- 底部信息 -->
          <div class="mt-12 pt-6 border-t border-slate-100 dark:border-slate-700">
            <div v-if="article.tags && article.tags.length" class="flex flex-wrap gap-2 mb-4">
              <span 
                v-for="tag in article.tags" 
                :key="tag"
                class="px-3 py-1 bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300 text-sm rounded-md transition hover:bg-indigo-50 hover:text-indigo-600 dark:hover:bg-slate-700"
              >
                #{{ tag }}
              </span>
            </div>
            <p v-if="article.updateTime || article.author" class="text-sm text-slate-400 italic">
              <span v-if="article.updateTime">最后更新于 {{ formatDate(article.updateTime) }}</span>
              <span v-if="article.updateTime && article.author"> · </span>
              <span v-if="article.author">本文由 {{ article.author }} 发布</span>
            </p>
          </div>
        </div>
      </div>

      <!-- 评论区（置于文章主体下方） -->
      <div class="max-w-5xl mx-auto px-4 md:px-6 py-6 mt-6">
        <CommentSection :articleId="article.id" />
      </div>

      <!-- 移动端底部操作栏 -->
      <div class="lg:hidden fixed bottom-6 left-1/2 -translate-x-1/2 bg-white/90 dark:bg-slate-800/90 backdrop-blur-md px-6 py-3 rounded-full shadow-2xl border border-slate-100 dark:border-slate-700 flex items-center gap-8 z-50">
        <div class="flex flex-col items-center gap-1 cursor-pointer" @click="handleLike">
          <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6" :class="{ 'text-red-500 fill-current': article.isLike, 'text-slate-500': !article.isLike }" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
          </svg>
          <span class="text-[10px] text-slate-500">{{ formatNumber(article.likeCount) }}</span>
        </div>
        <div class="flex flex-col items-center gap-1 cursor-pointer" @click="handleCollect">
          <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6" :class="{ 'text-yellow-500 fill-current': article.isCollect, 'text-slate-500': !article.isCollect }" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z"/>
          </svg>
          <span class="text-[10px] text-slate-500">{{ formatNumber(article.collectCount) }}</span>
        </div>
        <div class="flex flex-col items-center gap-1 cursor-pointer" @click="scrollToComments">
          <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6 text-slate-500" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" viewBox="0 0 24 24">
            <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
          </svg>
          <span class="text-[10px] text-slate-500">{{ formatNumber(article.commentCount) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="ArticleDetailView">
import AnimatedActionButton from '@/components/common/AnimatedActionButton.vue'
import ArticleTocSidebar from '@/components/common/ArticleTocSidebar.vue'
import CommentSection from '@/components/common/CommentSection.vue'
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { useNotification } from '@/composables/useNotification'
import { useRoute, useRouter } from 'vue-router'
import MarkdownIt from 'markdown-it'
import http from '@/api/http'

// Markdown 渲染器
const md = new MarkdownIt({ html: true, linkify: true, breaks: true })

// 与后端 ArticleFrontVO 完全一致的 TypeScript 接口
interface ArticleFrontVO {
  id: string
  mongoId?: string
  title: string
  content: string
  coverImg?: string
  keywords?: string
  category: string
  tags: string[]
  isOriginal: number
  isTop: number
  likeCount: number
  commentCount: number
  collectCount: number
  viewCount: number
  isLike: boolean
  isCollect: boolean
  publishTime: string
  updateTime: string
  author?: string
}

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const progress = ref(0)
let progressTimer: number | null = null

const startProgress = () => {
  progress.value = 6
  if (progressTimer) clearInterval(progressTimer)
  progressTimer = window.setInterval(() => {
    // 增长到 90% 以内，随机步进以显得自然
    if (progress.value < 90) {
      progress.value = Math.min(90, progress.value + Math.random() * 8)
    }
  }, 300)
}

const finishProgress = async () => {
  progress.value = 100
  if (progressTimer) {
    clearInterval(progressTimer)
    progressTimer = null
  }
  // 给出一点时间让用户看到完成动画
  await new Promise((r) => setTimeout(r, 220))
  progress.value = 0
}

onUnmounted(() => {
  if (progressTimer) {
    clearInterval(progressTimer)
    progressTimer = null
  }
})
const articleId = computed(() => route.params.id?.toString() ?? '')

const article = ref<ArticleFrontVO>({
  id: '',
  title: '',
  content: '',
  category: '',
  tags: [],
  isOriginal: 1,
  isTop: 0,
  likeCount: 0,
  commentCount: 0,
  collectCount: 0,
  viewCount: 0,
  isLike: false,
  isCollect: false,
  publishTime: '',
  updateTime: '',
  author: '云坛'
})

// Markdown 渲染结果
const renderedContent = computed(() => {
  return article.value.content ? md.render(article.value.content) : ''
})

// 真实 API 请求
const fetchArticleDetail = async (id: string) => {
  loading.value = true
  startProgress()
  try {
    const res = await http.get(`/front/articles/${id}`)
    const data = res.data?.data || {}

    // 兼容：后端可能返回的是未包含当前用户的基础数量（如 likeCount），
    // 如果后端同时返回 isLike/isCollect 为真，前端展示上按要求向上加 1
    const serverLikeCount = typeof data.likeCount === 'number' ? data.likeCount : 0
    const serverCollectCount = typeof data.collectCount === 'number' ? data.collectCount : 0
    const serverIsLike = !!data.isLike
    const serverIsCollect = !!data.isCollect

    article.value = {
      id: (data.id ?? id).toString(),
      mongoId: data.mongoId || '',
      title: data.title || '未命名文章',
      content: data.content || '',
      coverImg: data.coverImg || '',
      keywords: data.keywords || '',
      category: data.category || '未分类',
      tags: Array.isArray(data.tags) ? data.tags : [],
      isOriginal: typeof data.isOriginal === 'number' ? data.isOriginal : 1,
      isTop: typeof data.isTop === 'number' ? data.isTop : 0,
      likeCount: serverIsLike ? serverLikeCount + 1 : serverLikeCount,
      commentCount: typeof data.commentCount === 'number' ? data.commentCount : 0,
      collectCount: serverIsCollect ? serverCollectCount + 1 : serverCollectCount,
      viewCount: typeof data.viewCount === 'number' ? data.viewCount : 0,
      isLike: serverIsLike,
      isCollect: serverIsCollect,
      publishTime: data.publishTime || '',
      updateTime: data.updateTime || '',
      author: data.author || '云坛'
    }
  } catch (error) {
    console.error('获取文章详情失败', error)
    article.value = {
      id,
      title: '文章加载失败，请稍后重试',
      content: '',
      category: '系统',
      tags: [],
      isOriginal: 1,
      isTop: 0,
      likeCount: 0,
      commentCount: 0,
      collectCount: 0,
      viewCount: 0,
      isLike: false,
      isCollect: false,
      publishTime: '',
      updateTime: '',
      author: '云坛'
    }
  } finally {
    await finishProgress()
    loading.value = false
  }
}

// 工具函数
const formatDate = (dateStr?: string): string => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', { 
    year: 'numeric', 
    month: 'long', 
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatNumber = (num: number): string => {
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
  if (num >= 1000) return (num / 1000).toFixed(1) + 'k'
  return num.toString()
}

// 交互事件
// handleLike 接受可选的目标 checked 值（由子组件 emit 传入）
const handleLike = async (checked?: boolean) => {
  const userStore = useUserStore()
  const { warning, error } = useNotification()
  const token = (userStore && userStore.token) || localStorage.getItem('auth_token') || ''

  if (!token) {
    warning('请先登录', '请先登录后再进行点赞')
    return
  }

  const prev = article.value.isLike
  const newState = typeof checked === 'boolean' ? checked : !prev

  // 乐观更新
  article.value.isLike = newState
  article.value.likeCount += newState ? 1 : -1

  try {
    await http.post(`/front/interacts/like/${article.value.id}`)
  } catch (e: any) {
    // 回滚
    article.value.likeCount += article.value.isLike ? -1 : 1
    article.value.isLike = prev

    if (e?.response?.status === 401) {
      try { userStore.logout() } catch (_) {}
      warning('登录已过期', '请重新登录后再试')
    } else {
      error('操作失败', '点赞操作失败，请重试')
    }
  }
}

// handleCollect 接收可选 checked 参数，保持与 handleLike 一致的交互逻辑
const handleCollect = async (checked?: boolean) => {
  const userStore = useUserStore()
  const { warning, error } = useNotification()
  const token = (userStore && userStore.token) || localStorage.getItem('auth_token') || ''

  if (!token) {
    warning('请先登录', '请先登录后再进行收藏')
    return
  }

  const prev = article.value.isCollect
  const newState = typeof checked === 'boolean' ? checked : !prev

  // 乐观更新
  article.value.isCollect = newState
  article.value.collectCount += newState ? 1 : -1

  try {
    await http.post(`/front/interacts/collect/${article.value.id}`)
  } catch (e: any) {
    // 回滚
    article.value.collectCount += article.value.isCollect ? -1 : 1
    article.value.isCollect = prev

    if (e?.response?.status === 401) {
      try { userStore.logout() } catch (_) {}
      warning('登录已过期', '请重新登录后再试')
    } else {
      error('操作失败', '收藏操作失败，请重试')
    }
  }
}

const scrollToComments = () => {
  const commentsSection = document.getElementById('comments')
  commentsSection?.scrollIntoView({ behavior: 'smooth' })
}

const goBack = () => router.back()

// 页面加载
const sidebarVisible = ref(false)

onMounted(() => {
  if (articleId.value) {
    fetchArticleDetail(articleId.value)
  }
  // 监听滚动，控制目录侧边栏出现/消失
  const sidebarTrigger = () => {
    const article = document.querySelector('article')
    if (!article) return
    const rect = article.getBoundingClientRect()
    sidebarVisible.value = rect.top < 200 && rect.bottom > 400
  }
  window.addEventListener('scroll', sidebarTrigger)
  sidebarTrigger()
})
</script>

<style scoped>
/* 顶部进度条样式 */
.top-progress { height: 10px; display: flex; align-items: center; }
.top-progress__bar {
  height: 6px;
  width: 0%;
  background: linear-gradient(90deg, rgba(99,102,241,0.95), rgba(34,197,94,0.95));
  border-radius: 999px;
  box-shadow: 0 3px 10px rgba(2,6,23,0.12);
  transition: width 260ms cubic-bezier(.2,.8,.2,1);
}

/* Markdown 基础样式（保留排版微调，颜色交给 Tailwind 的 prose / dark:prose-invert） */
:deep(.prose h1) { font-size: 2.25em; margin-top: 2em; margin-bottom: 0.75em; font-weight: 700; }
:deep(.prose h2) { font-size: 1.75em; margin-top: 1.75em; margin-bottom: 0.75em; font-weight: 600; }
:deep(.prose) { line-height: 1.85; }

/* 代码块和引用的微调由组件控制（不覆盖基础颜色） */
:deep(.prose pre) { 
  background-color: #f8fafc; 
  padding: 1.25em; 
  border-radius: 0.75em; 
  overflow-x: auto; 
  border: 1px solid #e2e8f0;
}
:global(.dark) :deep(.prose pre) { 
  background-color: #0f1724; 
  color: #e2e8f0; 
  border-color: #273449;
}
:deep(.prose blockquote) { 
  border-left: 4px solid #e2e8f0; 
  padding-left: 1.25em; 
  font-style: italic; 
  color: #64748b; 
}
:global(.dark) :deep(.prose blockquote) {
  border-left-color: #6366f1;
  color: #94a3b8;
}
:deep(.prose ul) { list-style-type: disc; padding-left: 1.5em; margin-bottom: 1.25em; }

/* 封面图优化后的丝滑放大动画（GPU hint） */
.group img { will-change: transform; }
</style>