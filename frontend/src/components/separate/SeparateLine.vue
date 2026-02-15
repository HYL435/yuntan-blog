<template>
  <div class="separator-wrapper">
    <div class="separator-line">
      <div class="line"></div>
      <div v-if="title" class="separator-title">{{ title }}</div>
      <div class="line"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  title?: string;
}>();
</script>

<style scoped>
.separator-wrapper {
  width: 100%;
  padding: 2rem 0;
  display: flex;
  justify-content: center;
}

.separator-line {
  display: flex;
  align-items: center;
  gap: 1.5rem;
  width: 100%;
  max-width: 1000px;
  padding: 0 2rem;
}

.line {
  flex: 1;
  height: 2px;
  background: linear-gradient(
    to right,
    rgba(219, 234, 254, 1),
    rgba(96, 165, 250, 0.8),
    rgba(219, 234, 254, 1)
  );
  border-radius: 1px;
}

:global(.dark) .separator-line .line:first-child,
:global(.dark-mode) .separator-line .line:first-child {
  background-image: linear-gradient(to right, #111111, #7f1d1d) !important;
  background-color: transparent !important;
}

:global(.dark) .separator-line .line:last-child,
:global(.dark-mode) .separator-line .line:last-child {
  background-image: linear-gradient(to right, #7f1d1d, #111111) !important;
  background-color: transparent !important;
}

.separator-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  white-space: nowrap;
  letter-spacing: 0.05em;
  position: relative;
  padding: 0 1rem;
}

.separator-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 4px;
  background: linear-gradient(135deg, #3b82f6, #06b6d4);
  border-radius: 50%;
}

.separator-title::after {
  content: '';
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 4px;
  background: linear-gradient(135deg, #3b82f6, #06b6d4);
  border-radius: 50%;
}

:global(.dark) .separator-title,
:global(.dark-mode) .separator-title {
  color: #fca5a5;
}

:global(.dark) .separator-title::before,
:global(.dark) .separator-title::after,
:global(.dark-mode) .separator-title::before,
:global(.dark-mode) .separator-title::after {
  background: #ef4444 !important;
}

@media (prefers-color-scheme: dark) {
  .separator-line .line:first-child {
    background-image: linear-gradient(to right, #111111, #7f1d1d) !important;
    background-color: transparent !important;
  }

  .separator-line .line:last-child {
    background-image: linear-gradient(to right, #7f1d1d, #111111) !important;
    background-color: transparent !important;
  }

  .separator-title {
    color: #fca5a5;
  }

  .separator-title::before,
  .separator-title::after {
    background: #ef4444 !important;
  }
}
</style>