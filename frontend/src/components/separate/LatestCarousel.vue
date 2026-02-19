<script setup lang="ts" name="LatestCarousel">
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import ArticleCard from '@/components/cards/ArticleCard.vue'
import ThreeBodyLoader from '@/components/loaders/ThreeBodyLoader.vue'

const router = useRouter()
const row = ref<HTMLElement | null>(null)

const props = defineProps<{ title?: string }>()

let scrollRaf: number | null = null
let velocity = 0
let animating = false
const friction = 0.92
const minVelocity = 0.1
const maxSpeed = 25

// 文章列表数据
const articles = ref<any[]>([])
const loading = ref(false)
const currentPage = ref(1)
const totalPages = ref(1)
const isScrolledToBottom = ref(false)

// 获取推荐文章
const fetchRecommendedArticles = async (pageNo: number = 1) => {
  loading.value = true
  try {
    const response = await fetch(`/front/articles/page?pageNo=${pageNo}&pageSize=5`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    })
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }
    
    const data = await response.json()
    console.log('API response:', data)
    console.log('API response keys:', Object.keys(data))
    console.log('data.data:', data.data)
    console.log('data.code:', data.code)
    
    // 处理返回的数据结构 - 优先检查内部属性
    let result: any[] = []
    
    if (data.data) {
      if (Array.isArray(data.data)) {
        result = data.data
      } else if (data.data.records && Array.isArray(data.data.records)) {
        result = data.data.records
      } else if (data.data.list && Array.isArray(data.data.list)) {
        result = data.data.list
      } else if (typeof data.data === 'object') {
        // 如果是对象，尝试获取第一个数组属性
        const firstArrayKey = Object.keys(data.data).find(key => Array.isArray(data.data[key]))
        if (firstArrayKey) {
          result = data.data[firstArrayKey]
        }
      }
    } else if (Array.isArray(data)) {
      result = data
    }
    
    // 如果是第一页，替换数据；否则追加数据
    if (pageNo === 1) {
      articles.value = result
    } else {
      articles.value.push(...result)
    }
    
    // 记录分页信息
    currentPage.value = pageNo
    if (data.data && data.data.pages) {
      totalPages.value = parseInt(data.data.pages)
    }
    
    console.log('Final articles:', articles.value)
    console.log('Articles count:', articles.value.length)
    console.log('Total pages:', totalPages.value)
  } catch (err) {
    console.error('Failed to fetch articles:', err)
    if (currentPage.value === 1) {
      articles.value = []
    }
  } finally {
    loading.value = false
  }
}

// 加载下一页
const loadNextPage = async () => {
  if (currentPage.value < totalPages.value) {
    isScrolledToBottom.value = false  // 重置滚动状态
    await fetchRecommendedArticles(currentPage.value + 1)
  }
}

const handleArticleClick = (id?: string | number) => {
  if (!id) return
  router.push(`/article/${id}`)
}

const handleTagClick = (tag?: string) => {
  if (!tag) return
  router.push(`/tag/${encodeURIComponent(tag)}`)
}

const animateScroll = () => {
  if (!row.value) return

  const step = () => {
    if (!row.value) return
    
    const maxScroll = row.value.scrollWidth - row.value.clientWidth
    if (maxScroll <= 0) {
      animating = false
      velocity = 0
      return
    }

    row.value.scrollLeft += velocity

    if (row.value.scrollLeft <= 0 || row.value.scrollLeft >= maxScroll) {
      velocity = 0
      animating = false
      return
    }

    velocity *= friction

    if (Math.abs(velocity) > minVelocity && animating) {
      scrollRaf = requestAnimationFrame(step)
    } else {
      animating = false
    }
  }

  if (!animating) {
    animating = true
    scrollRaf = requestAnimationFrame(step)
  }
}

const handleWheel = (event: WheelEvent) => {
  if (!row.value) return
  
  const maxScroll = row.value.scrollWidth - row.value.clientWidth
  
  if (maxScroll <= 0) return

  event.preventDefault()
  event.stopPropagation()
  
  // 使用 deltaX，如果没有则使用 deltaY
  const delta = event.deltaX !== 0 ? event.deltaX : event.deltaY
  
  velocity = delta * 0.15
  velocity = Math.min(Math.max(velocity, -maxSpeed), maxSpeed)
  
  animateScroll()
}

