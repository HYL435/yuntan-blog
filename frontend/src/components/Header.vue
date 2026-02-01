<script setup>
import { ref, onMounted, onUnmounted } from "vue";

// 状态管理
const showTransparentHeader = ref(true); // 控制透明导航显示
const showStickyHeader = ref(false);     // 控制白色悬浮导航显示
const isMenuOpen = ref(false);

const handleScroll = () => {
  const scrollTop = window.scrollY || document.documentElement.scrollTop;

  // 1. 透明导航逻辑
  // 阈值设得很小 (10px)，一旦开始滚动，它就利用“先快后慢”的动画迅速撤离
  // 如果回到最顶部，它再滑回来
  showTransparentHeader.value = scrollTop < 10;

  // 2. 白色导航逻辑
  // 阈值设为 180px，保证两者之间有足够的时间差，不会重叠
  showStickyHeader.value = scrollTop > 180;
};

const toggleMenu = () => {
  isMenuOpen.value = !isMenuOpen.value;
  if (isMenuOpen.value) {
    document.body.style.overflow = "hidden";
  } else {
    document.body.style.overflow = "";
  }
};

const navLinks = [
  { name: "Home", href: "#super_container" },
  { name: "About", href: "#about" },
  { name: "Work", href: "#work" },
  { name: "Contact", href: "#contact" },
];

onMounted(() => {
  handleScroll();
  window.addEventListener("scroll", handleScroll);
});

onUnmounted(() => {
  window.removeEventListener("scroll", handleScroll);
  document.body.style.overflow = "";
});
</script>

