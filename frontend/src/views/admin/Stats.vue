<template>
  <div class="p-6">
    <h2 class="text-2xl font-bold mb-4">站点统计</h2>

    <div class="grid grid-cols-1 gap-6">
      <div class="card p-4">
        <StatsChart :x="chartX" :series="chartSeries" />
      </div>

      <div class="card p-4">
        <h3 class="font-medium mb-2">原始数据</h3>
        <pre class="text-sm overflow-auto bg-gray-100 dark:bg-[#111] p-3 rounded">{{ raw }}</pre>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import StatsChart from '@/components/common/StatsChart.vue'
import { getAdminStats } from '@/api/stats'

const raw = ref<any>(null)
const chartX = ref<string[]>([])
const chartSeries = ref<{ name: string; data: number[] }[]>([])

const normalize = (data: any) => {
  // 适配多种后端结构
  if (!data) return { x: [], series: [] }
  if (data.x && data.series) return { x: data.x, series: data.series }
  if (data.dates) {
    const x = data.dates
    const series: any[] = []
    if (data.visits) series.push({ name: '访问量', data: data.visits })
    if (data.posts) series.push({ name: '文章数', data: data.posts })
    if (data.users) series.push({ name: '用户数', data: data.users })
    return { x, series }
  }
  // 最后回退：尝试把数字数组当作单系列
  return { x: [], series: [] }
}

onMounted(async () => {
  try {
    const data = await getAdminStats()
    raw.value = data
    const n = normalize(data)
    chartX.value = n.x
    chartSeries.value = n.series
  } catch (e) {
    raw.value = { error: (e as any)?.message || e }
  }
})
</script>

<style scoped>
.card { background: var(--card-bg, #fff); border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.04); }
</style>
