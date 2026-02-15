<template>
  <!-- From Uiverse.io by satyamchaudharydev --> 
  <button class="back-top-button" @click="scrollToTop">
    <div class="text">
      <span>返回</span>
      <span>顶部</span>
    </div>
    <div class="clone">
      <span>返回</span>
      <span>顶部</span>
    </div>
    <svg
      stroke-width="2"
      stroke="currentColor"
      viewBox="0 0 24 24"
      fill="none"
      class="h-6 w-6"
      xmlns="http://www.w3.org/2000/svg"
      width="20px"
    >
      <path
        d="M14 5l7 7m0 0l-7 7m7-7H3"
        stroke-linejoin="round"
        stroke-linecap="round"
      ></path>
    </svg>
  </button>

</template>

<script setup lang="ts">
const scrollToTop = () => {
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  })
}
</script>

<style scoped>
/* From Uiverse.io by satyamchaudharydev */ 
.back-top-button {
  width: 140px;
  height: 56px;
  overflow: hidden;
  border: 1px solid #F7F9FE;
  color: #0f172a;
  background: #F7F9FE;
  position: relative;
  padding-bottom: 2em;
  cursor: pointer;
  border-radius: 14px;
}

:global(html.dark) .back-top-button {
  background: #121212;
  border-color: #121212;
}
.back-top-button > div,
.back-top-button > svg {
  position: absolute;
  width: 100%;
  height: 100%;
  display: flex;
}

.back-top-button:before {
  content: "";
  position: absolute;
  height: 2px;
  bottom: 0;
  left: 0;
  width: 100%;
  transform: scaleX(0);
  transform-origin: bottom right;
  background: currentColor;
  transition: transform 0.25s ease-out;
}

.back-top-button:hover:before {
  transform: scaleX(1);
  transform-origin: bottom left;
}

.back-top-button .clone > *,
.back-top-button .text > * {
  opacity: 1;
  font-size: 1.05rem;
  transition: 0.2s;
  margin-left: 4px;
}

.back-top-button .clone > * {
  transform: translateY(60px);
}

.back-top-button:hover .clone > * {
  opacity: 1;
  transform: translateY(0px);
  transition: all 0.2s cubic-bezier(0.215, 0.61, 0.355, 1) 0s;
}

.back-top-button:hover .text > * {
  opacity: 1;
  transform: translateY(-60px);
  transition: all 0.2s cubic-bezier(0.215, 0.61, 0.355, 1) 0s;
}

.back-top-button:hover .clone > :nth-child(1) {
  transition-delay: 0.15s;
}

.back-top-button:hover .clone > :nth-child(2) {
  transition-delay: 0.2s;
}

.back-top-button:hover .clone > :nth-child(3) {
  transition-delay: 0.25s;
}

.back-top-button:hover .clone > :nth-child(4) {
  transition-delay: 0.3s;
}
/* icon style and hover */
.back-top-button svg {
  width: 20px;
  right: 0;
  top: 50%;
  transform: translateY(-50%) rotate(-50deg);
  transition: 0.2s ease-out;
}

.back-top-button:hover svg {
  transform: translateY(-50%) rotate(-90deg);
}
/* 深色模式全局样式，不使用 scoped 以获得更低的优先级 */
html.dark .back-top-button {
  border: 1px solid #121212 !important;
  color: var(--text-primary) !important;
  background: #121212 !important;
}
</style>