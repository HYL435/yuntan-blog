<template>
  <div class="loading" aria-label="loading" role="status">
    <svg width="64" height="48" viewBox="0 0 64 48">
      <polyline points="0.157 23.954, 14 23.954, 21.843 48, 43 0, 50 24, 64 24" id="back"></polyline>
      <polyline points="0.157 23.954, 14 23.954, 21.843 48, 43 0, 50 24, 64 24" id="front"></polyline>
    </svg>
  </div>
</template>

<script setup lang="ts" name="LoadingPulse">
</script>

<style scoped>
.loading {
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.loading svg polyline {
  fill: none;
  stroke-width: 3;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.loading svg polyline#back {
  stroke: #ff4d5033;
}

.loading svg polyline#front {
  stroke: #ff4d4f;
  stroke-dasharray: 48, 144;
  stroke-dashoffset: 192;
  animation: dash_682 1.4s linear infinite;
}

@keyframes dash_682 {
  72.5% {
    opacity: 0;
  }

  to {
    stroke-dashoffset: 0;
  }
}
</style>
