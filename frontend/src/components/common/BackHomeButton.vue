<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}
</script>

<template>
  <div class="back-home-button">
    <button
      class="back-btn"
      type="button"
      @click="goHome"
    >
      <span class="back-fill" aria-hidden="true"></span>
      <span class="back-content">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          viewBox="0 0 1024 1024"
          height="20px"
          width="20px"
          class="back-icon"
        >
          <path
            d="M224 480h640a32 32 0 1 1 0 64H224a32 32 0 0 1 0-64z"
            fill="currentColor"
          ></path>
          <path
            d="m237.248 512 265.408 265.344a32 32 0 0 1-45.312 45.312l-288-288a32 32 0 0 1 0-45.312l288-288a32 32 0 1 1 45.312 45.312L237.248 512z"
            fill="currentColor"
          ></path>
        </svg>
        <span class="back-text">回到首页</span>
      </span>
    </button>
  </div>
</template>

<style scoped>
.back-home-button {
  position: absolute;
  left: 24px;
  top: 24px;
  z-index: 2;
}

.back-btn {
  position: relative;
  overflow: hidden;
  width: 192px;
  height: 56px;
  border-radius: 16px;
  border: 1px solid var(--border-color, #ecedec);
  background: var(--bg-form, #ffffff);
  color: var(--text-main, #151717);
  font-size: 18px;
  font-weight: 600;
  letter-spacing: 0.02em;
  cursor: pointer;
  box-shadow: 0 10px 24px rgba(0, 0, 0, 0.12);
  transition: transform 0.2s ease, box-shadow 0.2s ease, color 0.2s ease;
}

.back-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 14px 28px rgba(0, 0, 0, 0.16);
  color: #ffffff;
}

.back-content {
  position: relative;
  z-index: 2;
  display: inline-flex;
  align-items: center;
  gap: 10px;
}

.back-icon {
  display: block;
}

.back-fill {
  position: absolute;
  left: 6px;
  top: 6px;
  width: calc(100% - 12px);
  height: calc(100% - 12px);
  border-radius: 12px;
  background: linear-gradient(90deg, rgba(168, 85, 247, 0.75) 0%, rgba(236, 72, 153, 0.75) 100%);
  transform-origin: left center;
  transform: scaleX(0);
  transition: transform 1s ease-in-out;
  z-index: 1;
}

.back-btn:hover .back-fill {
  transform: scaleX(1);
}
</style>