const attachWheelListener = () => {
  if (!row.value) return
  row.value.addEventListener('wheel', handleWheel, { passive: false })
  row.value.addEventListener('scroll', handleScroll)
}

const detachWheelListener = () => {
  if (row.value) {
    row.value.removeEventListener('wheel', handleWheel)
    row.value.removeEventListener('scroll', handleScroll)
  }
}

// 检测是否滚动到底部
const handleScroll = () => {
  if (!row.value) return
  
  const scrollLeft = row.value.scrollLeft
  const scrollWidth = row.value.scrollWidth
  const clientWidth = row.value.clientWidth
  
  // 判断是否滚动到底部（留20px的误差范围）
  isScrolledToBottom.value = scrollWidth - scrollLeft - clientWidth <= 20
}

// 监听文章数据变化，数据加载完后添加事件监听
watch(articles, async (newArticles) => {
  if (newArticles.length > 0) {
    await nextTick()
    attachWheelListener()
    // 延迟检测滚动状态，确保 DOM 已完全渲染
    setTimeout(() => {
      handleScroll()
    }, 100)
  }
}, { immediate: true })

onMounted(() => {
  // 获取推荐文章
  fetchRecommendedArticles()
})

onUnmounted(() => {
  if (scrollRaf) {
    cancelAnimationFrame(scrollRaf)
  }
  detachWheelListener()
})
</script>

<template>
  <div class="latest-carousel">
    <button v-if="props.title" class="btn" type="button">
      <span>{{ props.title }}</span>
    </button>
    <div v-if="loading && articles.length === 0" class="carousel-row" style="display: flex; align-items: center; justify-content: center; min-height: 300px;">
      <ThreeBodyLoader />
    </div>
    <div v-else-if="articles.length === 0" class="carousel-row" style="display: flex; align-items: center; justify-content: center; min-height: 300px;">
      <span>无数据</span>
    </div>
    <div v-else class="carousel-wrapper">
      <div ref="row" class="carousel-row">
        <ArticleCard
          v-for="article in articles"
          :key="article.id"
          :article="article"
          class="flex-none w-80 snap-start"
          @click="handleArticleClick(article.id)"
          @tag-click="handleTagClick"
        />
      </div>
      <button 
        v-show="currentPage < totalPages && isScrolledToBottom" 
        class="load-more-btn" 
        @click="loadNextPage"
        :disabled="loading"
      >
        <span class="btn-text">查看更多</span>
        <svg class="btn-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <polyline points="9 18 15 12 9 6"></polyline>
        </svg>
      </button>
    </div>
  </div>
</template>

<style scoped>
.latest-carousel {
  border: none;
  border-radius: 18px;
  padding: 12px 12px 16px;
  position: relative;
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

.latest-carousel::before,
.latest-carousel::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 18px;
  padding: 1px;
  pointer-events: none;
  z-index: 1;
  animation-timing-function: linear;
  animation-iteration-count: infinite;
}

