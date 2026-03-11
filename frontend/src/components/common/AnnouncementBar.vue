<template>
  <Transition
    enter-active-class="transition-all duration-500 ease-out"
    enter-from-class="opacity-0 translate-y-4 scale-95"
    enter-to-class="opacity-100 translate-y-0 scale-100"
    leave-active-class="transition-all duration-300 ease-in"
    leave-from-class="opacity-100 scale-100"
    leave-to-class="opacity-0 scale-95"
  >
    <div v-if="isVisible" class="w-full max-w-4xl mx-auto">
      <!-- 
        外层容器：
        1. p-[1px] 或 p-[2px] 决定了边框的粗细 
        2. overflow-hidden 裁剪掉旋转层多余的部分
      -->
      <div class="relative overflow-hidden rounded-xl p-[1.5px] group shadow-lg">
        
        <!-- 
          旋转光线层 (核心动画) 
          原理：一个巨大的矩形在背后旋转，背景是锥形渐变
        -->
        <div class="absolute inset-[-500%] animate-[spin_4s_linear_infinite] 
          bg-[conic-gradient(from_90deg_at_50%_50%,#E2E8F0_0%,#3B82F6_50%,#E2E8F0_100%)]
          dark:bg-[conic-gradient(from_90deg_at_50%_50%,#111827_0%,#60A5FA_50%,#111827_100%)]">
        </div>

        <!-- 
          内容层 (遮罩)
          背景色必须是不透明的，以遮住中间的旋转层，只露出边缘
        -->
        <div class="relative rounded-[10px] bg-white dark:bg-[#0B1121] h-full p-4 sm:px-5">
          
          <!-- 新的背景装饰：极光/弥散光斑效果，替代之前的条纹 -->
          <div class="absolute top-0 right-0 -mt-4 -mr-4 w-24 h-24 bg-blue-500/10 rounded-full blur-2xl pointer-events-none"></div>
          <div class="absolute bottom-0 left-0 -mb-4 -ml-4 w-24 h-24 bg-purple-500/10 rounded-full blur-2xl pointer-events-none"></div>

          <div class="relative flex items-start gap-4 z-10">
            <!-- 图标：带呼吸灯效果 -->
            <div class="flex-shrink-0 pt-0.5">
              <div class="relative flex items-center justify-center w-10 h-10 rounded-full bg-blue-50/80 dark:bg-blue-900/20 text-blue-600 dark:text-blue-400 ring-1 ring-blue-100 dark:ring-blue-800/50">
                <!-- 图标后的光晕 -->
                <span class="absolute inset-0 rounded-full bg-blue-400/20 animate-ping opacity-20 duration-1000"></span>
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="relative z-10">
                  <path d="M12 2a3 3 0 0 0-3 3v7a3 3 0 0 0 6 0V5a3 3 0 0 0-3-3Z" />
                  <path d="M19 10v2a7 7 0 0 1-14 0v-2" />
                  <line x1="12" x2="12" y1="19" y2="22" />
                </svg>
              </div>
            </div>

            <!-- 文字区域 -->
            <div class="flex-1 min-w-0">
              <div class="flex items-center gap-2 mb-1">
                <h3 class="text-base font-bold text-gray-900 dark:text-white tracking-tight">
                  {{ title }}
                </h3>
                <span class="px-1.5 py-0.5 rounded text-[10px] font-medium bg-blue-100 text-blue-700 dark:bg-blue-900 dark:text-blue-300 border border-blue-200 dark:border-blue-800">
                  NEW
                </span>
              </div>
              <div class="text-sm text-gray-600 dark:text-gray-400 leading-relaxed">
                <span v-html="content"></span>
                <a 
                  v-if="link" 
                  :href="link" 
                  target="_blank"
                  class="group/link inline-flex items-center ml-2 font-medium text-blue-600 dark:text-blue-400 hover:text-blue-500 transition-colors"
                >
                  查看详情
                  <svg class="w-3.5 h-3.5 ml-0.5 transform transition-transform group-hover/link:translate-x-0.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 12h14M12 5l7 7-7 7"/></svg>
                </a>
              </div>
            </div>

            <!-- 关闭按钮 -->
            <button 
              v-if="closable" 
              @click="handleClose" 
              class="flex-shrink-0 -mr-2 -mt-2 p-2 rounded-full text-gray-400 hover:text-gray-600 dark:text-gray-500 dark:hover:text-gray-300 hover:bg-gray-100 dark:hover:bg-white/5 transition-all"
            >
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <line x1="18" y1="6" x2="6" y2="18"></line>
                <line x1="6" y1="6" x2="18" y2="18"></line>
              </svg>
            </button>
          </div>
        </div>
      </div>
    </div>
  </Transition>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const props = defineProps({
  title: { type: String, default: '重要通知' },
  content: { type: String, default: '' },
  link: { type: String, default: '' },
  closable: { type: Boolean, default: false }
})

const emit = defineEmits(['close'])
const isVisible = ref(true)

function handleClose() {
  isVisible.value = false
  setTimeout(() => {
    emit('close')
  }, 300) // 等待 transition 动画结束
}
</script>

<style scoped>
/* 这里没有任何CSS，样式全部由 Tailwind Utility Classes 完成，保持了代码的纯净 */
</style>