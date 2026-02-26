<template>
  <section class="mt-12 pt-8 border-t border-slate-100 dark:border-slate-800" id="comments">
    <!-- 头部标题 -->
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-bold text-slate-800 dark:text-slate-100">
        评论 <span class="text-base font-normal text-slate-500 ml-1">({{ commentCount }})</span>
      </h3>
      <button 
        class="text-sm text-slate-500 hover:text-blue-600 flex items-center gap-1 transition-colors" 
        @click="refreshComments" 
        :disabled="refreshing"
      >
        <span :class="{'animate-spin': refreshing}">↻</span> 刷新
      </button>
    </div>

    <!-- 骨架屏 Loading -->
    <div v-if="loading" class="space-y-6">
      <div v-for="i in 3" :key="i" class="flex gap-4 animate-pulse">
        <div class="w-10 h-10 bg-slate-200 dark:bg-slate-800 rounded-full shrink-0"></div>
        <div class="flex-1 space-y-2">
          <div class="h-4 bg-slate-200 dark:bg-slate-800 rounded w-1/4"></div>
          <div class="h-4 bg-slate-200 dark:bg-slate-800 rounded w-full"></div>
          <div class="h-4 bg-slate-200 dark:bg-slate-800 rounded w-2/3"></div>
        </div>
      </div>
    </div>

    <div v-else>
      <!-- 顶部发表评论框 -->
      <div class="mb-10">
        <CommentInput 
          placeholder="既然来了，不说两句吗？" 
          :buttonText="posting ? '发布中...' : '发表评论'" 
          :disabled="posting" 
          @submit="handleSubmit" 
        />
      </div>

      <!-- 空状态 -->
      <div v-if="comments.length === 0" class="py-10 text-center text-slate-400 bg-slate-50 dark:bg-slate-800/50 rounded-lg border border-dashed border-slate-200 dark:border-slate-700">
        <div class="text-2xl mb-2">🍃</div>
        <p>暂无评论，快来抢占沙发吧~</p>
      </div>

      <!-- 评论列表 -->
      <ul class="space-y-8">
        <li v-for="rootComment in comments" :key="rootComment.id" class="group">
          <div class="flex gap-4">
            <!-- 头像 -->
            <div class="shrink-0">
              <img 
                :src="rootComment.userImg || '/default-avatar.png'" 
                alt="avatar" 
                class="w-10 h-10 rounded-full object-cover border border-slate-200 dark:border-slate-700 shadow-sm" 
              />
            </div>

            <!-- 主体内容 -->
            <div class="flex-1 min-w-0">
              <!-- 顶部信息 -->
              <div class="flex items-center justify-between mb-1">
                <div class="flex items-center gap-2">
                  <span class="font-semibold text-slate-800 dark:text-slate-200">{{ rootComment.nickname || '匿名用户' }}</span>
                  <span v-if="rootComment.ip" class="text-xs px-1.5 py-0.5 bg-slate-100 dark:bg-slate-800 text-slate-500 rounded text-[10px]">
                    {{ rootComment.ip }}
                  </span>
                </div>
                <span class="text-xs text-slate-400 font-mono">{{ formatDate(rootComment.createTime) }}</span>
              </div>

              <!-- 评论内容 -->
              <div class="text-sm text-slate-700 dark:text-slate-300 leading-relaxed whitespace-pre-wrap">
                {{ rootComment.content }}
              </div>

              <!-- 图片展示 -->
              <div v-if="rootComment.image" class="mt-3">
                <img 
                  :src="rootComment.image" 
                  class="max-h-48 rounded-lg border border-slate-200 dark:border-slate-700 cursor-zoom-in hover:opacity-90 transition-opacity"
                  alt="comment image"
                />
              </div>

              <!-- 操作栏 -->
              <div class="flex items-center gap-4 mt-2 text-xs text-slate-500">
                <button 
                  class="flex items-center gap-1 hover:text-blue-500 transition-colors"
                  @click="initReply(rootComment.id, rootComment.id, rootComment.nickname)"
                >
                  <span class="icon-reply">💬</span> 回复
                </button>
                <!-- 预留点赞功能 -->
                <!-- <button class="flex items-center gap-1 hover:text-red-500 transition-colors">
                   <span>👍</span> {{ rootComment.likeCount || 0 }}
                </button> -->
              </div>

              <!-- 回复输入框 (针对根评论) -->
              <div v-if="replyState.showId === rootComment.id" class="mt-4 animate-fade-in-down">
                <CommentInput 
                  :placeholder="`回复 @${replyState.toNickname}...`" 
                  :buttonText="posting ? '回复中...' : '回复'" 
                  :disabled="posting" 
                  @submit="handleReplySubmit"
                />
                <div class="text-right mt-2">
                   <button class="text-xs text-slate-400 hover:text-slate-600" @click="cancelReply">取消回复</button>
                </div>
              </div>

              <!-- 子评论 (楼中楼) -->
              <div v-if="rootComment.children && rootComment.children.length > 0" class="mt-4 p-4 bg-slate-50 dark:bg-[#161b22] rounded-xl">
                <ul class="space-y-4">
                  <li v-for="child in getVisibleChildren(rootComment)" :key="child.id" class="relative">
                    <div class="flex gap-3">
                      <img 
                        :src="child.userImg || '/default-avatar.png'" 
                        class="w-8 h-8 rounded-full object-cover border border-slate-200 dark:border-slate-700" 
                      />
                      <div class="flex-1">
                        <div class="flex items-baseline flex-wrap gap-2 text-sm">
                          <span class="font-medium text-slate-700 dark:text-slate-200">{{ child.nickname }}</span>
                          
                          <!-- 核心逻辑：显示回复给谁 -->
                          <span v-if="child.toUserName" class="text-slate-400 text-xs flex items-center gap-1">
                            回复 <span class="text-blue-500 font-medium">@{{ child.toUserName }}</span>
                          </span>

                          <span class="text-xs text-slate-400 ml-auto">{{ formatDate(child.createTime) }}</span>
                        </div>

                        <div class="mt-1 text-sm text-slate-600 dark:text-slate-400 whitespace-pre-wrap">
                          {{ child.content }}
                        </div>

                        <div v-if="child.image" class="mt-2">
                          <img :src="child.image" class="max-h-32 rounded border border-slate-200 dark:border-slate-700" />
                        </div>

                        <div class="flex items-center gap-3 mt-1.5">
                          <span v-if="child.ip" class="text-[10px] text-slate-400 bg-slate-200 dark:bg-slate-700 px-1 rounded">{{ child.ip }}</span>
                          <button 
                            class="text-xs text-slate-500 hover:text-blue-500 transition-colors"
                            @click="initReply(child.id, rootComment.id, child.nickname)"
                          >
                            回复
                          </button>
                        </div>
                        
                        <!-- 回复输入框 (针对子评论) -->
                        <div v-if="replyState.showId === child.id" class="mt-3">
                           <CommentInput 
                            :placeholder="`回复 @${replyState.toNickname}...`" 
                            :buttonText="posting ? '回复中...' : '回复'" 
                            :disabled="posting" 
                            @submit="handleReplySubmit"
                          />
                          <div class="text-right mt-1">
                             <button class="text-xs text-slate-400" @click="cancelReply">取消</button>
                          </div>
                        </div>
                      </div>
                    </div>
                  </li>
                </ul>

                <!-- 展开/收起按钮 -->
                <div v-if="rootComment.children.length > 2" class="mt-3 pt-2 border-t border-slate-200 dark:border-slate-700/50">
                  <button 
                    class="text-xs font-medium text-blue-500 hover:text-blue-600 flex items-center gap-1" 
                    @click="toggleExpand(rootComment.id)"
                  >
                    {{ expanded[rootComment.id] ? '收起评论' : `展开剩余 ${rootComment.children.length - 2} 条回复` }}
                    <span class="text-[10px]">{{ expanded[rootComment.id] ? '▲' : '▼' }}</span>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </li>
      </ul>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import CommentInput from '@/components/common/CommentInput.vue'
