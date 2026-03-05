<template>
  <div class="common-layout h-screen w-full overflow-hidden flex bg-gray-50 dark:bg-[#0f172a] text-gray-700 dark:text-gray-200 transition-colors duration-300">
    <!-- 左侧菜单栏 -->
    <el-aside 
      :width="isCollapse ? '64px' : '240px'" 
      class="h-full bg-white dark:bg-[#1e293b] border-r border-gray-200 dark:border-gray-800 transition-all duration-300 flex flex-col"
    >
      <!-- Logo -->
      <div class="h-16 flex items-center justify-center border-b border-gray-100 dark:border-gray-700/50">
        <div class="flex items-center gap-2 font-bold text-xl text-primary truncate px-4">
          <div class="w-8 h-8 rounded bg-blue-600 flex items-center justify-center text-white shrink-0">
            <el-icon><Management /></el-icon>
          </div>
          <span v-show="!isCollapse" class="transition-opacity duration-300">后台管理</span>
        </div>
      </div>
      <!-- 菜单 -->
      <el-scrollbar class="flex-1">
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          class="border-none !bg-transparent py-2"
          text-color="#64748b"
          active-text-color="#2563eb"
          unique-opened
          router
        >
          <el-menu-item index="/admin/dashboard">
            <el-icon><Odometer /></el-icon>
            <template #title>仪表盘</template>
          </el-menu-item>
          <el-menu-item index="/admin/stats">
            <el-icon><DataAnalysis /></el-icon>
            <template #title>统计</template>
          </el-menu-item>
          <el-sub-menu index="content">
            <template #title>
              <el-icon><Document /></el-icon>
              <span>内容管理</span>
            </template>
            <el-menu-item index="/admin/articles">文章管理</el-menu-item>
            <el-menu-item index="/admin/categories">分类管理</el-menu-item>
            <el-menu-item index="/admin/tag">标签管理</el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="interaction">
            <template #title>
              <el-icon><ChatLineRound /></el-icon>
              <span>互动管理</span>
            </template>
            <el-menu-item index="/admin/comment">评论管理</el-menu-item>
            <el-menu-item index="/admin/guestbook">留言管理</el-menu-item>
          </el-sub-menu>
          <el-menu-item index="/admin/users">
            <el-icon><User /></el-icon>
            <template #title>用户管理</template>
          </el-menu-item>
          <el-menu-item index="/admin/settings">
            <el-icon><Setting /></el-icon>
            <template #title>系统设置</template>
          </el-menu-item>
        </el-menu>
      </el-scrollbar>
    </el-aside>

    <!-- 右侧主内容 -->
    <div class="flex-1 flex flex-col min-w-0">
      <!-- 顶部 Header -->
      <header class="h-16 bg-white dark:bg-[#1e293b] border-b border-gray-200 dark:border-gray-800 flex items-center justify-between px-6 transition-colors duration-300">
        <div class="flex items-center gap-4">
          <el-button circle text @click="isCollapse = !isCollapse">
            <el-icon :size="20"><component :is="isCollapse ? Expand : Fold" /></el-icon>
          </el-button>
          <el-breadcrumb separator="/" class="hidden md:flex">
            <el-breadcrumb-item>首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ breadcrumbName }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="flex items-center gap-4">
          <el-button type="primary" link @click="goFront">
            <el-icon class="mr-1"><HomeFilled /></el-icon> 返回前台
          </el-button>
          <el-divider direction="vertical" />
          <el-button circle text @click="toggleTheme">
            <el-icon :size="20" class="text-gray-600 dark:text-yellow-400">
              <component :is="isDark ? Sunny : Moon" />
            </el-icon>
          </el-button>
          <el-dropdown>
            <div class="flex items-center gap-2 cursor-pointer">
              <el-avatar :size="32" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
              <span class="text-sm font-medium hidden sm:block">管理员</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>个人中心</el-dropdown-item>
                <el-dropdown-item divided class="text-red-500" @click="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>
      <!-- 主内容区 -->
      <main class="flex-1 overflow-auto p-6 relative">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts" name="AdminLayout">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import {
  Management, Expand, Fold, HomeFilled, Odometer, Document, ChatLineRound, User, Setting, Sunny, Moon, DataAnalysis
} from '@element-plus/icons-vue'

const isCollapse = ref(false)
const isDark = ref(false)
const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const breadcrumbName = computed(() => {
  // 可根据 route.name 或 path 定制面包屑
    const map: Record<string, string> = {
    '/admin/dashboard': '仪表盘',
    '/admin/articles': '文章管理',
    '/admin/categories': '分类管理',
    '/admin/tag': '标签管理',
    '/admin/comment': '评论管理',
    '/admin/guestbook': '留言管理',
    '/admin/stats': '统计',
    '/admin/users': '用户管理',
    '/admin/settings': '系统设置'
  }
  return map[route.path] || '仪表盘'
})

const logout = () => {
  userStore.logout()
  router.push('/').catch(() => {})
}
const goFront = () => {
  router.push('/').catch(() => {})
}
const toggleTheme = () => {
  isDark.value = !isDark.value
  applyTheme()
}
const applyTheme = () => {
  const html = document.documentElement
  if (isDark.value) {
    html.classList.add('dark')
    localStorage.setItem('theme', 'dark')
  } else {
    html.classList.remove('dark')
    localStorage.setItem('theme', 'light')
  }
}
onMounted(() => {
  const savedTheme = localStorage.getItem('theme')
  const systemDark = window.matchMedia('(prefers-color-scheme: dark)').matches
  isDark.value = savedTheme === 'dark' || (!savedTheme && systemDark)
  applyTheme()
})
</script>

<style scoped>
/* 定义淡入动画 */
.animate-fade-in {
  animation: fadeIn 0.3s ease-out;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
