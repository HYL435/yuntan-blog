<template>
  <div class="login-page">
    <div class="auth-wrap">
      <h1 class="page-title">找回密码</h1>

      <form class="form" @submit.prevent="handleSubmit">
        <!-- 邮箱输入 -->
        <div class="flex-column">
          <label>注册邮箱</label>
        </div>
        <div class="inputForm">
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="u-icon"><rect width="20" height="16" x="2" y="4" rx="2"/><path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7"/></svg>
          <input v-model.trim="email" type="email" class="input" placeholder="请输入注册邮箱" />
        </div>

        <!-- 验证码输入 (核心优化部分) -->
        <div class="flex-column mt-3">
          <label>验证码</label>
        </div>
        <div class="inputForm verify-code-wrapper">
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="u-icon"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10"/><path d="m9 12 2 2 4-4"/></svg>
          
          <input 
            v-model.trim="code" 
            type="text" 
            maxlength="6" 
            class="input code-input" 
            placeholder="6位数字验证码" 
          />
          
          <button 
            type="button" 
            class="verify-btn" 
            :disabled="sending || countdown > 0 || !email" 
            :class="{ 'is-disabled': sending || countdown > 0 || !email }"
            @click="sendCode"
          >
            <span v-if="sending">发送中...</span>
            <span v-else-if="countdown > 0" class="countdown-text">{{ countdown }}s 后重发</span>
            <span v-else>获取验证码</span>
          </button>
        </div>

        <!-- 新密码输入 -->
        <div class="flex-column mt-3">
          <label>新密码</label>
        </div>
        <div class="inputForm">
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="u-icon"><rect width="18" height="11" x="3" y="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
          <input v-model.trim="newPassword" type="password" class="input" placeholder="8-20位，包含字母和数字" />
        </div>

        <!-- 消息提示 -->
        <p v-if="message" class="message-text" :class="messageType">
          {{ message }}
        </p>

        <!-- 提交按钮 -->
        <button class="button-submit" type="submit" :disabled="loading">
          {{ loading ? '重置中...' : '确认重置密码' }}
        </button>

        <p class="register-text">
          想起来了？<RouterLink class="register-link" to="/login">返回登录</RouterLink>
        </p>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { sendResetCodeApi, resetPasswordApi } from '@/api/auth'
import { isValidEmail, isValidPassword } from '@/utils/validators'
import { useNotification } from '@/composables/useNotification' // 假设你有这个hook

const router = useRouter()
const { success, error, warning } = useNotification() // 增加 warning

const email = ref('')
const code = ref('')
const newPassword = ref('')
const loading = ref(false)
const sending = ref(false)
const countdown = ref(0)
const message = ref('')
const messageType = ref<'info' | 'error' | 'success'>('info')

let timer: ReturnType<typeof setInterval> | null = null

// 开始倒计时
const startCountdown = (sec = 60) => {
  countdown.value = sec
  if (timer) clearInterval(timer)
  
  timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      if (timer) clearInterval(timer)
      timer = null
    }
  }, 1000)
}

// 发送验证码
const sendCode = async () => {
  message.value = ''
  
  // 1. 基础校验
  if (!email.value) {
    messageType.value = 'error'
    message.value = '请先输入邮箱地址'
    warning('请先输入邮箱地址')
    return
  }
  if (!isValidEmail(email.value)) {
    messageType.value = 'error'
    message.value = '请输入有效的邮箱格式'
    return
  }
  
  // 2. 防止重复点击
  if (sending.value || countdown.value > 0) return

  sending.value = true
  try {
    const res = await sendResetCodeApi(email.value) // 假设接口参数是 { email: xxx } 或直接传 string
    if (res.data && (res.data.code === 200 || res.status === 200)) {
      success('验证码已发送到您的邮箱')
      messageType.value = 'success'
      message.value = '验证码已发送，请查收'
      startCountdown(60)
    } else {
      throw new Error(res.data?.message || '发送失败')
    }
  } catch (err: any) {
    messageType.value = 'error'
    message.value = err?.response?.data?.message || err?.message || '验证码发送失败，请稍后重试'
    error(message.value)
  } finally {
    sending.value = false
  }
}

// 提交重置
const handleSubmit = async () => {
  message.value = ''
  
  if (!isValidEmail(email.value)) {
    messageType.value = 'error'
    message.value = '请输入有效的邮箱地址'
    return
  }
  if (!/^\d{6}$/.test(code.value)) {
    messageType.value = 'error'
    message.value = '验证码必须是6位数字'
    return
  }
  if (!isValidPassword(newPassword.value)) {
    messageType.value = 'error'
    message.value = '新密码格式不正确（8-20位，含字母数字）'
    return
  }

  loading.value = true
  try {
    // 根据你的后端接口调整参数结构
    const res = await resetPasswordApi({ 
      email: email.value, 
      code: code.value, 
      newPassword: newPassword.value 
    })
    
    if (res.data && (res.data.code === 200 || res.status === 200)) {
      messageType.value = 'success'
      message.value = '密码重置成功，即将跳转登录...'
      success('密码重置成功')
      setTimeout(() => router.push('/login'), 2000)
    } else {
      throw new Error(res.data?.message || '重置失败')
    }
  } catch (err: any) {
    messageType.value = 'error'
    message.value = err?.response?.data?.message || err?.message || '重置失败，请检查验证码'
    error(message.value)
  } finally {
    loading.value = false
  }
}

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
/* 这里尽量复用你的 login-shared.css，只添加针对性的样式 */
@import "./login-shared.css"; 

/* 如果 inputForm 是 flex 布局，我们需要确保它能容纳内部元素 */
.inputForm {
  display: flex;
  align-items: center;
  position: relative;
  /* 假设 inputForm 有 border, border-radius, background 等基础样式 */
  /* 如果没有，请在这里补上，例如：
  border: 1.5px solid #ecedec;
  border-radius: 10px;
  height: 50px;
  padding: 0 15px;
  */
}

.u-icon {
  margin-right: 10px;
  color: #888;
}

.input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  height: 100%;
}

/* --- 验证码输入框特定样式 --- */
.verify-code-wrapper {
  padding-right: 5px; /* 右侧留点空隙给按钮 */
}

.code-input {
  /* 防止输入文字盖住按钮 */
  padding-right: 10px; 
}

/* --- 发送验证码按钮样式 --- */
.verify-btn {
  border: none;
  background-color: transparent; /* 透明背景，显得更轻量 */
  color: #7b4bf7; /* 主题色，根据你的设计调整 */
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 6px;
  transition: all 0.3s ease;
  white-space: nowrap; /* 防止文字换行 */
  min-width: 90px;     /* 保证倒计时宽度不跳动 */
  text-align: center;
}

.verify-btn:hover:not(.is-disabled) {
  background-color: rgba(123, 75, 247, 0.1); /* 鼠标悬停时的淡背景 */
}

.verify-btn:active:not(.is-disabled) {
  transform: scale(0.96);
}

/* 禁用状态（倒计时中、邮箱为空、发送中） */
.verify-btn.is-disabled {
  color: #a0a0a0;
  cursor: not-allowed;
  background-color: transparent;
}

.countdown-text {
  color: #999;
  font-family: monospace; /* 等宽字体防止倒计时数字跳动 */
}

/* --- 消息提示样式 --- */
.message-text {
  margin-top: 10px;
  font-size: 13px;
  text-align: center;
}
.message-text.error { color: #ef4444; }
.message-text.success { color: #10b981; }

.mt-3 {
  margin-top: 15px;
}
</style>