import http from '@/api/http'
import { getCommentCount, postComment } from '@/api/comment'
import { useUserStore } from '@/stores/user'
import { useNotification } from '@/composables/useNotification'

// --- 类型定义，严格对应后端 VO ---
interface CommentChildVO {
  id: string
  userId: number
  nickname: string
  userImg?: string
  parentId: string // 父评论ID (通常是根评论ID)
  toUserName?: string // 被回复的人
  content: string
  image?: string
  likeCount?: number
  ip?: string
  createTime: string
}

interface CommentVO {
  id: string
  userId: number
  nickname: string
  userImg?: string
  content: string
  image?: string
  ip?: string
  createTime: string
  children?: CommentChildVO[]
}

const props = defineProps<{ articleId: string }>()

// 状态管理
const comments = ref<CommentVO[]>([])
const loading = ref(true)
const posting = ref(false)
const refreshing = ref(false)
const commentCount = ref<number>(0)
const expanded = ref<Record<string, boolean>>({})

// 回复状态管理
const replyState = reactive({
  showId: '' as string,       // 当前显示回复框的评论ID (即点击回复按钮的那条评论ID)
  rootId: '' as string,       // 根评论ID (用于接口传参)
  toNickname: '' as string    // 目标用户昵称 (用于显示 placeholder)
})

