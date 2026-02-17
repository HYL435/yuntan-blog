<template>
  <div class="min-h-screen bg-gray-50 dark:bg-[#121212] pt-16 pb-8">
    <div class="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- 加载状态 -->
      <div v-if="isLoading" class="text-center py-20">
        <p class="text-xl text-gray-600 dark:text-gray-400">加载中...</p>
      </div>

      <!-- 错误状态 -->
      <div v-else-if="!user" class="text-center py-20">
        <p class="text-xl text-red-600 dark:text-red-400">用户信息加载失败，请重新登录</p>
        <router-link to="/login" class="text-indigo-600 hover:text-indigo-700">返回登录</router-link>
      </div>

      <!-- 统一的容器盒子：左边资料卡 + 右边立方体 -->
      <div v-else class="bg-white dark:bg-[#1E1E1E] rounded-3xl shadow-2xl dark:shadow-[0_20px_60px_rgba(239,68,68,0.16)] overflow-hidden mt-4">
        <div class="flex flex-col lg:flex-row min-h-72">
          <!-- 左侧：个人资料内容 -->
          <div class="flex-1 p-6 pt-10 border-b lg:border-b-0 lg:border-r border-gray-200 dark:border-[#2D2D2D] flex flex-col">
            <!-- 头部 -->
            <div class="flex items-center justify-between mb-6">
              <h2 class="text-2xl font-bold text-gray-900 dark:text-white">个人资料</h2>
              <!-- 编辑/保存按钮 -->
              <div class="flex gap-2">
                <button
                  v-if="!isEditing"
                  @click="startEdit"
                  class="px-3 py-1.5 text-xs bg-indigo-600 hover:bg-indigo-700 text-white font-medium rounded-full transition flex items-center gap-1"
                >
                  <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                  </svg>
                  编辑
                </button>
                <template v-else>
                  <button
                    @click="submitUpdate"
                    :disabled="isSaving"
                    class="px-3 py-1.5 text-xs bg-green-600 hover:bg-green-700 disabled:opacity-50 text-white font-medium rounded-full transition"
                  >
                    {{ isSaving ? '保存中...' : '保存' }}
                  </button>
                  <button
                    @click="cancelEdit"
                    class="px-3 py-1.5 text-xs bg-red-600 hover:bg-red-700 text-white font-medium rounded-full transition"
                  >
                    取消
                  </button>
                </template>
              </div>
            </div>

            <!-- 头像和徽章 -->
            <div class="flex flex-col items-center mb-6">
              <div v-if="isEditing" class="group relative mb-4">
                <img
                  :src="previewUrl || formData.image || 'https://via.placeholder.com/256?text=头像'"
                  :alt="formData.nickname"
                  class="w-28 h-28 rounded-full border-4 border-indigo-500 dark:border-[#6366F1] shadow-lg dark:shadow-[0_10px_30px_rgba(239,68,68,0.22)] object-cover cursor-pointer hover:opacity-75 transition"
                  @click="focusFileInput"
                />
                <!-- 相机图标 -->
                <div class="absolute inset-0 w-28 h-28 rounded-full flex items-center justify-center bg-black/40 opacity-0 group-hover:opacity-100 transition-opacity cursor-pointer" @click="focusFileInput">
                  <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z" />
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 13a3 3 0 11-6 0 3 3 0 016 0z" />
                  </svg>
                </div>
                <input
                  ref="fileInput"
                  type="file"
                  accept="image/*"
                  class="hidden"
                  @change="handleImageSelect"
                />
              </div>
              <img
                v-else
                :src="user?.image || 'https://via.placeholder.com/256?text=头像'"
                :alt="user?.nickname"
                class="w-28 h-28 rounded-full border-4 border-indigo-500 dark:border-[#6366F1] shadow-lg dark:shadow-[0_10px_30px_rgba(239,68,68,0.22)] object-cover mb-4"
              />
              <span class="px-4 py-2 rounded-full text-xs font-medium bg-gray-200 dark:bg-[#2D2D2D] text-gray-700 dark:text-[#E5E7EB]">
                用户名：{{ user?.username || '未设置' }}
              </span>
            </div>

            <!-- 信息表单 -->
            <div class="space-y-2 flex-1">
              <!-- 昵称 -->
              <div class="flex items-center justify-between py-2 border-b border-gray-200 dark:border-[#2D2D2D]">
                <label class="text-base font-medium text-gray-600 dark:text-[#9CA3AF] min-w-max">昵称</label>
                <div class="flex-1 ml-4">
                  <div v-if="!isEditing" class="text-base text-gray-900 dark:text-[#E5E7EB] text-right">
                    {{ user?.nickname || '未设置' }}
                  </div>
                  <input
                    v-else
                    v-model="formData.nickname"
                    type="text"
                    maxlength="50"
                    class="w-full px-3 py-2 text-base text-right border-2 border-gray-300 dark:border-[#2D2D2D] rounded-xl bg-white dark:bg-[#2D2D2D] text-gray-900 dark:text-[#E5E7EB] placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:border-indigo-500 dark:focus:border-[#6366F1] focus:ring-2 focus:ring-indigo-500/20 dark:focus:ring-[#6366F1]/30 focus:shadow-md dark:focus:shadow-[0_0_0_3px_rgba(239,68,68,0.22)] transition-all duration-200 ease-out"
                  />
                </div>
              </div>

              <!-- 邮箱 -->
              <div class="flex items-center justify-between py-2 border-b border-gray-200 dark:border-[#2D2D2D]">
                <label class="text-base font-medium text-gray-600 dark:text-[#9CA3AF] min-w-max">邮箱</label>
                <div class="flex-1 ml-4">
                  <div v-if="!isEditing" class="text-base text-gray-900 dark:text-[#E5E7EB] text-right">
                    {{ user?.email || '未设置' }}
                  </div>
                  <input
                    v-else
                    v-model="formData.email"
                    type="email"
                    class="w-full px-3 py-2 text-base text-right border-2 border-gray-300 dark:border-[#2D2D2D] rounded-xl bg-white dark:bg-[#2D2D2D] text-gray-900 dark:text-[#E5E7EB] placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:border-indigo-500 dark:focus:border-[#6366F1] focus:ring-2 focus:ring-indigo-500/20 dark:focus:ring-[#6366F1]/30 focus:shadow-md dark:focus:shadow-[0_0_0_3px_rgba(239,68,68,0.22)] transition-all duration-200 ease-out"
                  />
                </div>
              </div>

              <!-- 手机号 -->
              <div class="flex items-center justify-between py-2 border-b border-gray-200 dark:border-[#2D2D2D]">
                <label class="text-base font-medium text-gray-600 dark:text-[#9CA3AF] min-w-max">手机</label>
                <div class="text-base text-gray-500 dark:text-[#9CA3AF] ml-4">暂未实现</div>
              </div>

              <!-- 个人简介 -->
              <div class="flex items-start justify-between py-2">
                <label class="text-base font-medium text-gray-600 dark:text-[#9CA3AF] min-w-max mt-1">简介</label>
                <div class="flex-1 ml-4">
                  <div v-if="!isEditing" class="text-base text-gray-900 dark:text-[#E5E7EB] text-right">
                    {{ user?.intro || '未设置' }}
                  </div>
                  <textarea
                    v-else
                    v-model="formData.intro"
                    rows="2"
                    maxlength="100"
                    class="w-full px-3 py-2 text-base text-right border-2 border-gray-300 dark:border-[#2D2D2D] rounded-xl bg-white dark:bg-[#2D2D2D] text-gray-900 dark:text-[#E5E7EB] placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:border-indigo-500 dark:focus:border-[#6366F1] focus:ring-2 focus:ring-indigo-500/20 dark:focus:ring-[#6366F1]/30 focus:shadow-md dark:focus:shadow-[0_0_0_3px_rgba(239,68,68,0.22)] transition-all duration-200 ease-out resize-none"
                  />
                </div>
              </div>
            </div>
          </div>

          <!-- 右侧：动画立方体 -->
          <div class="flex-1 flex justify-end items-end p-6 bg-gradient-to-br from-indigo-50 to-purple-50 dark:from-[#1E1E1E] dark:to-[#121212]">
            <div class="pb-4" style="transform: translate(-75px, -50px);">
              <AnimatedCubes />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useNotification } from '@/composables/useNotification'
