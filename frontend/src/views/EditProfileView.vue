<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900 py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-4xl mx-auto">
      <!-- 返回按钮 -->
      <router-link
        to="/profile"
        class="inline-flex items-center gap-2 mb-10 text-indigo-600 dark:text-indigo-400 hover:text-indigo-700 dark:hover:text-indigo-300 transition-colors duration-200"
      >
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
        </svg>
        <span class="font-medium">返回资料页</span>
      </router-link>

      <!-- 编辑卡片 -->
      <div class="bg-white dark:bg-gray-800 rounded-3xl shadow-2xl overflow-hidden ring-1 ring-black/5 dark:ring-white/10">
        <!-- 头部渐变 -->
        <div class="bg-linear-to-r from-purple-600 via-indigo-600 to-pink-600 px-8 py-14 text-center">
          <h1 class="text-4xl font-bold text-white">编辑个人资料</h1>
          <p class="mt-3 text-purple-100 text-lg">更新你的个人信息，让别人更好地认识你</p>
        </div>

        <!-- 表单区域 -->
        <form @submit.prevent="submitUpdate" class="px-8 pt-10 pb-12 space-y-10">
          <!-- 昵称 -->
          <div>
            <label class="block text-sm font-semibold text-gray-900 dark:text-gray-100 mb-3">昵称</label>
            <input
              v-model="formData.nickname"
              type="text"
              maxlength="50"
              placeholder="请输入昵称"
              class="w-full px-5 py-4 rounded-2xl border-2 border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-white placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:border-indigo-500 dark:focus:border-indigo-400 focus:ring-4 focus:ring-indigo-500/20 dark:focus:ring-indigo-400/20 transition-all duration-200"
            />
            <p class="mt-2 text-sm text-gray-500 dark:text-gray-400">{{ formData.nickname.length }}/50 个字符</p>
          </div>

          <!-- 邮箱 -->
          <div>
            <label class="block text-sm font-semibold text-gray-900 dark:text-gray-100 mb-3">邮箱</label>
            <input
              v-model="formData.email"
              type="email"
              placeholder="请输入邮箱地址"
              class="w-full px-5 py-4 rounded-2xl border-2 border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-white placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:border-indigo-500 dark:focus:border-indigo-400 focus:ring-4 focus:ring-indigo-500/20 dark:focus:ring-indigo-400/20 transition-all duration-200"
            />
            <p class="mt-2 text-sm text-gray-500 dark:text-gray-400">我们会严格保护你的邮箱隐私</p>
          </div>

          <!-- 手机号 -->
          <div>
            <label class="block text-sm font-semibold text-gray-900 dark:text-gray-100 mb-3">手机号</label>
            <input
              v-model="formData.phone"
              type="tel"
              placeholder="请输入手机号"
              class="w-full px-5 py-4 rounded-2xl border-2 border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-white placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:border-indigo-500 dark:focus:border-indigo-400 focus:ring-4 focus:ring-indigo-500/20 dark:focus:ring-indigo-400/20 transition-all duration-200"
            />
            <p class="mt-2 text-sm text-gray-500 dark:text-gray-400">可选，不认布绘</p>
          </div>

          <!-- 头像上传 -->
          <div>
            <label class="block text-sm font-semibold text-gray-900 dark:text-gray-100 mb-3">头像上传</label>
            <div class="flex items-center gap-4">
              <label class="relative flex-1">
                <div class="px-5 py-4 rounded-2xl border-2 border-dashed border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 hover:border-indigo-500 dark:hover:border-indigo-400 transition-colors duration-200 cursor-pointer text-center">
                  <div class="flex flex-col items-center">
                    <svg class="w-8 h-8 text-gray-400 mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
                    </svg>
                    <p class="text-sm font-medium text-gray-600 dark:text-gray-300">
                      {{ imageFile ? '已选择文件' : '点击选择图片' }}
                    </p>
                    <p class="text-xs text-gray-500 dark:text-gray-400 mt-1">支持 JPG、PNG、GIF（最大 10MB）</p>
                  </div>
                </div>
                <input
                  type="file"
                  accept="image/*"
                  class="hidden"
                  @change="handleImageSelect"
                />
              </label>
              <!-- 清除按钮 -->
              <button
                v-if="imageFile"
                type="button"
                @click="clearImageFile"
                class="px-4 py-4 bg-red-50 dark:bg-red-900/30 text-red-600 dark:text-red-400 rounded-2xl hover:bg-red-100 dark:hover:bg-red-900/50 transition-colors font-medium"
              >
                清除
              </button>
            </div>
            <p class="mt-2 text-sm text-gray-500 dark:text-gray-400">
              {{ imageFile ? `已选择: ${imageFile.name}` : '不选择文件则保持当前头像' }}
            </p>
          </div>

          <!-- 个人简介 -->
          <div>
            <label class="block text-sm font-semibold text-gray-900 dark:text-gray-100 mb-3">个人简介</label>
            <textarea
              v-model="formData.intro"
              rows="4"
              maxlength="200"
              placeholder="请输入个人简介（最大200个字符）"
              class="w-full px-5 py-4 rounded-2xl border-2 border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-white placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:border-indigo-500 dark:focus:border-indigo-400 focus:ring-4 focus:ring-indigo-500/20 dark:focus:ring-indigo-400/20 transition-all duration-200 resize-none"
            />
            <p class="mt-2 text-sm text-gray-500 dark:text-gray-400">{{ formData.intro.length }}/200 个字符</p>
          </div>

          <!-- 头像预览 -->
          <div v-if="previewUrl || formData.image || imageError" class="p-8 bg-gray-50 dark:bg-gray-700/50 rounded-3xl text-center">
            <p class="text-sm font-medium text-gray-600 dark:text-gray-400 mb-6">头像预览</p>
            <div class="inline-block relative">
              <img
                :src="previewUrl || formData.image || placeholderAvatar"
                :alt="formData.nickname || '头像预览'"
                @error="handleImageError"
                class="w-40 h-40 rounded-full object-cover shadow-2xl border-8 border-white dark:border-gray-800 ring-4 ring-indigo-500/20"
              />
              <div v-if="imageError" class="absolute inset-0 flex items-center justify-center bg-gray-200 dark:bg-gray-600 rounded-full">
                <svg class="w-12 h-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 6h.01" />
                  <circle cx="12" cy="12" r="10" stroke-width="2" />
                </svg>
              </div>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="flex gap-4 pt-6">
            <router-link to="/profile" class="flex-1">
              <button
                type="button"
                class="w-full px-6 py-4 bg-gray-200 dark:bg-gray-700 hover:bg-gray-300 dark:hover:bg-gray-600 text-gray-800 dark:text-gray-100 font-semibold rounded-2xl shadow-md hover:shadow-lg transition-all duration-200"
              >
                取消
              </button>
            </router-link>
            <button
              type="submit"
              :disabled="isSaving"
              class="flex-1 px-6 py-4 bg-linear-to-r from-indigo-600 to-purple-600 hover:from-indigo-700 hover:to-purple-700 disabled:opacity-50 disabled:cursor-not-allowed text-white font-semibold rounded-2xl shadow-md hover:shadow-lg transition-all duration-200"
            >
              {{ isSaving ? '保存中...' : '保存更改' }}
            </button>
          </div>

          <!-- 消息提示 -->
          <Transition name="fade">
            <div
              v-if="message"
              :class="[
                'p-5 rounded-2xl text-center font-medium text-lg shadow-md',
                messageType === 'success'
                  ? 'bg-green-50 dark:bg-green-900/30 text-green-700 dark:text-green-300 border border-green-200 dark:border-green-800'
                  : 'bg-red-50 dark:bg-red-900/30 text-red-700 dark:text-red-300 border border-red-200 dark:border-red-800'
              ]"
            >
              {{ message }}
            </div>
          </Transition>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="EditProfileView">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const isSaving = ref(false)
