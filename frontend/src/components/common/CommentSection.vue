<template>
  <section class="mt-12 pt-6 border-t border-slate-100 dark:border-slate-700" id="comments">
    <h3 class="text-lg font-semibold mb-4">评论</h3>

    <div v-if="loading" class="space-y-3">
      <div class="h-3 bg-slate-200 dark:bg-slate-800 rounded w-3/4 animate-pulse"></div>
      <div class="h-3 bg-slate-200 dark:bg-slate-800 rounded w-full animate-pulse"></div>
    </div>

    <div v-else>
      <div v-if="comments.length === 0" class="text-sm text-slate-500 mb-4">暂无评论，快来抢沙发 😊</div>

      <ul class="space-y-4 mb-6">
        <li v-for="c in comments" :key="c.id" class="p-4 bg-slate-50 dark:bg-[#111] rounded-lg border border-slate-100 dark:border-slate-800">
          <div class="flex items-center justify-between mb-2">
            <div class="text-sm font-medium text-slate-700 dark:text-slate-200">{{ c.author || '匿名' }}</div>
            <div class="text-xs text-slate-400">{{ formatDate(c.createTime) }}</div>
          </div>
          <div class="text-sm text-slate-700 dark:text-slate-200 whitespace-pre-wrap">{{ c.content }}</div>
        </li>
      </ul>

      <div class="space-y-2">
        <CommentInput :placeholder="'写下你的评论...'" :buttonText="posting ? '提交中...' : '发表评论'" :disabled="posting" @submit="handleSubmit" @attach="handleAttach" />
        <div class="flex items-center justify-end gap-2">
          <button class="px-4 py-2 rounded-md bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-200" @click="refreshComments" :disabled="refreshing">刷新</button>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import CommentInput from '@/components/common/CommentInput.vue'
import http from '@/api/http'
import { useUserStore } from '@/stores/user'
import { useNotification } from '@/composables/useNotification'

interface CommentItem {
  id: string
  author?: string
  content: string
  createTime?: string
}

const props = defineProps<{ articleId: string }>()

const comments = ref<CommentItem[]>([])
const loading = ref(true)
const posting = ref(false)
const refreshing = ref(false)
const newComment = ref('')

const { warning, error, success } = useNotification()
const userStore = useUserStore()

const formatDate = (t?: string) => {
  if (!t) return ''
  const d = new Date(t)
  return d.toLocaleString('zh-CN')
}

const fetchComments = async () => {
  loading.value = true
  try {
    const res = await http.get(`/front/comments`, { params: { articleId: props.articleId } })
    comments.value = Array.isArray(res.data?.data) ? res.data.data : []
  } catch (e) {
    console.error('fetch comments failed', e)
    comments.value = []
  } finally {
    loading.value = false
  }
}

const refreshComments = async () => {
  refreshing.value = true
  await fetchComments()
  refreshing.value = false
}

const submitComment = async (content: string) => {
  const token = userStore?.token || localStorage.getItem('auth_token') || ''
  if (!token) {
    warning('请先登录', '登录后才能发表评论')
    return
  }
  if (!content.trim()) return

  posting.value = true
  // 乐观插入
  const tempId = 'temp-' + Date.now()
  const temp: CommentItem = { id: tempId, author: userStore?.name || '我', content: content.trim(), createTime: new Date().toISOString() }
  comments.value.unshift(temp)
  const payload = { articleId: props.articleId, content: temp.content }

  try {
    const res = await http.post('/front/comments', payload)
    // 如果后端返回了新评论，替换 temp
    const created = res.data?.data
    if (created && created.id) {
      const idx = comments.value.findIndex(c => c.id === tempId)
      if (idx !== -1) comments.value[idx] = created
    } else {
      // 无返回则刷新
      await fetchComments()
    }
    success('已发布', '你的评论已发布')
  } catch (e: any) {
    // 回滚
    comments.value = comments.value.filter(c => c.id !== tempId)
    console.error('post comment failed', e)
    if (e?.response?.status === 401) {
      try { userStore.logout() } catch (_) {}
      warning('登录已过期', '请重新登录')
    } else {
      error('发布失败', '评论发布失败，请重试')
    }
  } finally {
    posting.value = false
  }
}

const handleSubmit = (content: string) => submitComment(content)

const handleAttach = (file: File) => {
  // 简单地 emit 上传事件或保留未来实现
  console.log('attached file', file)
}

onMounted(() => {
  if (props.articleId) fetchComments()
})

watch(() => props.articleId, (id) => { if (id) fetchComments() })
</script>

<style scoped>
.comment-author { font-weight: 600 }
</style>
