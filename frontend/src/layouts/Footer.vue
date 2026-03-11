<template>
  <footer class="h-auto bg-gray-100 dark:bg-slate-900 text-gray-900 dark:text-[rgba(255,255,255,0.87)] transition-colors duration-300 relative z-50">
    <div class="max-w-6xl mx-auto px-4 py-8">
      <!-- icons row removed -->

      <!-- three columns -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-8 text-center md:text-left">
        <div>
          <h4 class="text-lg font-semibold mb-3">直达</h4>
          <ul class="space-y-2">
            <li><a href="#" class="hover:underline">藏宝阁</a></li>
            <li><a href="#" class="hover:underline">友链</a></li>
            <li><a href="#" class="hover:underline">留言厅</a></li>
            <li><a href="#" class="hover:underline">个人相册</a></li>
            <li><a href="#" class="hover:underline">幻音坊</a></li>
          </ul>
        </div>

        <div>
          <h4 class="text-lg font-semibold mb-3">分类</h4>
          <ul class="space-y-2">
            <li><a href="#" class="hover:underline">学习笔记</a></li>
            <li><a href="#" class="hover:underline">我的项目</a></li>
            <li><a href="#" class="hover:underline">游戏</a></li>
            <li><a href="#" class="hover:underline">动漫</a></li>
            <li><a href="#" class="hover:underline">随想</a></li>
            <li><a href="#" class="hover:underline">查看全部</a></li>
          </ul>
        </div>

        <div class="md:text-right">
          <h4 class="text-lg font-semibold mb-3">友链</h4>
          <ul class="space-y-2">
            <li><a href="#" class="hover:underline">POETIZE</a></li>
            <li><a href="#" class="hover:underline">轻笑Chuckle</a></li>
            <li><a href="#" class="hover:underline">Ezuxoay</a></li>
            <li><a href="#" class="hover:underline">380AM-0204</a></li>
            <li><a href="#" class="hover:underline">查看更多</a></li>
          </ul>
        </div>
      </div>

      <!-- bottom line -->
      <div class="mt-8 border-t border-gray-200 dark:border-slate-700/50 pt-4 flex flex-col md:flex-row items-center justify-between text-sm">
        <div class="font-semibold">Copyright © 2026 By Yuntan</div>
        <div class="mt-3 md:mt-0">本站已运行：<span class="font-medium">{{ runtimeString }}</span></div>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts" name="Footer">
import { ref, onMounted, onUnmounted } from 'vue'

const runtimeString = ref('0天 0时 0分 0秒')
let timer: number | undefined

function updateRuntime() {
  const now = new Date()
  const start = new Date(now)
  start.setHours(0, 0, 0, 0)
  let diff = Math.floor((now.getTime() - start.getTime()) / 1000)

  const days = Math.floor(diff / 86400)
  diff = diff % 86400
  const hours = Math.floor(diff / 3600)
  diff = diff % 3600
  const minutes = Math.floor(diff / 60)
  const seconds = diff % 60

  runtimeString.value = `${days}天 ${hours}时 ${minutes}分 ${seconds}秒`
}

onMounted(() => {
  updateRuntime()
  timer = window.setInterval(updateRuntime, 1000)
})

onUnmounted(() => {
  if (timer) window.clearInterval(timer)
})
</script>

<style scoped>
/* 保持主色不变，仅微调间距与文字 */
</style>