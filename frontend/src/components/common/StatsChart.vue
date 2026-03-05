<template>
  <div ref="chartRef" class="w-full h-72 md:h-96"></div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, onBeforeUnmount } from 'vue'

const props = defineProps<{
  x?: string[];
  series?: { name: string; data: number[] }[];
  type?: 'line' | 'bar';
  autoResize?: boolean;
}>()

const chartRef = ref<HTMLDivElement | null>(null)
let chart: any = null

const initChart = async () => {
  if (!chartRef.value) return
  const echarts = await import('echarts')
  chart = echarts.init(chartRef.value)
  const option = buildOption()
  chart.setOption(option)
}

const buildOption = () => {
  const x = props.x || []
  const s = props.series || []
  return {
    tooltip: { trigger: 'axis' },
    legend: { top: 6 },
    grid: { left: '6%', right: '6%', bottom: '10%', containLabel: true },
    xAxis: { type: 'category', data: x },
    yAxis: { type: 'value' },
    series: s.map((ser) => ({
      name: ser.name,
      type: props.type || 'line',
      data: ser.data,
      smooth: true,
      emphasis: { focus: 'series' }
    }))
  }
}

onMounted(() => {
  initChart()
  if (props.autoResize ?? true) {
    window.addEventListener('resize', resize)
  }
})

watch(() => [props.x, props.series], async () => {
  if (!chart) await initChart()
  chart?.setOption(buildOption())
}, { deep: true })

const resize = () => chart?.resize()
onBeforeUnmount(() => { window.removeEventListener('resize', resize); chart?.dispose(); chart = null })
</script>

<style scoped>
.echarts-container { width: 100%; height: 100%; }
</style>
