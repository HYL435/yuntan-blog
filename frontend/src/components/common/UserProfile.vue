<script setup lang="ts">
import { useUserStore } from '@/stores/user'
import { useNotification } from '@/composables/useNotification'
import { useRouter } from 'vue-router'
import LoginButton from './LoginButton.vue'
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { logoutApi } from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()
const { success } = useNotification()
const isDropdownOpen = ref(false)
const imageLoadError = ref(false)
const dropdownContainer = ref<HTMLElement | null>(null)

// 处理图片 URL - 如果是相对路径则拼接后端地址
const getImageUrl = (imageUrl: string | null | undefined): string | null => {
  if (!imageUrl) return null
  
  // 如果已经是完整 URL（http:// 或 https://），直接使用
  if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
    return imageUrl
  }
  
  // 如果是相对路径，拼接后端地址
  const backendUrl = ((import.meta.env as any).VITE_API_URL || 'https://yuntan-blog.top').replace(/\/$/, '')
  return `${backendUrl}${imageUrl.startsWith('/') ? '' : '/'}${imageUrl}`
}

const userImageUrl = computed(() => {
  if (imageLoadError.value) return null
  return getImageUrl(userStore.user?.image)
})

const handleImageError = () => {
  imageLoadError.value = true
}

const handleLogout = async () => {
  try {
    // 调用后端退出登录接口
    const response = await logoutApi()
    
    if (response.data.code === 200) {
      success('退出登录成功')
    }
  } catch (error) {
    console.warn('退出登录接口调用失败:', error)
    // 即使后端接口失败，仍继续清除本地数据
  } finally {
    // 清除本地登录状态和数据
    userStore.logout()
    isDropdownOpen.value = false
    
    // 退出登录后继续留在当前页面，不跳转
  }
}

const handleProfileClick = () => {
  console.log('导航到 /profile')
  isDropdownOpen.value = false
  router.push('/profile').catch(err => {
    console.error('导航到 /profile 失败:', err)
  })
}

const handleSettingsClick = () => {
  console.log('导航到 /settings')
  isDropdownOpen.value = false
  router.push('/settings').catch(err => {
    console.error('导航到 /settings 失败:', err)
  })
}

const toggleDropdown = () => {
  console.log('切换下拉菜单:', !isDropdownOpen.value)
  isDropdownOpen.value = !isDropdownOpen.value
}