.latest-carousel::before {
  background: conic-gradient(from 0deg, #6366f1, #8b5cf6, #6366f1);
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  animation: rotateGlowLeft 6s infinite;
}

.latest-carousel::after {
  background: conic-gradient(from 180deg, #8b5cf6, #6366f1, #8b5cf6);
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  animation: rotateGlowRight 8s infinite reverse;
}

.carousel-row {
  position: relative;
  z-index: 2;
}

.carousel-wrapper {
  position: relative;
  z-index: 2;
  display: flex;
  gap: 16px;
  align-items: center;
}

.load-more-btn {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  padding: 16px 12px;
  min-width: 70px;
  height: 200px;
  border: none;
  border-radius: 12px;
  background: transparent;
  color: #6b7280;
  font-weight: 300;
  cursor: pointer;
  transition: all 0.3s ease;
  flex-direction: column;
  writing-mode: vertical-rl;
  text-orientation: mixed;
}

.load-more-btn .btn-text {
  animation: heartbeat 1.5s ease-in-out infinite;
  letter-spacing: 6px;
}

.load-more-btn .btn-icon {
  width: 20px;
  height: 20px;
  animation: heartbeat 1.5s ease-in-out infinite;
  transform: rotate(90deg);
}

.load-more-btn:hover:not(:disabled) {
  color: #6366f1;
}

.load-more-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

@keyframes heartbeat {
  0% {
    transform: scale(1);
    opacity: 1;
  }
  14% {
    transform: scale(1.1);
    opacity: 1;
  }
  28% {
    transform: scale(1);
    opacity: 1;
  }
  42% {
    transform: scale(1.1);
    opacity: 1;
  }
  70% {
    transform: scale(1);
    opacity: 0.8;
  }
  100% {
    transform: scale(1);
    opacity: 0.8;
  }
}

:global(.dark) .load-more-btn {
  color: #d1d5db;
}

:global(.dark) .load-more-btn:hover:not(:disabled) {
  color: #c084fc;
}

.latest-carousel:hover {
  box-shadow: 0 0 20px rgba(99, 102, 241, 0.3),
              0 0 40px rgba(99, 102, 241, 0.15);
  transition: box-shadow 0.5s ease-in-out;
}

.latest-carousel:hover::before {
  background: conic-gradient(from 0deg, #8b5cf6, #6366f1, #8b5cf6);
  animation: rotateGlowLeftHover 6s infinite;
}

.latest-carousel:hover::after {
  background: conic-gradient(from 180deg, #6366f1, #8b5cf6, #6366f1);
  animation: rotateGlowRightHover 8s infinite reverse;
}

@keyframes rotateGlowLeft {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

@keyframes rotateGlowRight {
  from {
    transform: rotate(360deg);
  }
  to {
    transform: rotate(0deg);
  }
}

@keyframes rotateGlowLeftHover {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

@keyframes rotateGlowRightHover {
  from {
    transform: rotate(360deg);
  }
  to {
    transform: rotate(0deg);
  }
}

.carousel-row {
  display: flex;
  gap: 24px;
  overflow-x: auto;
  overflow-y: hidden;
  padding: 2px 2px 10px;
  position: relative;
  cursor: grab;
  user-select: none;
  scroll-behavior: auto;
  scrollbar-width: none;
  transition: all 0.3s ease;
  flex: 1;
  min-width: 0;
}

.carousel-row::-webkit-scrollbar {
  display: none;
}

.carousel-row:active {
  cursor: grabbing;
}

:global(.dark) .latest-carousel,
:global(.dark-mode) .latest-carousel {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

:global(.dark) .latest-carousel::before,
:global(.dark-mode) .latest-carousel::before {
  background: conic-gradient(from 0deg, #a78bfa, #c084fc, #a78bfa);
}

:global(.dark) .latest-carousel::after,
:global(.dark-mode) .latest-carousel::after {
  background: conic-gradient(from 180deg, #c084fc, #a78bfa, #c084fc);
}

:global(.dark) .latest-carousel:hover,
:global(.dark-mode) .latest-carousel:hover {
  box-shadow: 0 0 20px rgba(168, 85, 247, 0.4),
              0 0 40px rgba(168, 85, 247, 0.2);
}

:global(.dark) .latest-carousel:hover::before,
:global(.dark-mode) .latest-carousel:hover::before {
  background: conic-gradient(from 0deg, #c084fc, #a78bfa, #c084fc);
}

:global(.dark) .latest-carousel:hover::after,
:global(.dark-mode) .latest-carousel:hover::after {
  background: conic-gradient(from 180deg, #a78bfa, #c084fc, #a78bfa);
}

:global(.dark) .btn,
:global(.dark-mode) .btn {
  color: #d1d5db;
}

:global(.dark) .btn:hover,
:global(.dark-mode) .btn:hover {
  color: #f3f4f6;
}

.btn {
  font-size: 17px;
  background: transparent;
  border: none;
  padding: 0.75em 1.5em;
  color: #6b7280;
  text-transform: uppercase;
  position: relative;
  transition: 0.5s ease;
  cursor: pointer;
  margin: 8px 0 12px;
  flex-shrink: 0;
  font-weight: 600;
  border-radius: 8px;
}

.btn::before {
  content: "";
  position: absolute;
  left: 0;
  bottom: 0;
  height: 2px;
  width: 0;
  background-color: #ffc506;
  transition: 0.5s ease;
}

.btn:hover {
  color: #1f2937;
  transition-delay: 0.5s;
}

.btn:hover::before {
  width: 100%;
}

.btn::after {
  content: "";
  position: absolute;
  left: 0;
  bottom: 0;
  height: 0;
  width: 100%;
  background-color: #ffc506;
  transition: 0.4s ease;
  z-index: -1;
}

.btn:hover::after {
  height: 100%;
  transition-delay: 0.4s;
  color: aliceblue;
}
</style>
