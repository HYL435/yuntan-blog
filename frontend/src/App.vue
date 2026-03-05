<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import Header from '@/layouts/Header.vue'
import Footer from '@/layouts/Footer.vue'
import LoadingPulse from '@/components/loaders/LoadingPulse.vue'
import NotificationsContainer from '@/layouts/NotificationsContainer.vue'
import PortalButton from '@/components/common/PortalButton.vue'

const route = useRoute()
const hideChromeRoutes = new Set(['/login', '/register'])
const isAdminRoute = computed(() => route.path.startsWith('/admin'))
const showChrome = computed(() => !isAdminRoute.value && !hideChromeRoutes.has(route.path))
const showFooter = computed(() => !isAdminRoute.value && !hideChromeRoutes.has(route.path) && route.path !== '/profile')
</script>

<template>
  <Header v-if="showChrome" />

  <Suspense>
    <router-view />
    <template #fallback>
      <div class="app-loading">
        <LoadingPulse />
      </div>
    </template>
  </Suspense>

  <Footer v-if="showFooter" />

  <NotificationsContainer />
  <PortalButton />
</template>

<style scoped>
.app-loading {
  min-height: 60vh;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
