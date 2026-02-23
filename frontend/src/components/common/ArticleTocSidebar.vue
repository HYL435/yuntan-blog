<template>
  <aside
    class="article-toc-sidebar hidden lg:block fixed right-1 top-35 z-30 w-72 h-fit max-h-[calc(100vh-160px)] bg-white/95 dark:bg-[#18181c]/95 backdrop-blur-2xl rounded-3xl shadow-xl border border-slate-100 dark:border-slate-700 overflow-hidden transition-all duration-500"
    :style="props.visible ? 'transform: translateX(0); opacity: 1;' : 'transform: translateX(120%); opacity: 0;'"
  >
    <div class="px-6 py-5 border-b border-slate-100 dark:border-slate-700 flex items-center gap-3">
      <div class="w-2 h-5 bg-gradient-to-b from-indigo-500 to-violet-500 rounded-full"></div>
      <span class="font-semibold text-lg tracking-tight text-slate-800 dark:text-white">目录</span>
    </div>

    <!-- 目录内容区域：隐藏滚动条但可滚动 -->
    <nav class="p-4 overflow-y-auto" style="scrollbar-width: none; -ms-overflow-style: none;">
      <ul class="space-y-1">
        <li v-for="item in toc" :key="item.id">
          
          <!-- 1. 一级标题 (h2)：始终显示 -->
          <a
            v-if="item.level === 2"
            :href="'#' + item.id"
            @click.prevent="scrollTo(item.id)"
            class="flex items-center w-full py-[10px] px-4 rounded-xl text-[14px] font-medium transition-all hover:bg-slate-100 dark:hover:bg-slate-800 text-left"
            :class="isActive(item.id) || isParentOfActive(item.id) 
              ? 'text-indigo-600 dark:text-indigo-400 bg-indigo-50/70 dark:bg-indigo-950/40' 
              : 'text-slate-600 dark:text-slate-400 opacity-80 hover:opacity-100'"
          >
            <!-- 强制 text-left 和 w-full 确保左对齐 -->
            <span class="truncate w-full text-left">{{ item.text }}</span>
          </a>

          <!-- 2. 子级标题 (h3+)：仅当父级是当前活跃章节时才显示 -->
          <div
            v-else-if="isChildOfActiveSection(item)"
            class="overflow-hidden transition-all duration-300 origin-top"
          >
            <a
              :href="'#' + item.id"
              @click.prevent="scrollTo(item.id)"
              class="flex items-center w-full py-1.5 pr-2 rounded-lg text-xs transition-all hover:text-indigo-500 text-left"
              :class="isActive(item.id) ? 'text-indigo-600 dark:text-indigo-400 font-medium' : 'text-slate-400 dark:text-slate-500'"
              :style="{ paddingLeft: `${(item.level - 1) * 0.8 + 1}rem` }"
            >
              <!-- 强制 text-left 和 w-full 确保左对齐 -->
              <div class="truncate w-full text-left border-l-2 pl-3 transition-colors" 
                   :class="isActive(item.id) ? 'border-indigo-500' : 'border-slate-200 dark:border-slate-700'">
                {{ item.text }}
              </div>
            </a>
          </div>

        </li>
      </ul>

      <div v-if="toc.length === 0" class="py-8 text-center text-slate-400 dark:text-slate-500 text-xs">
        暂无目录
      </div>
    </nav>
  </aside>

  <!-- 全局样式：强制隐藏滚动条 -->
  <component is="style">
    nav::-webkit-scrollbar { display: none; }
  </component>
</template>

<script setup lang="ts" name="ArticleTocSidebar">
import { ref, computed, watch, nextTick, onUnmounted } from 'vue'

interface TocItem {
  id: string
  text: string
  level: number
}

const props = defineProps<{ html: string, visible?: boolean }>()
const toc = ref<TocItem[]>([])
const activeId = ref<string>('')
let observer: IntersectionObserver | null = null

// 初始化目录（抓取真实 DOM）
function initToc() {
  const headings = document.querySelectorAll('article h2, article h3, article h4')
  const result: TocItem[] = []
  headings.forEach((el, index) => {
    if (!el.id) el.id = `heading-${index}-${(el.textContent || '').trim().replace(/\s+/g, '-')}`
    result.push({
      id: el.id,
      text: el.textContent?.trim() || '',
      level: Number(el.tagName[1])
    })
  })
  toc.value = result
  setupObserver()
}

// 监听滚动
function setupObserver() {
  if (observer) observer.disconnect()
  observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) activeId.value = entry.target.id
    })
  }, { rootMargin: '-100px 0px -60% 0px' })
  document.querySelectorAll('article h2, article h3, article h4').forEach(el => observer?.observe(el))
}

// 获取当前活跃的一级章节 ID
const currentActiveLevel1Id = computed(() => {
  if (!activeId.value) return toc.value[0]?.id || '' // 默认展开第一个
  
  const activeItem = toc.value.find(i => i.id === activeId.value)
  if (!activeItem) return ''
  
  // 如果当前是 h2，直接返回
  if (activeItem.level === 2) return activeItem.id
  
  // 如果是 h3+，向上找最近的 h2
  const index = toc.value.findIndex(i => i.id === activeId.value)
  for (let i = index; i >= 0; i--) {
    if (toc.value[i].level === 2) return toc.value[i].id
  }
  return ''
})

// 判断是否是当前活跃章节的子级（关键逻辑）
const isChildOfActiveSection = (item: TocItem) => {
  if (item.level === 2) return false // h2 自己已处理
  
  const activeH2Id = currentActiveLevel1Id.value
  if (!activeH2Id) return false

  const h2Index = toc.value.findIndex(i => i.id === activeH2Id)
  const itemIndex = toc.value.findIndex(i => i.id === item.id)

  // 必须在当前 h2 之后
  if (itemIndex <= h2Index) return false

  // 必须在下一个 h2 之前
  for (let i = h2Index + 1; i < toc.value.length; i++) {
    if (toc.value[i].level === 2) {
      return itemIndex < i
    }
  }
  return true // 后面没有 h2 了，那就是它的子级
}

const isActive = (id: string) => activeId.value === id
const isParentOfActive = (id: string) => id === currentActiveLevel1Id.value

function scrollTo(id: string) {
  const el = document.getElementById(id)
  if (el) {
    const offset = 100
    const top = el.getBoundingClientRect().top + window.scrollY - offset
    window.scrollTo({ top, behavior: 'smooth' })
  }
}

watch(() => props.html, (val) => {
  if (val) nextTick(initToc)
}, { immediate: true })

onUnmounted(() => observer?.disconnect())
</script>