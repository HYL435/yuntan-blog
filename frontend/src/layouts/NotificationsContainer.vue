<script setup lang="ts">
import { useNotification } from '@/composables/useNotification'
import NotificationCard from '@/components/common/NotificationCard.vue'

const { notifications, remove } = useNotification()
</script>

<template>
  <div class="notifications-container">
    <div v-for="notification in notifications" :key="notification.id" class="notification-item">
      <NotificationCard
        :type="notification.type"
        :title="notification.title"
        :message="notification.message"
        :duration="notification.duration"
        @close="() => remove(notification.id)"
      />
    </div>
  </div>
</template>

<style scoped>
.notifications-container {
  position: fixed;
  top: 20px;
  left: 20px;
  z-index: 9999;
  display: flex;
  flex-direction: column;
  gap: 12px;
  pointer-events: none;
}

.notification-item {
  pointer-events: auto;
  animation: slideIn 0.3s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-100px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@media (max-width: 768px) {
  .notifications-container {
    top: 10px;
    left: 10px;
  }

  .notification-item {
    max-width: 100%;
  }
}
</style>