const { warning, error, success } = useNotification()
const userStore = useUserStore()

// --- 核心逻辑 ---

const formatDate = (t?: string) => {
  if (!t) return ''
  // 简单格式化，建议引入 dayjs 处理 "几分钟前"
  const d = new Date(t)
  return d.toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

const fetchComments = async () => {
  loading.value = true
  try {
    const res = await http.get(`/front/comments/${props.articleId}`)
    comments.value = Array.isArray(res.data?.data) ? res.data.data : []
  } catch (e) {
    console.error('fetch comments failed', e)
    comments.value = []
  } finally {
    loading.value = false
  }
  // 获取数量
  try {
    const cntRes = await getCommentCount(props.articleId)
    commentCount.value = Number(cntRes.data?.data || 0)
  } catch (_) {}
}

const refreshComments = async () => {
  refreshing.value = true
  await fetchComments()
  refreshing.value = false
}

// 点击回复按钮
const initReply = (currentId: string, rootId: string, toNickname: string) => {
  replyState.showId = currentId
  replyState.rootId = rootId
  replyState.toNickname = toNickname
}

const cancelReply = () => {
  replyState.showId = ''
  replyState.rootId = ''
  replyState.toNickname = ''
}

// 提交评论 (包含根评论和回复)
const submitComment = async (payload: string | { content: string, file?: File | null }, isReply = false) => {
  const token = userStore?.token || localStorage.getItem('auth_token')
  if (!token) {
    warning('请先登录', '登录后才能发表评论')
    return
  }

  const content = typeof payload === 'string' ? payload : (payload.content || '')
  const file = typeof payload === 'string' ? null : (payload.file ?? null)
  
  if (!content.trim() && !file) return

  posting.value = true
  
  // 构建要发送到后端的对象。注意：后端对 /front/comments 目前更可靠地接受 JSON。
  try {
    // 先上传图片（如果有），然后携带返回的图片 URL 一并以 JSON 提交评论，避免 multipart 导致 415
    const payload: Record<string, any> = {
      articleId: String(props.articleId),
      content: content.trim(),
    }

    if (isReply && replyState.rootId) {
      payload.parentId = replyState.rootId
    }

    if (file) {
      const upFd = new FormData()
      upFd.append('file', file)
      // 使用已有的上传接口（与文章封面、编辑器上传保持一致）
      const upRes = await http.post('/admin/upload', upFd)
      const upData = upRes.data?.data || upRes.data
      const imageUrl = typeof upData === 'string' ? upData : upData?.url
      if (!imageUrl) throw new Error('图片上传失败')
      payload.image = imageUrl
    }

    await postComment(payload)
    success('发布成功', '你的评论已发布')

    if (isReply) cancelReply()
    await fetchComments()
  } catch (e: any) {
    console.error(e)
    const msg = e?.response?.data?.msg || e?.message || '网络异常，请重试'
    error('发布失败', msg)
  } finally {
    posting.value = false
  }
}

const handleSubmit = (payload: any) => submitComment(payload, false)
const handleReplySubmit = (payload: any) => submitComment(payload, true)

// 展开/收起逻辑
const getVisibleChildren = (root: CommentVO) => {
  if (!root.children) return []
  return expanded.value[root.id] ? root.children : root.children.slice(0, 2)
}

const toggleExpand = (id: string) => {
  expanded.value[id] = !expanded.value[id]
}

onMounted(() => {
  if (props.articleId) fetchComments()
})

watch(() => props.articleId, (id) => { 
  if (id) fetchComments() 
})
</script>

<style scoped>
/* 简单的淡入动画 */
@keyframes fadeInDown {
  from { opacity: 0; transform: translateY(-5px); }
  to { opacity: 1; transform: translateY(0); }
}
.animate-fade-in-down {
  animation: fadeInDown 0.2s ease-out forwards;
}
</style>