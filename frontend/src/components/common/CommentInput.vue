<template>
  <div class="w-full py-4" ref="rootRef">
    <div class="relative max-w-xl w-full mx-auto">
      <div class="relative flex flex-col bg-transparent overflow-visible">
        <textarea
          ref="textareaRef"
          v-model="value"
          :placeholder="placeholder"
          :id="id"
          class="w-full min-h-[80px] max-h-[300px] rounded-xl rounded-b-none px-5 py-4 bg-white/5 dark:bg-black text-slate-900 dark:text-white placeholder:text-slate-400 dark:placeholder:text-white/60 border-0 outline-none resize-y focus:ring-0 focus:outline-none leading-[1.4]"
        ></textarea>

        <div class="h-14 bg-transparent rounded-b-xl">
          <div class="absolute left-3 bottom-4 flex items-center gap-3">
            <label class="cursor-pointer rounded-lg p-2 bg-white/5 hover:bg-white/10">
              <input class="hidden" type="file" @change="onFileChange" />
              <svg class="text-slate-400 hover:text-slate-200 transition-colors" stroke-linejoin="round" stroke-linecap="round" stroke-width="2" stroke="currentColor" fill="none" viewBox="0 0 24 24" height="16" width="16" xmlns="http://www.w3.org/2000/svg">
                <path d="m21.44 11.05-9.19 9.19a6 6 0 0 1-8.49-8.49l8.57-8.57A4 4 0 1 1 18 8.84l-8.59 8.57a2 2 0 0 1-2.83-2.83l8.49-8.48"></path>
              </svg>
            </label>

            <div class="relative">
              <button
                class="rounded-full flex items-center gap-2 px-4 py-2 border h-10 cursor-pointer bg-yellow-400/10 hover:bg-yellow-400/20 border-yellow-300 text-yellow-500"
                type="button"
                @click.stop="togglePicker"
                title="插入表情"
              >
                <div class="w-8 h-8 flex items-center justify-center shrink-0 rounded-full bg-yellow-400 text-white text-xl">😊</div>
              </button>

              <transition name="picker">
                <div v-show="showPicker" class="absolute left-0 top-full mt-2 z-50 w-[1200px] max-w-[95vw] bg-white dark:bg-[#0b1220] rounded-lg shadow-xl border border-slate-100 dark:border-slate-800 p-3">
                  <div class="flex gap-x-[5px] gap-y-[1px] flex-nowrap sm:flex-wrap sm:justify-start sm:items-start sm:gap-x-[5px] sm:gap-y-[1px] overflow-x-auto py-0">
                    <button v-for="emo in emojis" :key="emo" class="w-8 h-8 p-0 flex items-center justify-center rounded-sm text-lg leading-none hover:bg-slate-100 dark:hover:bg-slate-800" @click.stop="onPick(emo)">{{ emo }}</button>
                  </div>
                </div>
              </transition>
            </div>
          </div>

          <div class="absolute right-3 bottom-4">
            <button class="rounded-lg p-2 bg-white/5 hover:bg-white/10 text-slate-400 hover:text-slate-200 cursor-pointer transition-colors" type="button" @click="onSubmit" :disabled="disabled || !value.trim()">
              <svg stroke-linejoin="round" stroke-linecap="round" stroke-width="2" stroke="currentColor" fill="none" viewBox="0 0 24 24" height="16" width="16" xmlns="http://www.w3.org/2000/svg">
                <path d="m22 2-7 20-4-9-9-4Z"></path>
                <path d="M22 2 11 13"></path>
              </svg>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, defineEmits, defineProps, nextTick, onMounted, onUnmounted } from 'vue'

const emits = defineEmits(['submit', 'attach', 'cancel'])
const props = defineProps({
  placeholder: { type: String, default: '写下你的评论...' },
  id: { type: String, default: 'comment-input' },
  valueModel: { type: String, default: '' },
  buttonText: { type: String, default: '发送' },
  disabled: { type: Boolean, default: false }
})

const value = ref(props.valueModel)
const textareaRef = ref<HTMLTextAreaElement | null>(null)
const rootRef = ref<HTMLElement | null>(null)

const insertEmoji = async (emoji: string) => {
  const ta = textareaRef.value
  if (!ta) {
    value.value = (value.value || '') + emoji
    return
  }
  const start = ta.selectionStart ?? value.value.length
  const end = ta.selectionEnd ?? start
  const text = value.value || ''
  value.value = text.slice(0, start) + emoji + text.slice(end)
  await nextTick()
  const pos = start + emoji.length
  ta.focus()
  ta.selectionStart = ta.selectionEnd = pos
}

// Emoji picker state
const showPicker = ref(false)
const emojis = ['😊','😀','😄','👍','🎉','🔥','😅','😁','😍','😎','🤔','👏']

const togglePicker = () => { showPicker.value = !showPicker.value }
const onPick = (emo: string) => { insertEmoji(emo); showPicker.value = false }

const handleDocClick = (e: MouseEvent) => {
  const t = e.target as Node
  const root = rootRef.value
  if (!root) return
  if (!root.contains(t)) {
    showPicker.value = false
  }
}

onMounted(() => document.addEventListener('click', handleDocClick))
onUnmounted(() => document.removeEventListener('click', handleDocClick))

watch(() => props.valueModel, (v) => { value.value = v })

const onFileChange = (e: Event) => {
  const input = e.target as HTMLInputElement
  if (input?.files && input.files.length) {
    emits('attach', input.files[0])
  }
}

const onSubmit = () => {
  if (!value.value.trim()) return
  emits('submit', value.value.trim())
  value.value = ''
}
</script>

<style scoped>
.rounded-b-none { border-bottom-left-radius: 0; border-bottom-right-radius: 0 }
.rounded-b-xl { border-bottom-left-radius: 0.75rem; border-bottom-right-radius: 0.75rem }

/* Transition for emoji picker */
.picker-enter-active, .picker-leave-active { transition: all 160ms cubic-bezier(.2,.8,.2,1); }
.picker-enter-from, .picker-leave-to { opacity: 0; transform: translateY(-6px) scale(0.98); }
.picker-enter-to, .picker-leave-from { opacity: 1; transform: translateY(0) scale(1); }
</style>
