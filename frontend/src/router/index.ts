import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'HomeView',
    component: () => import('@/views/HomeView.vue') // 懒加载首页组件
  },
  {
    path: '/login',
    name: 'LoginView',
    component: () => import('@/views/LoginView.vue')
  },
  {
    path: '/register',
    name: 'RegisterView',
    component: () => import('@/views/RegisterView.vue')
  },
  {
    path: '/article/:id',
    name: 'ArticleDetailView',
    component: () => import('@/views/ArticleDetailView.vue')
  },
  {
    path: '/tag/:name',
    name: 'TagView',
    component: () => import('@/views/TagView.vue')
  },
  // 可以添加更多路由配置
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router