<template>
  <button
    v-if="showPortal"
    @click="onClick"
    class="fixed right-6 bottom-6 z-50 flex items-center justify-center w-14 h-14 rounded-full shadow-2xl border border-slate-200 dark:border-slate-700
           bg-white dark:bg-slate-800 text-slate-700 dark:text-slate-100 hover:scale-105 transform-gpu transition-transform duration-150"
    aria-label="传送门"
  >
    <!-- 简洁的传送门图标 -->
    <svg xmlns="http://www.w3.org/2000/svg" class="w-7 h-7" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
      <circle cx="12" cy="12" r="3.5" />
      <path d="M12 2v2" />
      <path d="M12 20v2" />
      <path d="M4.93 4.93l1.41 1.41" />
      <path d="M17.66 17.66l1.41 1.41" />
      <path d="M2 12h2" />
      <path d="M20 12h2" />
      <path d="M4.93 19.07l1.41-1.41" />
      <path d="M17.66 6.34l1.41-1.41" />
    </svg>
  </button>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const stripBearer = (t: string) => t.replace(/^Bearer\s+/i, '')

function parseJwtPayload(token: string): any | null {
  try {
    const parts = token.split('.')
    if (parts.length < 2) return null
    const payload = parts[1]
    // base64url -> base64
    const base64 = payload.replace(/-/g, '+').replace(/_/g, '/')
    const padded = base64 + '='.repeat((4 - (base64.length % 4)) % 4)
    const json = atob(padded)
    return JSON.parse(json)
  } catch (e) {
    return null
  }
}

const showPortal = computed(() => {
  // 优先使用 store.token，再 fallback 到 localStorage
  const raw = userStore?.token || (localStorage.getItem('auth_token') || '')
  if (!raw) return false
  const token = stripBearer(raw)
  const payload = parseJwtPayload(token)
  if (!payload) return false

  // 常见的角色字段名：role, roles, authority, authorities
  let roleVal: any = null
  if (payload.role !== undefined) roleVal = payload.role
  else if (payload.roles !== undefined) roleVal = payload.roles
  else if (payload.authority !== undefined) roleVal = payload.authority
  else if (payload.authorities !== undefined) roleVal = payload.authorities

  // 如果是数组，取第一个；如果对象尝试读取 .role
  if (Array.isArray(roleVal) && roleVal.length > 0) roleVal = roleVal[0]
  if (roleVal && typeof roleVal === 'object' && roleVal.role !== undefined) roleVal = roleVal.role

  const roleNum = Number(roleVal)
  if (Number.isNaN(roleNum)) return false
  return roleNum === 0 || roleNum === 1
})

const onClick = () => {
  router.push('/admin').catch(() => {})
}
</script>
