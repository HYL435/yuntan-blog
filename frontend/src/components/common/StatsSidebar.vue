<template>
  <div class="space-y-3">
    <!-- 标题 -->
    <div class="flex items-center space-x-2 px-1">
      <span class="w-1 h-4 bg-indigo-500 rounded-full"></span>
      <h3 class="font-bold text-gray-800 dark:text-gray-200">数据概览</h3>
    </div>

    <div class="grid grid-cols-2 gap-3">
      <!-- 文章 (占据较大空间) -->
      <div class="col-span-2 relative overflow-hidden rounded-2xl bg-gradient-to-br from-indigo-500 to-purple-600 p-4 text-white shadow-lg shadow-indigo-500/20">
        <div class="absolute right-0 top-0 opacity-10 transform translate-x-2 -translate-y-2">
          <svg class="w-24 h-24" fill="currentColor" viewBox="0 0 24 24"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path></svg>
        </div>
        <div class="relative z-10">
          <p class="text-indigo-100 text-xs font-medium mb-1">文章沉淀</p>
          <div class="flex items-baseline space-x-1">
            <span class="text-3xl font-black tracking-tight">{{ articleCountDisplay }}</span>
            <span class="text-xs opacity-80">篇</span>
          </div>
        </div>
      </div>

      <!-- 今日访问 -->
      <div class="rounded-2xl bg-white dark:bg-[#252525] p-3 border border-gray-100 dark:border-slate-700 shadow-sm flex flex-col justify-between">
        <div class="w-8 h-8 rounded-lg bg-orange-100 dark:bg-orange-900/30 text-orange-500 flex items-center justify-center mb-2">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6"></path></svg>
        </div>
        <div>
           <span class="text-xl font-bold text-gray-800 dark:text-gray-100 block">{{ todayVisitsDisplay }}</span>
           <span class="text-[10px] text-gray-400">今日热度</span>
        </div>
      </div>

      <!-- 分类 -->
      <div class="rounded-2xl bg-white dark:bg-[#252525] p-3 border border-gray-100 dark:border-slate-700 shadow-sm flex flex-col justify-between">
        <div class="w-8 h-8 rounded-lg bg-teal-100 dark:bg-teal-900/30 text-teal-500 flex items-center justify-center mb-2">
           <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z"></path></svg>
        </div>
        <div>
           <span class="text-xl font-bold text-gray-800 dark:text-gray-100 block">{{ categoryCountDisplay }}</span>
           <span class="text-[10px] text-gray-400">内容分类</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import http from '@/api/http'
import { getAdminStats } from '@/api/stats'
import { getCategories } from '@/api/category'

const articleCount = ref<number | null>(null)
const categoryCount = ref<number | null>(null)
const todayVisits = ref<number | null>(null)

const articleCountDisplay = computed(() => (articleCount.value === null ? '—' : articleCount.value))
const categoryCountDisplay = computed(() => (categoryCount.value === null ? '—' : categoryCount.value))
const todayVisitsDisplay = computed(() => (todayVisits.value === null ? '—' : todayVisits.value))

async function fetchArticleCount() {
  try {
    const res = await http.get('/front/articles/page/categoryOrTags', { params: { pageNo: 1, pageSize: 1 } })
    const body = res.data || res
    const data = body.data || body
    const total = data?.total ?? (Array.isArray(data) ? data.length : undefined)
    articleCount.value = typeof total === 'number' ? total : null
  } catch (e) {
    articleCount.value = null
  }
}

async function fetchCategoryCount() {
  try {
    const res = await getCategories()
    if (Array.isArray(res)) categoryCount.value = res.length
    else if (res && Array.isArray((res as any).data)) categoryCount.value = (res as any).data.length
    else categoryCount.value = null
  } catch (e) {
    categoryCount.value = null
  }
}

async function fetchTodayVisits() {
  try {
    const res = await getAdminStats()
    if (res) {
      if (Array.isArray(res.visits) && res.visits.length) {
        todayVisits.value = res.visits[res.visits.length - 1]
        return
      }
      if (Array.isArray(res.series)) {
        const vSeries = res.series.find(s => /visit|visits|pv|访问/i.test(String(s.name)))
        if (vSeries && Array.isArray(vSeries.data) && vSeries.data.length) {
          todayVisits.value = vSeries.data[vSeries.data.length - 1]
          return
        }
      }
    }
    todayVisits.value = null
  } catch (e) {
    todayVisits.value = null
  }
}

onMounted(() => {
  fetchArticleCount()
  fetchCategoryCount()
  fetchTodayVisits()
})
</script>

<style scoped>
</style>
