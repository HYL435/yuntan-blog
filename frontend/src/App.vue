<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import Header from '@/components/layout/Header.vue'
import Footer from '@/components/layout/Footer.vue'
import LoadingPulse from '@/components/loaders/LoadingPulse.vue'

const route = useRoute()
const hideChromeRoutes = new Set(['/login', '/register'])
const showChrome = computed(() => !hideChromeRoutes.has(route.path))
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

  <Footer v-if="showChrome" />
</template>

<style scoped>
.app-loading {
  min-height: 60vh;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
