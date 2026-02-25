<template>
  <!-- 顶部加载进度条 -->
  <div class="top-progress fixed top-0 left-0 right-0 z-50 pointer-events-none" v-show="progress > 0">
    <div class="top-progress__bar h-[3px] bg-gradient-to-r from-indigo-500 to-emerald-400" :style="{ width: progress + '%' }"></div>
  </div>

  <!-- 全屏背景图 + 适度模糊遮罩 -->
  <div class="fixed inset-0 w-full h-full z-0">
    <img v-if="article.coverImg" :src="article.coverImg" alt="背景图"
      class="w-full h-full object-cover transition-opacity duration-700"
      style="filter: blur(16px) brightness(0.95) saturate(1.1);" />
    <div v-else class="w-full h-full bg-[#F8FAFC] dark:bg-[#0f172a]"></div>
    <div class="absolute inset-0 bg-white/70 dark:bg-[#0f172a]/70 backdrop-blur-sm"></div>
  </div>

  <!-- 内容区域 -->
  <div class="relative z-10 min-h-screen flex flex-col pt-20 pb-12">
    
    <!-- 骨架屏 -->
    <div v-if="loading" class="max-w-[1000px] mx-auto w-full px-4 animate-pulse mt-10">
      <div class="h-10 bg-slate-200 dark:bg-slate-700 rounded w-2/3 mb-6"></div>
      <div class="h-64 bg-slate-200 dark:bg-slate-700 rounded-2xl mb-8"></div>
    </div>

    <!-- 核心布局容器 -->
    <div v-else class="w-full max-w-[1000px] mx-auto px-4 md:px-6 relative group">
      
      <!-- 左侧悬浮操作栏 -->
      <aside 
        class="hidden xl:flex flex-col items-end gap-4 fixed top-[260px] z-20 transition-all duration-500 ease-out"
        :style="{ 
          left: 'calc(50% - 580px)', 
          opacity: sidebarVisible ? 1 : 0, 
          transform: sidebarVisible ? 'translateX(0)' : 'translateX(-40px)',
          pointerEvents: sidebarVisible ? 'auto' : 'none'
        }"
      >
        <div class="flex flex-col items-center gap-3 bg-white/80 dark:bg-[#1e293b]/80 backdrop-blur p-3 rounded-full shadow-lg border border-slate-100 dark:border-slate-700">
          <AnimatedActionButton :checked="article.isLike" icon="heart" id="like-btn" title="点赞" style="width:28px;height:28px;" @update:checked="handleLike" />
          <span class="text-xs text-slate-500 font-medium -mt-1">{{ formatNumber(article.likeCount) }}</span>

          <AnimatedActionButton :checked="article.isCollect" icon="star" id="collect-btn" title="收藏" style="width:28px;height:28px;" @update:checked="handleCollect" />
          <span class="text-xs text-slate-500 font-medium -mt-1">{{ formatNumber(article.collectCount) }}</span>

          <AnimatedActionButton :checked="false" icon="comment" id="comment-btn" title="评论" style="width:28px;height:28px;" class="text-cyan-500" @change="scrollToComments" />
          <span class="text-xs text-slate-500 font-medium -mt-1">{{ formatNumber(article.commentCount) }}</span>
        </div>
      </aside>

      <!-- 右侧目录栏 -->
      <div 
        class="hidden xl:block fixed top-[0px] z-20 transition-all duration-500 ease-out"
        :style="{ 
          left: 'calc(50% + 500px)', 
          width: '260px', 
          opacity: sidebarVisible ? 1 : 0,
          transform: sidebarVisible ? 'translateX(0)' : 'translateX(40px)',
          pointerEvents: sidebarVisible ? 'auto' : 'none'
        }"
      >
        <ArticleTocSidebar
          v-if="!loading"
          :html="renderedContent"
          :visible="true"
          class="max-h-[70vh] w-full bg-white/90 dark:bg-[#1e293b]/90 rounded-2xl shadow border border-slate-100 dark:border-slate-800 p-4 overflow-y-auto" style="top: 180px;"
        />
      </div>

      <!-- ==================== 核心宽对齐区域 ==================== -->
      
      <!-- 1. Header -->
      <header class="mb-10 text-center md:text-left bg-white/40 dark:bg-[#1e293b]/40 backdrop-blur-md rounded-3xl p-8 border border-white/20 dark:border-gray-700/30 w-full">
        <div class="flex flex-wrap items-center justify-center md:justify-start gap-3 mb-5">
          <span v-if="article.isTop === 1" class="px-2 py-0.5 text-xs font-bold text-white bg-rose-500 rounded shadow-sm transform -skew-x-12">TOP</span>
          <span class="px-3 py-0.5 text-xs font-medium rounded-full border border-indigo-100 dark:border-indigo-900 bg-indigo-50 dark:bg-indigo-900/30 text-indigo-600 dark:text-indigo-300">
            {{ article.category || '未分类' }}
          </span>
          <span v-for="tag in article.tags" :key="tag" class="text-slate-500 dark:text-slate-400 text-sm">#{{ tag }}</span>
        </div>
        <h1 class="text-3xl md:text-5xl font-extrabold text-slate-900 dark:text-white leading-tight mb-6 tracking-tight">
          {{ article.title }}
        </h1>
        <div class="flex flex-wrap items-center justify-center md:justify-start gap-4 text-sm text-slate-500 dark:text-slate-400 font-medium">
          <div class="flex items-center gap-2">
            <div class="w-6 h-6 rounded-full bg-indigo-500 flex items-center justify-center text-white text-xs font-bold">
              {{ article.author ? article.author.charAt(0) : '云' }}
            </div>
            <span>{{ article.author }}</span>
          </div>
          <span class="hidden md:inline">·</span>
          <time>{{ formatDate(article.publishTime) }}</time>
          <span class="hidden md:inline">·</span>
          <span>{{ formatNumber(article.viewCount) }} 阅读</span>
        </div>
      </header>

      <!-- 2. 封面图 -->
      <div v-if="article.coverImg" class="relative w-full aspect-[21/9] rounded-2xl overflow-hidden shadow-2xl mb-12 group">
        <img :src="article.coverImg" class="w-full h-full object-cover transition-transform duration-700 group-hover:scale-105" />
        <div class="absolute inset-0 bg-gradient-to-t from-black/20 to-transparent pointer-events-none"></div>
      </div>

      <!-- 3. 文章正文 (修复渲染问题) -->
      <article class="w-full bg-white dark:bg-[#1e293b] rounded-3xl p-6 md:p-12 shadow-xl border border-gray-100 dark:border-gray-700/50 min-h-[500px]">
        <!-- 
          核心修复：
          1.添加 'markdown-body' 类以应用强制样式 
          2.绑定处理后的 renderedContent
        -->
        <div 
          id="article-content"
          class="prose prose-lg md:prose-xl dark:prose-invert max-w-none text-left break-words markdown-body"
          v-html="renderedContent"
          @click="handleContentClick"
        ></div>

        <div class="mt-16 pt-8 border-t border-gray-100 dark:border-gray-800">
          <p class="text-sm text-gray-400 text-center italic">
            本文最后更新于 {{ formatDate(article.updateTime) }} · 许可协议 CC BY-NC-SA 4.0
          </p>
        </div>
      </article>

      <!-- 评论区 -->
      <div id="comments" class="mt-12 w-full">
        <CommentSection :articleId="article.id" />
      </div>

    </div>

    <!-- 移动端底部操作栏 -->
    <div class="xl:hidden fixed bottom-6 left-1/2 -translate-x-1/2 bg-white/90 dark:bg-slate-800/90 backdrop-blur-md px-6 py-3 rounded-full shadow-2xl border border-slate-100 dark:border-slate-700 flex items-center gap-8 z-50">
      <div class="flex flex-col items-center gap-1 cursor-pointer" @click="handleLike">
        <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6" :class="{ 'text-red-500 fill-current': article.isLike, 'text-slate-500': !article.isLike }" viewBox="0 0 24 24" fill="currentColor"><path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/></svg>
        <span class="text-[10px] text-slate-500">{{ formatNumber(article.likeCount) }}</span>
      </div>
      <div class="flex flex-col items-center gap-1 cursor-pointer" @click="handleCollect">
        <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6" :class="{ 'text-yellow-500 fill-current': article.isCollect, 'text-slate-500': !article.isCollect }" viewBox="0 0 24 24" fill="currentColor"><path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z"/></svg>
        <span class="text-[10px] text-slate-500">{{ formatNumber(article.collectCount) }}</span>
      </div>
      <div class="flex flex-col items-center gap-1 cursor-pointer" @click="scrollToComments">
        <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6 text-slate-500" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" viewBox="0 0 24 24"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
        <span class="text-[10px] text-slate-500">{{ formatNumber(article.commentCount) }}</span>
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
import { useRoute } from 'vue-router'
import http from '@/api/http'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import 'highlight.js/styles/atom-one-dark.css'

