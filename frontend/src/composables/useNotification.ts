import { ref, onUnmounted } from 'vue'

export type NotificationType = 'success' | 'error' | 'warning' | 'info'

export interface Notification {
  id: string
  type: NotificationType
  title: string
  message?: string
  duration?: number
}

const notifications = ref<Notification[]>([])

let nextId = 0

export function useNotification() {
  const show = (
    title: string,
    options?: {
      type?: NotificationType
      message?: string
      duration?: number
    }
  ) => {
    const id = String(nextId++)
    const notification: Notification = {
      id,
      type: options?.type || 'info',
      title,
      message: options?.message,
      duration: options?.duration !== undefined ? options.duration : 3000
    }

    notifications.value.push(notification)

    return id
  }

  const success = (title: string, message?: string, duration?: number) => {
    return show(title, { type: 'success', message, duration })
  }

  const error = (title: string, message?: string, duration?: number) => {
    return show(title, { type: 'error', message, duration })
  }

  const warning = (title: string, message?: string, duration?: number) => {
    return show(title, { type: 'warning', message, duration })
  }

  const info = (title: string, message?: string, duration?: number) => {
    return show(title, { type: 'info', message, duration })
  }

  const remove = (id: string) => {
    const index = notifications.value.findIndex((n) => n.id === id)
    if (index > -1) {
      notifications.value.splice(index, 1)
    }
  }

  const clear = () => {
    notifications.value = []
  }

  onUnmounted(() => {
    clear()
  })

  return {
    notifications,
    show,
    success,
    error,
    warning,
    info,
    remove,
    clear
  }
}
