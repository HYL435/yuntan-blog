<script setup lang="ts" name="NotificationCard">
import { ref, onMounted } from 'vue'

export interface NotificationProps {
  type?: 'success' | 'error' | 'warning' | 'info'
  title: string
  message?: string
  duration?: number // 毫秒，0 表示不自动关闭
}

const props = withDefaults(defineProps<NotificationProps>(), {
  type: 'success',
  duration: 3000
})

const emit = defineEmits<{
  close: []
}>()

const isVisible = ref(true)

onMounted(() => {
  if (props.duration > 0) {
    const timer = setTimeout(() => {
      isVisible.value = false
      setTimeout(() => {
        emit('close')
      }, 300)
    }, props.duration)

    return () => clearTimeout(timer)
  }
})

const handleClose = () => {
  isVisible.value = false
  setTimeout(() => {
    emit('close')
  }, 300)
}

const getIcon = () => {
  const icons = {
    success: 'M369 209c9.4-9.4 9.4-24.6 0-33.9s-24.6-9.4-33.9 0l-111 111-47-47c-9.4-9.4-24.6-9.4-33.9 0s-9.4 24.6 0 33.9l64 64c9.4 9.4 24.6 9.4 33.9 0L369 209z M256 48a208 208 0 1 1 0 416 208 208 0 1 1 0-416zm0 464A256 256 0 1 0 256 0a256 256 0 1 0 0 512z',
    error: 'M256 48a208 208 0 1 1 0 416 208 208 0 1 1 0-416zm0 464A256 256 0 1 0 256 0a256 256 0 1 0 0 512zM175 175c-9.4 9.4-9.4 24.6 0 33.9l47 47-47 47c-9.4 9.4-9.4 24.6 0 33.9s24.6 9.4 33.9 0l47-47 47 47c9.4 9.4 24.6 9.4 33.9 0s9.4-24.6 0-33.9l-47-47 47-47c9.4-9.4 9.4-24.6 0-33.9s-24.6-9.4-33.9 0l-47 47-47-47c-9.4-9.4-24.6-9.4-33.9 0z',
    warning: 'M256 32c123.5 0 224 100.5 224 224s-100.5 224-224 224S32 379.5 32 256 132.5 32 256 32zm0 48C141.1 80 80 141.1 80 256s61.1 176 176 176 176-61.1 176-176-61.1-176-176-176zm0 32c-8.8 0-16 7.2-16 16v112c0 8.8 7.2 16 16 16s16-7.2 16-16V208c0-8.8-7.2-16-16-16zm0 224c-13.3 0-24 10.7-24 24s10.7 24 24 24 24-10.7 24-24-10.7-24-24-24z',
    info: 'M256 48a208 208 0 1 1 0 416 208 208 0 1 1 0-416zm0 464A256 256 0 1 0 256 0a256 256 0 1 0 0 512zM256 336a24 24 0 1 0 0 48 24 24 0 1 0 0-48zm0-120a32 32 0 1 0 0-64 32 32 0 1 0 0 64z'
  }
  return icons[props.type]
}
</script>

<template>
  <Transition name="notification-fade">
    <div v-if="isVisible" class="card" :data-type="type">
      <svg class="wave" viewBox="0 0 1440 320" xmlns="http://www.w3.org/2000/svg">
        <path
          d="M0,256L11.4,240C22.9,224,46,192,69,192C91.4,192,114,224,137,234.7C160,245,183,235,206,213.3C228.6,192,251,160,274,149.3C297.1,139,320,149,343,181.3C365.7,213,389,267,411,282.7C434.3,299,457,277,480,250.7C502.9,224,526,192,549,181.3C571.4,171,594,181,617,208C640,235,663,277,686,256C708.6,235,731,149,754,122.7C777.1,96,800,128,823,165.3C845.7,203,869,245,891,224C914.3,203,937,117,960,112C982.9,107,1006,181,1029,197.3C1051.4,213,1074,171,1097,144C1120,117,1143,107,1166,133.3C1188.6,160,1211,224,1234,218.7C1257.1,213,1280,139,1303,133.3C1325.7,128,1349,192,1371,192C1394.3,192,1417,128,1429,96L1440,64L1440,320L1428.6,320C1417.1,320,1394,320,1371,320C1348.6,320,1326,320,1303,320C1280,320,1257,320,1234,320C1211.4,320,1189,320,1166,320C1142.9,320,1120,320,1097,320C1074.3,320,1051,320,1029,320C1005.7,320,983,320,960,320C937.1,320,914,320,891,320C868.6,320,846,320,823,320C800,320,777,320,754,320C731.4,320,709,320,686,320C662.9,320,640,320,617,320C594.3,320,571,320,549,320C525.7,320,503,320,480,320C457.1,320,434,320,411,320C388.6,320,366,320,343,320C320,320,297,320,274,320C251.4,320,229,320,206,320C182.9,320,160,320,137,320C114.3,320,91,320,69,320C45.7,320,23,320,11,320L0,320Z"
          fill-opacity="1"
        ></path>
      </svg>

      <div class="icon-container">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          viewBox="0 0 512 512"
          stroke-width="0"
          fill="currentColor"
          stroke="currentColor"
          class="icon"
        >
          <path :d="getIcon()"></path>
        </svg>
      </div>

      <div class="message-text-container">
        <p class="message-text">{{ title }}</p>
        <p v-if="message" class="sub-text">{{ message }}</p>
      </div>

      <svg
        xmlns="http://www.w3.org/2000/svg"
        viewBox="0 0 15 15"
        stroke-width="0"
        fill="none"
        stroke="currentColor"
        class="cross-icon"
        @click="handleClose"
      >
        <path
          fill="currentColor"
          d="M11.7816 4.03157C12.0062 3.80702 12.0062 3.44295 11.7816 3.2184C11.5571 2.99385 11.193 2.99385 10.9685 3.2184L7.50005 6.68682L4.03164 3.2184C3.80708 2.99385 3.44301 2.99385 3.21846 3.2184C2.99391 3.44295 2.99391 3.80702 3.21846 4.03157L6.68688 7.49999L3.21846 10.9684C2.99391 11.193 2.99391 11.557 3.21846 11.7816C3.44301 12.0061 3.80708 12.0061 4.03164 11.7816L7.50005 8.31316L10.9685 11.7816C11.193 12.0061 11.5571 12.0061 11.7816 11.7816C12.0062 11.557 12.0062 11.193 11.7816 10.9684L8.31322 7.49999L11.7816 4.03157Z"
          clip-rule="evenodd"
          fill-rule="evenodd"
        ></path>
      </svg>
    </div>
  </Transition>
