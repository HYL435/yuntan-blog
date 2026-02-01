<template>
  <button
    type="button"
    class="explore-button flex items-center justify-center gap-1.5 px-3 py-1.5 rounded-full font-semibold text-base transition-all duration-300"
  >
    <span>
      登录
    </span>
    <svg
      class="w-4 h-4 transition-transform duration-300"
      viewBox="0 0 16 19"
      xmlns="http://www.w3.org/2000/svg"
    >
      <path
        d="M7 18C7 18.5523 7.44772 19 8 19C8.55228 19 9 18.5523 9 18H7ZM8.70711 0.292893C8.31658 -0.0976311 7.68342 -0.0976311 7.29289 0.292893L0.928932 6.65685C0.538408 7.04738 0.538408 7.68054 0.928932 8.07107C1.31946 8.46159 1.95262 8.46159 2.34315 8.07107L8 2.41421L13.6569 8.07107C14.0474 8.46159 14.6805 8.46159 15.0711 8.07107C15.4616 7.68054 15.4616 7.04738 15.0711 6.65685L8.70711 0.292893ZM9 18L9 1H7L7 18H9Z"
        class="fill-current"
      ></path>
    </svg>
  </button>
</template>

<script setup lang="ts">
// Login button component
</script>

<style scoped>

.explore-button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: 2px solid transparent;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.25);
  position: relative;
  overflow: hidden;
  font-size: 1rem;
  padding: 0.375rem 0.75rem;
}


.explore-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
  transition: left 0.3s ease;
  z-index: -1;
}

.explore-button:hover {
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.35);
  transform: translateY(-1.5px);
}

.explore-button:hover::before {
  left: 0;
}

.explore-button:hover svg {
  transform: rotate(45deg);
}

.explore-button:active {
  transform: translateY(0);
}
</style>