import AnimatedCubes from '@/components/separate/AnimatedCubes.vue'

const router = useRouter()
const userStore = useUserStore()
const { success, error } = useNotification()

const isLoading = ref(true)
const isEditing = ref(false)
const isSaving = ref(false)
const fileInput = ref<HTMLInputElement>()
const imageFile = ref<File | null>(null)
const previewUrl = ref('')

const formData = ref({
  nickname: '',
  email: '',
  phone: '',
  image: '',
  intro: '',
})

const originalData = ref({
  nickname: '',
  email: '',
  phone: '',
  image: '',
  intro: '',
})

const user = computed(() => userStore.user)



// 开始编辑
const startEdit = () => {
  if (user.value) {
    originalData.value = {
      nickname: user.value.nickname || '',
      email: user.value.email || '',
      phone: user.value.phone || '',
      image: user.value.image || '',
      intro: user.value.intro || '',
    }
    formData.value = { ...originalData.value }
  }
  imageFile.value = null
  previewUrl.value = ''
  isEditing.value = true
}

// 取消编辑
const cancelEdit = () => {
  formData.value = { ...originalData.value }
  imageFile.value = null
  if (previewUrl.value) {
    URL.revokeObjectURL(previewUrl.value)
    previewUrl.value = ''
  }
  isEditing.value = false
}