// 点击外部关闭下拉框
const handleClickOutside = (event: MouseEvent) => {
  if (!isDropdownOpen.value) return
  if (dropdownContainer.value && !dropdownContainer.value.contains(event.target as Node)) {
    isDropdownOpen.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<template>
  <div>
    <!-- 已登录状态 -->
    <div v-if="userStore.isLoggedIn" class="relative">
      <!-- 用户头像 -->
      <button
        @click.stop="toggleDropdown"
        class="hover:opacity-80 transition-opacity"
        :title="userStore.user?.nickname"
      >
        <!-- 头像 -->
        <img
          v-if="userImageUrl"
          :src="userImageUrl"
          :alt="userStore.user?.nickname"
          @error="handleImageError"
          class="w-10 h-10 rounded-full object-cover"
        />
        <div
          v-else
          class="w-10 h-10 rounded-full bg-transparent flex items-center justify-center text-white font-bold text-sm"
        >
          {{ userStore.user?.nickname?.charAt(0) || 'U' }}
        </div>
      </button>

      <!-- 下拉菜单 -->
      <Transition name="dropdown-fade">
        <div
          v-if="isDropdownOpen"
          class="dropdown-card absolute right-0 mt-3 z-50"
          ref="dropdownContainer"
          @click.stop
        >
          <!-- 用户信息卡片 -->
          <div class="user-info-panel">
            <div class="user-info-content">
              <img
                v-if="userImageUrl"
                :src="userImageUrl"
                :alt="userStore.user?.nickname"
                @error="handleImageError"
                class="user-avatar"
              />
              <div
                v-else
                class="user-avatar-fallback"
              >
                {{ userStore.user?.nickname?.charAt(0) || 'U' }}
              </div>
              <div class="user-text">
                <p class="user-nickname">{{ userStore.user?.nickname }}</p>
              </div>
            </div>
          </div>

          <div class="separator"></div>

          <!-- 菜单项 -->
          <ul class="list">
            <li>
              <button 
                type="button"
                class="element" 
                @click="handleProfileClick"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="24"
                  height="24"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                >
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                  <circle cx="12" cy="7" r="4"></circle>
                </svg>
                <p class="label">个人资料</p>
              </button>
            </li>

            <li>
              <button 
                type="button"
                class="element" 
                @click="handleSettingsClick"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="24"
                  height="24"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                >
                  <circle cx="12" cy="12" r="3"></circle>
                  <path d="M12 1v6m0 6v6M4.22 4.22l4.24 4.24m5.08 5.08l4.24 4.24M1 12h6m6 0h6M4.22 19.78l4.24-4.24m5.08-5.08l4.24-4.24"></path>
                </svg>
                <p class="label">设置</p>
              </button>
            </li>
          </ul>

        <div class="separator"></div>

        <!-- 登出按钮 -->
        <ul class="list">
          <li class="element delete" @click="handleLogout">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="24"
              height="24"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
              <polyline points="16 17 21 12 16 7"></polyline>
              <line x1="21" y1="12" x2="9" y2="12"></line>
            </svg>
            <p class="label">登出</p>
          </li>
        </ul>
        </div>
      </Transition>
    </div>

    <!-- 未登录状态 -->
    <LoginButton v-else />
  </div>
</template>

<style scoped>
/* 头像按钮样式 */
button {
  background: transparent;
  border: none;
  padding: 0;
  cursor: pointer;
}

/* 下拉框过渡动画 */
.dropdown-fade-enter-active,
.dropdown-fade-leave-active {
  transition: all 0.2s ease-out;
}

.dropdown-fade-enter-from {
  opacity: 0;
  transform: translateY(-8px);
}

.dropdown-fade-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

/* 浅色模式作为默认 */
.dropdown-card {
  width: 220px;
  background-color: #f8fafc;
  background-image: none;
  border-radius: 10px;
  padding: 15px 0px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.08);
}

.user-info-panel {
  padding: 0px 15px;
}

.user-info-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.user-avatar-fallback {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: transparent;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #334155;
  font-weight: bold;
  font-size: 14px;
}

.user-text {
  flex: 1;
  overflow: hidden;
}

.user-nickname {
  font-weight: 600;
  font-size: 14px;
  color: #334155;
  margin: 0;
}

.user-id {
  font-size: 12px;
  color: #64748b;
  margin: 0;
}

.dropdown-card .separator {
  border-top: 1.5px solid #cbd5e1;
}

.dropdown-card .list {
  list-style-type: none;
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 0px 10px;
  margin: 0;
}

.dropdown-card .list .element {
  display: flex;
  align-items: center;
  color: #334155;
  gap: 10px;
  transition: all 0.3s ease-out;
  padding: 8px 10px;
  border-radius: 6px;
  cursor: pointer;
  border: none;
  background: none;
  width: 100%;
  text-align: left;
}

.dropdown-card .list .element svg {
  width: 18px;
  height: 18px;
  transition: all 0.3s ease-out;
  flex-shrink: 0;
  stroke: #64748b;
}

.dropdown-card .list .element .label {
  font-weight: 500;
  font-size: 14px;
  margin: 0;
}

.dropdown-card .list .element:hover {
  background-color: #5ba874;
  color: #ffffff;
  transform: translate(1px, -1px);
}

.dropdown-card .list .element:hover svg {
  stroke: #ffffff;
}

.dropdown-card .list .element:active {
  transform: scale(0.99);
}

.dropdown-card .list .element.delete {
  color: #dc2626;
}

.dropdown-card .list .element.delete svg {
  stroke: #dc2626;
}

.dropdown-card .list .element.delete:hover {
  background-color: #dc2626;
  color: #ffffff;
}

.dropdown-card .list .element.delete:hover svg {
  stroke: #ffffff;
}
</style>

<!-- 深色模式全局样式 -->
<style>
html.dark .dropdown-card,
html.dark-mode .dropdown-card {
  background-color: rgba(36, 40, 50, 1) !important;
  background-image: linear-gradient(
    139deg,
    rgba(36, 40, 50, 1) 0%,
    rgba(36, 40, 50, 1) 0%,
    rgba(37, 28, 40, 1) 100%
  ) !important;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.3) !important;
}

html.dark .dropdown-card .separator,
html.dark-mode .dropdown-card .separator {
  border-top: 1.5px solid #42434a !important;
}

html.dark .user-nickname,
html.dark-mode .user-nickname {
  color: #ffffff !important;
}

html.dark .user-id,
html.dark-mode .user-id {
  color: #7e8590 !important;
}

html.dark .user-avatar-fallback,
html.dark-mode .user-avatar-fallback {
  color: white !important;
}

html.dark .dropdown-card .list .element,
html.dark-mode .dropdown-card .list .element {
  color: #7e8590 !important;
}

html.dark .dropdown-card .list .element svg,
html.dark-mode .dropdown-card .list .element svg {
  stroke: #7e8590 !important;
}

html.dark .dropdown-card .list .element:hover,
html.dark-mode .dropdown-card .list .element:hover {
  background-color: #5353ff !important;
  color: #ffffff !important;
}

html.dark .dropdown-card .list .element:hover svg,
html.dark-mode .dropdown-card .list .element:hover svg {
  stroke: #ffffff !important;
}

html.dark .dropdown-card .list .element.delete,
html.dark-mode .dropdown-card .list .element.delete {
  color: #e74c3c !important;
}

html.dark .dropdown-card .list .element.delete svg,
html.dark-mode .dropdown-card .list .element.delete svg {
  stroke: #e74c3c !important;
}

html.dark .dropdown-card .list .element.delete:hover,
html.dark-mode .dropdown-card .list .element.delete:hover {
  background-color: #8e2a2a !important;
  color: #ffffff !important;
}

html.dark .dropdown-card .list .element.delete:hover svg,
html.dark-mode .dropdown-card .list .element.delete:hover svg {
  stroke: #ffffff !important;
}
</style>