</template>

<style scoped>
.card {
  width: 330px;
  height: auto;
  min-height: 80px;
  border-radius: 8px;
  box-sizing: border-box;
  padding: 15px;
  background-color: #ffffff;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: space-around;
  gap: 15px;
  transition: all 0.3s ease;
  
  /* 浅色模式 - 成功（默认） */
  --wave-fill: #34d39920;
  --icon-color: #22c55e;
  --icon-bg: #dcfce7;
  --text-color: #15803d;
  --subtext-color: #71717a;
}

:global(html.dark) .card {
  background-color: #1f232a;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.45);
  
  /* 深色模式 - 成功（默认） */
  --wave-fill: #95de6426;
  --icon-color: #95de64;
  --icon-bg: rgba(149, 222, 100, 0.18);
  --text-color: #b7eb8f;
  --subtext-color: #cfcfcf;
}

/* 错误类型 - 浅色 */
.card[data-type='error'] {
  --wave-fill: #f4338420;
  --icon-color: #f43f5e;
  --icon-bg: #ffe4e6;
  --text-color: #be123c;
  --subtext-color: #71717a;
}

/* 错误类型 - 深色 */
:global(html.dark) .card[data-type='error'] {
  --wave-fill: #ff787526;
  --icon-color: #ff7875;
  --icon-bg: rgba(255, 120, 117, 0.18);
  --text-color: #ff9c9c;
  --subtext-color: #cfcfcf;
}

/* 警告类型 - 浅色 */
.card[data-type='warning'] {
  --wave-fill: #fb923c20;
  --icon-color: #fb923c;
  --icon-bg: #ffedd5;
  --text-color: #c2410c;
  --subtext-color: #71717a;
}

/* 警告类型 - 深色 */
:global(html.dark) .card[data-type='warning'] {
  --wave-fill: #ffc53d26;
  --icon-color: #ffc53d;
  --icon-bg: rgba(255, 197, 61, 0.18);
  --text-color: #ffd666;
  --subtext-color: #cfcfcf;
}

/* 信息类型 - 浅色 */
.card[data-type='info'] {
  --wave-fill: #8b5cf620;
  --icon-color: #8b5cf6;
  --icon-bg: #ede9fe;
  --text-color: #6d28d9;
  --subtext-color: #71717a;
}

/* 信息类型 - 深色 */
:global(html.dark) .card[data-type='info'] {
  --wave-fill: #69b1ff26;
  --icon-color: #69b1ff;
  --icon-bg: rgba(105, 177, 255, 0.18);
  --text-color: #91caff;
  --subtext-color: #cfcfcf;
}

.wave {
  position: absolute;
  transform: rotate(90deg);
  left: -31px;
  top: 32px;
  width: 80px;
  fill: var(--wave-fill);
  opacity: 0.6;
}

.icon-container {
  width: 40px;
  height: 40px;
  min-width: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: var(--icon-bg);
  border-radius: 50%;
}

.icon {
  width: 20px;
  height: 20px;
  color: var(--icon-color);
}

.message-text-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-start;
  flex-grow: 1;
  min-width: 0;
}

.message-text,
.sub-text {
  margin: 0;
  cursor: default;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.message-text {
  color: var(--text-color);
  font-size: 15px;
  font-weight: 700;
  line-height: 1.2;
}

.sub-text {
  font-size: 13px;
  color: var(--subtext-color);
  margin-top: 4px;
}

.cross-icon {
  width: 18px;
  height: 18px;
  min-width: 18px;
  color: var(--subtext-color);
  cursor: pointer;
  transition: opacity 0.2s;
}

.cross-icon:hover {
  opacity: 0.7;
}

/* 动画 */
.notification-fade-enter-active,
.notification-fade-leave-active {
  transition: all 0.3s ease;
}

.notification-fade-enter-from {
  opacity: 0;
  transform: translateY(-20px);
}

.notification-fade-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}
</style>