// ------------------------------------------------
// 1. Markdown 渲染配置
// ------------------------------------------------
const md = new MarkdownIt({
  html: true,
  linkify: true,
  typographer: true,
  breaks: true, // 核心配置：开启硬换行
  highlight: function (str, lang) {
    let highlightedCode = ''
    const language = lang && hljs.getLanguage(lang) ? lang : 'text'
    try {
      highlightedCode = hljs.highlight(str, { language, ignoreIllegals: true }).value
    } catch (__) {
      highlightedCode = md.utils.escapeHtml(str)
    }

    const lines = highlightedCode.split('\n')
    if (lines.length > 0 && lines[lines.length - 1] === '') lines.pop()
    const lineNumbersCode = lines.map((_, index) => `<span class="line-number">${index + 1}</span>`).join('\n')
    const contentCode = lines.map(line => `<span class="line-content">${line || ' '}</span>`).join('\n')

    return `<div class="mac-code-block group"><div class="mac-code-header"><div class="mac-buttons"><span class="mac-btn close"></span><span class="mac-btn minimize"></span><span class="mac-btn maximize"></span></div><div class="mac-lang">${language}</div><div class="mac-copy-btn" data-clipboard-text="${encodeURIComponent(str)}"><span class="copy-text">复制代码</span><svg class="copy-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect><path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path></svg></div></div><div class="mac-code-body"><div class="line-numbers-wrapper">${lineNumbersCode}</div><pre><code class="hljs language-${language}">${contentCode}</code></pre></div></div>`
  }
})

