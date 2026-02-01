import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'HomeView',
    component: () => import('@/views/HomeView.vue') // 懒加载首页组件
  },
  // 可以添加更多路由配置
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router