const message = ref('')
const messageType = ref<'success' | 'error'>('success')
const imageError = ref(false)
const imageFile = ref<File | null>(null)
const previewUrl = ref('')

// 默认占位头像（可替换为自己的 CDN 链接）
const placeholderAvatar = 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?ixlib=rb-4.0.3&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80'

const formData = ref({
  nickname: '',
  email: '',
  phone: '',
  image: '',
  intro: '',
})

const user = computed(() => userStore.user)

const handleImageError = () => {
  imageError.value = true
  message.value = '头像加载失败，请检查链接是否正确'
  messageType.value = 'error'
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
    message.value = '请选择有效的图片文件'
    messageType.value = 'error'
    input.value = ''
    return
  }

  // 验证文件大小（最大 10MB）
  if (file.size > 10 * 1024 * 1024) {
    message.value = '文件大小不能超过 10MB'
    messageType.value = 'error'
    input.value = ''
    return
  }

  imageFile.value = file
  imageError.value = false

  // 创建预览 URL
  previewUrl.value = URL.createObjectURL(file)
  message.value = ''
}

const clearImageFile = () => {
  imageFile.value = null
  previewUrl.value = ''
  message.value = ''
}

onMounted(() => {
  if (!userStore.isLoggedIn) {
    router.push('/')
    return
  }

  if (user.value) {
    formData.value.nickname = user.value.nickname || ''
    formData.value.email = user.value.email || ''
    formData.value.phone = user.value.phone || ''
    formData.value.image = user.value.image || ''
    formData.value.intro = user.value.intro || ''
  }
})

const submitUpdate = async () => {
  if (!formData.value.nickname.trim()) {
    message.value = '昵称不能为空'
    messageType.value = 'error'
    return
  }

  // 验证用户信息
  if (!user.value || !user.value.id) {
    message.value = '用户信息丢失，请重新登录'
    messageType.value = 'error'
    return
  }

  isSaving.value = true
  message.value = ''

  try {
    const updateData = {
      id: user.value.id,
      nickname: formData.value.nickname.trim(),
      email: formData.value.email || '',
      phone: formData.value.phone || '',
      image: imageFile.value || formData.value.image || '', // 优先使用选中的文件，否则使用当前 URL
      intro: formData.value.intro || '',
    }

    const result = await userStore.updateProfile(updateData as any)

    if (result.success) {
      message.value = '资料已成功更新'
      messageType.value = 'success'
      // 清理 object URL
      if (previewUrl.value) {
        URL.revokeObjectURL(previewUrl.value)
      }
      setTimeout(() => {
        router.push('/profile')
      }, 1500)
    } else {
      message.value = result.message || '更新失败'
      messageType.value = 'error'
    }
  } catch (error: any) {
    console.error('更新失败:', error)
    message.value = error.message || '更新失败，请重试'
    messageType.value = 'error'
  } finally {
    isSaving.value = false
  }
}
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: all 0.4s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(-12px);
}
</style>