<template>
  <div>
    <!-- ============================================== -->
    <!-- Header 1: 透明导航 (Fixed)                      -->
    <!-- 动画：先快后慢 (Expo Ease Out 质感)             -->
    <!-- ============================================== -->
    <header
      class="fixed top-0 left-0 w-full z-30 bg-transparent py-6 transition-transform duration-1000 ease-[cubic-bezier(0.19,1,0.22,1)]"
      :class="showTransparentHeader ? 'translate-y-0' : '-translate-y-full'"
    >
      <div class="container mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex items-center justify-between">
          <!-- Logo (White) -->
          <a href="#" class="flex items-center gap-3 group relative">
            <div
              class="w-10 h-10 bg-white text-brand-black flex items-center justify-center rounded-sm"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-6 w-6"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M13 10V3L4 14h7v7l9-11h-7z"
                />
              </svg>
            </div>
            <span
              class="font-display text-2xl font-extrabold tracking-tighter uppercase text-white"
            >
              Squareform
            </span>
          </a>

          <!-- Nav (White Text) -->
          <div class="hidden md:flex items-center gap-8">
            <nav>
              <ul class="flex items-center gap-8">
                <li v-for="link in navLinks" :key="link.name">
                  <a
                    :href="link.href"
                    class="text-lg font-medium text-white hover:text-gray-300 transition-colors relative group"
                  >
                    {{ link.name }}
                    <span
                      class="absolute -bottom-1 left-0 w-0 h-0.5 bg-white transition-[width] duration-300 group-hover:w-full"
                    ></span>
                  </a>
                </li>
              </ul>
            </nav>
            <a
              href="#"
              class="bg-white text-brand-black px-6 py-2.5 rounded-full font-medium hover:bg-gray-100 transition-all transform hover:scale-105"
            >
              Get Started
            </a>
          </div>

          <!-- Hamburger (White) -->
          <button
            @click="toggleMenu"
            class="md:hidden w-10 h-10 flex flex-col justify-center items-center gap-1.5 focus:outline-none"
          >
            <span class="block w-6 h-0.5 bg-white"></span>
            <span class="block w-6 h-0.5 bg-white"></span>
            <span class="block w-6 h-0.5 bg-white"></span>
          </button>
        </div>
      </div>
    </header>

    <!-- ============================================== -->
    <!-- Header 2: 白色悬浮导航 (Fixed)                  -->
    <!-- 动画：平滑上升下降 (Standard Ease)               -->
    <!-- ============================================== -->
    <header
      class="fixed top-0 left-0 w-full z-40 bg-white shadow-md py-4 transition-transform duration-700 ease-in-out"
      :class="showStickyHeader ? 'translate-y-0' : '-translate-y-full'"
    >
      <div class="container mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex items-center justify-between">
          <!-- Logo (Black) -->
          <a href="#" class="flex items-center gap-3 group relative">
            <div
              class="w-10 h-10 bg-brand-black text-white flex items-center justify-center rounded-sm"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-6 w-6"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M13 10V3L4 14h7v7l9-11h-7z"
                />
              </svg>
            </div>
            <span
              class="font-display text-2xl font-extrabold tracking-tighter uppercase text-brand-black"
            >
              Squareform
            </span>
          </a>

          <!-- Nav (Black Text) -->
          <div class="hidden md:flex items-center gap-8">
            <nav>
              <ul class="flex items-center gap-8">
                <li v-for="link in navLinks" :key="link.name">
                  <a
                    :href="link.href"
                    class="text-lg font-medium text-brand-black hover:text-gray-600 transition-colors relative group"
                  >
                    {{ link.name }}
                    <span
                      class="absolute -bottom-1 left-0 w-0 h-0.5 bg-brand-black transition-[width] duration-300 group-hover:w-full"
                    ></span>
                  </a>
                </li>
              </ul>
            </nav>
            <a
              href="#"
              class="bg-brand-black text-white px-6 py-2.5 rounded-full font-medium hover:bg-gray-800 transition-all transform hover:scale-105"
            >
              Get Started
            </a>
          </div>

          <!-- Hamburger (Black) -->
          <button
            @click="toggleMenu"
            class="md:hidden w-10 h-10 flex flex-col justify-center items-center gap-1.5 focus:outline-none"
          >
            <span class="block w-6 h-0.5 bg-brand-black"></span>
            <span class="block w-6 h-0.5 bg-brand-black"></span>
            <span class="block w-6 h-0.5 bg-brand-black"></span>
          </button>
        </div>
      </div>
    </header>

    <!-- ============================================== -->
    <!-- Mobile Menu Overlay (Shared)                   -->
    <!-- ============================================== -->
    <div
      class="fixed inset-0 bg-brand-black z-50 transition-transform duration-500 ease-[cubic-bezier(0.77,0,0.175,1)]"
      :class="isMenuOpen ? 'translate-y-0' : '-translate-y-full'"
    >
      <!-- Close Button -->
      <button
        @click="toggleMenu"
        class="absolute top-6 right-4 sm:right-6 lg:right-8 w-10 h-10 flex flex-col justify-center items-center gap-1.5 z-50"
      >
        <span class="block w-6 h-0.5 bg-white rotate-45 translate-y-2"></span>
        <span class="block w-6 h-0.5 bg-white opacity-0"></span>
        <span
          class="block w-6 h-0.5 bg-white -rotate-45 -translate-y-2"
        ></span>
      </button>

      <div
        class="w-full h-full flex flex-col items-center justify-center pt-20"
      >
        <ul class="flex flex-col items-center gap-8 mb-10">
          <li v-for="link in navLinks" :key="link.name">
            <a
              :href="link.href"
              @click="toggleMenu"
              class="text-3xl font-medium text-white hover:text-gray-300 transition-colors"
            >
              {{ link.name }}
            </a>
          </li>
        </ul>
        <a
          href="#"
          class="bg-white text-brand-black px-8 py-3 rounded-full text-lg font-medium hover:bg-gray-200 transition-colors"
        >
          Get Started
        </a>
      </div>
    </div>
  </div>
</template>

<style scoped>
.text-brand-black {
  color: #1f1f1f;
}
.bg-brand-black {
  background-color: #1f1f1f;
}
</style>