// ------------------------------------------------
// 2. 状态与逻辑
// ------------------------------------------------
const route = useRoute()
const articleId = computed(() => route.params.id?.toString() ?? '')
const loading = ref(true)
const progress = ref(0)
const sidebarVisible = ref(false)
let progressTimer: number | null = null

const article = ref({
  id: '', title: '', content: '', category: '', tags: [] as string[],
  isOriginal: 1, isTop: 0, likeCount: 0, commentCount: 0, collectCount: 0, viewCount: 0,
  isLike: false, isCollect: false, publishTime: '', updateTime: '', author: '云坛', coverImg: ''
})

// 计算属性：渲染 Markdown
const renderedContent = computed(() => {
  if (!article.value.content) return ''
  
  // 🔥 核心修复：对文本进行预处理
  // 如果后端返回的 Markdown 文本中，列表/标题前缺少空行，markdown-it 可能无法正确解析
  // 这里用正则强制在数字列表（1. xxx）和无序列表（- xxx）以及标题（# xxx）前加空行
  let content = article.value.content
  
  // 1. 列表修复：将紧挨着上文的列表项前强制加换行
  content = content.replace(/([^\n])\n(\d+\.\s)/g, '$1\n\n$2')
  content = content.replace(/([^\n])\n(-\s)/g, '$1\n\n$2')
  
  // 2. 标题修复：将紧挨着上文的标题前强制加换行
  content = content.replace(/([^\n])\n(#+\s)/g, '$1\n\n$2')

  return md.render(content)
})

// ------------------------------------------------
// 3. 辅助方法
// ------------------------------------------------
const startProgress = () => {
  progress.value = 5
  progressTimer = window.setInterval(() => {
    if (progress.value < 90) progress.value += Math.random() * 5
  }, 200)
}
const finishProgress = async () => {
  if (progressTimer) clearInterval(progressTimer)
  progress.value = 100
  setTimeout(() => progress.value = 0, 300)
}

const fetchArticleDetail = async (id: string) => {
  loading.value = true
  startProgress()
  try {
    const res = await http.get(`/front/articles/${id}`)
    const data = res.data?.data || {}
    article.value = {
      ...article.value,
      ...data,
      likeCount: Number(data.likeCount || 0),
      collectCount: Number(data.collectCount || 0),
      commentCount: Number(data.commentCount || 0),
      viewCount: Number(data.viewCount || 0),
      tags: Array.isArray(data.tags) ? data.tags : [],
    }
  } catch (err) { console.error(err) } finally { finishProgress(); loading.value = false }
}

const handleContentClick = async (e: MouseEvent) => {
  const target = (e.target as HTMLElement).closest('.mac-copy-btn') as HTMLElement
  if (target) {
    const code = decodeURIComponent(target.dataset.clipboardText || '')
    try {
      await navigator.clipboard.writeText(code)
      const textSpan = target.querySelector('.copy-text')
      if (textSpan) {
        const original = textSpan.textContent
        textSpan.textContent = '已复制'
        target.classList.add('text-green-400')
        setTimeout(() => { textSpan.textContent = original; target.classList.remove('text-green-400') }, 2000)
      }
    } catch (err) { console.error(err) }
  }
}

const checkLogin = () => {
  const userStore = useUserStore()
  const { warning } = useNotification()
  if (!userStore.token) { warning('请先登录', '登录后即可操作'); return false }
  return true
}

const handleLike = async () => {
  if (!checkLogin()) return
  const prevIsLike = article.value.isLike
  const nextIsLike = !prevIsLike
  const prevCount = Number(article.value.likeCount) || 0
  article.value.isLike = nextIsLike
  article.value.likeCount = Math.max(0, prevCount + (nextIsLike ? 1 : -1))
  try { await http.post(`/front/interacts/like/${article.value.id}`) } 
  catch { article.value.isLike = prevIsLike; article.value.likeCount = prevCount }
}

const handleCollect = async () => {
  if (!checkLogin()) return
  const prevIsCollect = article.value.isCollect
  const nextIsCollect = !prevIsCollect
  const prevCount = Number(article.value.collectCount) || 0
  article.value.isCollect = nextIsCollect
  article.value.collectCount = Math.max(0, prevCount + (nextIsCollect ? 1 : -1))
  try { await http.post(`/front/interacts/collect/${article.value.id}`) } 
  catch { article.value.isCollect = prevIsCollect; article.value.collectCount = prevCount }
}

const scrollToComments = () => document.getElementById('comments')?.scrollIntoView({ behavior: 'smooth' })
const formatDate = (s: string) => s ? new Date(s).toLocaleDateString() : ''
const formatNumber = (n: number) => { const num = Number(n); return num >= 1000 ? (num/1000).toFixed(1)+'k' : num.toString() }

onMounted(() => {
  if (articleId.value) fetchArticleDetail(articleId.value)
  window.addEventListener('scroll', () => {
    const art = document.querySelector('article')
    if (art) {
      const rect = art.getBoundingClientRect()
      if (rect.top < 300 && rect.bottom > 400) sidebarVisible.value = true
      else sidebarVisible.value = false
    }
  })
})
onUnmounted(() => { if (progressTimer) clearInterval(progressTimer) })
</script>

<!-- 🔥 核心修复：移除 scoped，或者使用 :deep() 强制覆盖 -->
<!-- 这里使用全局样式 (无 scoped) 来确保 v-html 内部的 Markdown 标签一定能被样式选中 -->
<style>
/* 
  markdown-body 样式：兜底方案 
  即使 Tailwind Typography 插件没生效，这些样式也能保证文章排版美观 
*/

/* 1. 标题 */
.markdown-body h1, .markdown-body h2, .markdown-body h3, .markdown-body h4 {
  margin-top: 1.5em;
  margin-bottom: 0.8em;
  font-weight: 700;
  line-height: 1.3;
  color: #1a202c; /* gray-900 */
}
.dark .markdown-body h1, .dark .markdown-body h2, .dark .markdown-body h3 { color: #f7fafc; }

.markdown-body h1 { font-size: 2.25em; border-bottom: 1px solid #e2e8f0; padding-bottom: 0.3em; margin-top: 0.5em; }
.markdown-body h2 { font-size: 1.75em; border-bottom: 1px solid #e2e8f0; padding-bottom: 0.3em; }
.markdown-body h3 { font-size: 1.5em; }
.dark .markdown-body h1, .dark .markdown-body h2 { border-bottom-color: #2d3748; }

/* 2. 段落 */
.markdown-body p {
  margin-bottom: 1.25em;
  line-height: 1.75;
  color: #374151; /* gray-700 */
  text-align: justify;
}
.dark .markdown-body p { color: #d1d5db; }

/* 3. 列表 (核心修复) */
.markdown-body ul, .markdown-body ol {
  margin-bottom: 1.25em;
  padding-left: 1.6em; /* 必须有缩进 */
  color: #374151;
}
.dark .markdown-body ul, .dark .markdown-body ol { color: #d1d5db; }

.markdown-body ul { list-style-type: disc; } /* 实心圆点 */
.markdown-body ol { list-style-type: decimal; } /* 数字 */

.markdown-body li {
  margin-bottom: 0.5em;
  padding-left: 0.2em;
}
.markdown-body li > p { margin-bottom: 0.5em; } /* 列表里的段落间距减小 */

/* 4. 引用 Blockquote */
.markdown-body blockquote {
  border-left: 4px solid #6366f1; /* indigo-500 */
  padding: 0.5em 1em;
  margin: 1.5em 0;
  color: #4b5563;
  background: #f3f4f6; /* gray-100 */
  border-radius: 0 4px 4px 0;
  font-style: italic;
}
.dark .markdown-body blockquote {
  background: #1f2937;
  color: #9ca3af;
  border-left-color: #818cf8;
}

/* 5. 链接 */
.markdown-body a {
  color: #4f46e5;
  text-decoration: none;
  font-weight: 500;
  border-bottom: 1px dashed #4f46e5;
  transition: all 0.2s;
}
.markdown-body a:hover {
  color: #4338ca;
  border-bottom-style: solid;
}
.dark .markdown-body a { color: #818cf8; border-bottom-color: #818cf8; }

/* 6. 强调/加粗 */
.markdown-body strong {
  font-weight: 700;
  color: #111827; /* gray-900 */
}
.dark .markdown-body strong { color: #f3f4f6; }

/* 7. 图片 */
.markdown-body img {
  border-radius: 0.75rem;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  margin: 2rem auto;
  max-width: 100%;
}

/* 8. 行内代码 */
.markdown-body :not(pre) > code {
  background-color: #f3f4f6;
  color: #ef4444; /* red-500 */
  padding: 0.2em 0.4em;
  border-radius: 0.25rem;
  font-family: 'JetBrains Mono', monospace;
  font-size: 0.9em;
}
.dark .markdown-body :not(pre) > code {
  background-color: #374151;
  color: #fca5a5;
}

/* macOS 代码块样式 (保持不变) */
.mac-code-block { background: #282c34; border-radius: 10px; margin: 1em 0; box-shadow: 0 8px 24px -8px rgba(0,0,0,0.45); overflow: hidden; font-family: 'JetBrains Mono', 'Consolas', monospace; border: 1px solid rgba(0,0,0,0.25); position: relative; }
.mac-code-header { background: #21252b; padding: 6px 12px; display: flex; align-items: center; justify-content: space-between; border-bottom: 1px solid #181a1f; height: 36px; user-select: none; }
.mac-buttons { display: flex; gap: 8px; }
.mac-btn { width: 12px; height: 12px; border-radius: 50%; display: inline-block; }
.mac-btn.close { background-color: #ff5f56; }
.mac-btn.minimize { background-color: #ffbd2e; }
.mac-btn.maximize { background-color: #27c93f; }
.mac-lang { color: #abb2bf; font-size: 13px; font-weight: 600; text-transform: uppercase; letter-spacing: 0.05em; opacity: 0.6; }
.mac-copy-btn { display: flex; align-items: center; gap: 6px; color: #abb2bf; font-size: 12px; cursor: pointer; opacity: 0.6; transition: all 0.2s; }
.mac-copy-btn:hover { opacity: 1; color: #fff; }
.mac-copy-btn .copy-icon { width: 14px; height: 14px; }
.mac-code-body { padding: 6px 0; display: flex; overflow-x: auto; font-size: 13px; line-height: 1.05; }
.line-numbers-wrapper { padding: 0 6px; text-align: right; border-right: 1px solid #3b4048; margin-right: 6px; flex-shrink: 0; user-select: none; min-width: 30px; }
.line-number { display: block; color: #495162; }
.mac-code-body pre { margin: 0 !important; padding: 0 !important; background: transparent !important; overflow: visible !important; flex-grow: 1; padding-right: 8px !important; }
.mac-code-body code { font-family: inherit; color: #abb2bf; display: block; white-space: pre; }
.line-content { display: block; min-height: 1.0em; }

/* 覆盖 Tailwind Typography 的干扰 */
.prose pre { background-color: transparent !important; padding: 0 !important; margin: 0 !important; border-radius: 0 !important; border: none !important; }
.prose :where(code):not(:where([class~="not-prose"] *))::before, .prose :where(code):not(:where([class~="not-prose"] *))::after { content: "" !important; }
.top-progress__bar { transition: width 0.2s ease-out; box-shadow: 0 0 10px rgba(99,102,241,0.5); }
</style>