// 焦点聚焦到文件输入
const focusFileInput = () => {
  fileInput.value?.click()
}

const handleImageSelect = (event: Event) => {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]

  if (!file) {
    imageFile.value = null
    previewUrl.value = ''
    return
  }

  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    error('请选择有效的图片文件')
    input.value = ''
    return
  }

  // 验证文件大小（最大 10MB）
  if (file.size > 10 * 1024 * 1024) {
    error('文件大小不能超过 10MB')
    input.value = ''
    return
  }

  imageFile.value = file

  // 创建预览 URL
  previewUrl.value = URL.createObjectURL(file)
}

// 提交更新
const submitUpdate = async () => {
  if (!formData.value.nickname.trim()) {
    error('昵称不能为空')
    return
  }

  // 验证用户信息
  if (!user.value || !user.value.id) {
    error('用户信息丢失，请重新登录')
    return
  }

  isSaving.value = true

  try {
    const updateData = {
      id: user.value.id,
      nickname: formData.value.nickname,
      email: formData.value.email || '',
      phone: formData.value.phone || '',
      image: imageFile.value || formData.value.image || '',
      intro: formData.value.intro || '',
    }

    const result = await userStore.updateProfile(updateData as any)

    if (result.success) {
      success('资料已成功更新')
      imageFile.value = null
      isEditing.value = false
      // 清理 object URL
      if (previewUrl.value) {
        URL.revokeObjectURL(previewUrl.value)
        previewUrl.value = ''
      }
    } else {
      error(result.message)
    }
  } catch (err: any) {
    console.error('更新失败:', err)
    error(err.message || '更新失败，请重试')
  } finally {
    isSaving.value = false
  }
}

// 初始化
onMounted(async () => {
  if (!userStore.isLoggedIn) {
    router.push('/')
    return
  }

  try {
    await userStore.getUserProfile()
  } catch (err) {
    console.warn('获取用户信息失败:', err)
  }
  
  isLoading.value = false
})
</script>

<style scoped>
</style>
