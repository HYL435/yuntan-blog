<template>
  <button class="button" @click="scrollToTop">
    <svg class="svgIcon" viewBox="0 0 384 512">
      <path
        d="M214.6 41.4c-12.5-12.5-32.8-12.5-45.3 0l-160 160c-12.5 12.5-12.5 32.8 0 45.3s32.8 12.5 45.3 0L160 141.2V448c0 17.7 14.3 32 32 32s32-14.3 32-32V141.2l105.4 105.4c12.5 12.5 32.8 12.5 45.3 0s12.5-32.8 0-45.3l-160-160z"
      /></svg>
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
.button {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: rgb(80, 80, 80);
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.5s ease;
  box-shadow: 0px 0px 0px 4px rgba(180, 160, 255, 0.253);
}

.button:hover {
  width: 85px;
  height: 28px;
  border-radius: 12px;
  background: rgb(181, 160, 255);
}

.svgIcon {
  width: 14px;
  height: 14px;
  fill: white;
  transition: transform 0.5s ease;
  flex-shrink: 0;
}

.button:hover .svgIcon {
  transform: translateY(-200%);
}

.button::before {
  content: "Back to Top";
  position: absolute;
  color: white;
  font-size: 9px;
  font-weight: 600;
  opacity: 0;
  transition: opacity 0.5s ease;
  white-space: nowrap;
  letter-spacing: 0.5px;
}

.button:hover::before {
  opacity: 1;
}

:global(.dark) .button {
  background: rgb(20, 20, 20);
  box-shadow: 0px 0px 0px 4px rgba(180, 160, 255, 0.253);
}

:global(.dark) .button:hover {
  background: rgb(181, 160, 255);
}
</style>