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

:global(html.dark) .line {
  background: linear-gradient(
    to right,
    rgba(30, 58, 138, 0.5),
    rgba(59, 130, 246, 0.6),
    rgba(30, 58, 138, 0.5)
  );
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

:global(html.dark) .separator-title {
  color: #e5e7eb;
}

:global(html.dark) .separator-title::before,
:global(html.dark) .separator-title::after {
  background: linear-gradient(135deg, #60a5fa, #22d3ee);
}
</style>