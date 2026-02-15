<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import { useRouter } from "vue-router";
import OptionButton from "@/components/common/OptionButton.vue";
import DarkButton from "@/components/common/DarkButton.vue";
import LoginButton from "@/components/common/LoginButton.vue";
import BackTopButton from "@/components/common/BackTopButton.vue";

const showTransparentHeader = ref(true);
const showStickyHeader = ref(false);
const isMenuOpen = ref(false);
const router = useRouter();

const handleScroll = () => {
  const scrollTop = window.scrollY || document.documentElement.scrollTop;
  showTransparentHeader.value = scrollTop < 10;
  showStickyHeader.value = scrollTop > 170;
};

const toggleMenu = () => {
  isMenuOpen.value = !isMenuOpen.value;
  document.body.style.overflow = isMenuOpen.value ? "hidden" : "";
};

const handleOptionClick = (linkName, option) => {
  if (linkName !== "文章") return;
  if (!option) return;
  router.push(`/tag/${encodeURIComponent(option)}`);
};

const navLinks = [
  { name: "文章", href: "#super_container", options: ["全部", "标签", "后端"] },
  { name: "关于", href: "#about", options: ["团队介绍", "发展历程"] },
  { name: "工作", href: "#work" },
  { name: "联系", href: "#contact", options: ["微信", "邮箱"] },
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
    <!-- Header 1: 透明导航 -->
    <header
      class="header-slide fixed top-0 left-0 w-full z-30 bg-transparent py-6 transition-transform duration-1000 ease-[cubic-bezier(0.19,1,0.22,1)]"
      :class="showTransparentHeader ? 'translate-y-0' : '-translate-y-full'"
    >
      <div class="container mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex items-center justify-between">
          <RouterLink to="/" class="flex items-center gap-3">
            <div class="w-10 h-10 bg-white text-black flex items-center justify-center rounded-sm">
               <span class="font-bold">Logo</span>
            </div>
            <span class="text-2xl font-extrabold text-white">云 坛</span>
          </RouterLink>

          <div class="hidden md:flex items-center gap-8">
            <nav>
              <ul class="flex items-center gap-8">
                <li v-for="link in navLinks" :key="link.name" class="relative group h-full flex items-center">
                  <a :href="link.href" class="relative group/link text-lg font-medium text-white hover:text-gray-200 transition-colors py-2">
                    {{ link.name }}
                    <span class="nav-underline absolute -bottom-1 left-0 h-0.5 bg-white"></span>
                  </a>
                  <div v-if="link.options" class="nav-dropdown-wrapper">
                    <div class="nav-dropdown-card">
                      <div class="card-content">
                         <OptionButton
                           v-for="opt in link.options"
                           :key="opt"
                           @click="handleOptionClick(link.name, opt)"
                         >
                           {{ opt }}
                         </OptionButton>
                      </div>
                    </div>
                  </div>
                </li>
              </ul>
            </nav>
            <DarkButton />
            <LoginButton />
          </div>
          <button @click="toggleMenu" class="md:hidden text-white">菜单</button>
        </div>
      </div>
    </header>

    <!-- Header 2: 白色悬浮导航 -->
    <header
      class="header-slide fixed top-0 left-0 w-full z-40 bg-[#F7F9FE] shadow-md py-4 transition-transform duration-700 ease-in-out header-light"
      :class="showStickyHeader ? 'translate-y-0' : '-translate-y-full'"
    >
      <div class="container mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex items-center justify-between">      
          <RouterLink to="/" class="flex items-center gap-3">
            <div class="w-10 h-10 bg-black text-white flex items-center justify-center rounded-sm">
               <span class="font-bold">Logo</span>
            </div>
            <span class="text-2xl font-extrabold text-black">云 坛</span>
          </RouterLink>

          <div class="hidden md:flex items-center gap-8">
            <nav>
              <ul class="flex items-center gap-8">
                <li v-for="link in navLinks" :key="link.name" class="relative group h-full flex items-center">
                  <a :href="link.href" class="relative group/link text-lg font-medium text-black hover:text-gray-600 transition-colors py-2">
                    {{ link.name }}
                    <span class="nav-underline absolute -bottom-1 left-0 h-0.5 bg-black"></span>
                  </a>
                  <div v-if="link.options" class="nav-dropdown-wrapper">
                    <div class="nav-dropdown-card">
                      <div class="card-content">
                         <OptionButton
                           v-for="opt in link.options"
                           :key="opt"
                           @click="handleOptionClick(link.name, opt)"
                         >
                           {{ opt }}
                         </OptionButton>
                      </div>
                    </div>
                  </div>
                </li>
              </ul>
            </nav>

            <BackTopButton />

            <LoginButton />
          </div>
          <button @click="toggleMenu" class="md:hidden text-black">菜单</button>
        </div>
      </div>
    </header>

    <!-- Mobile Menu -->
    <div
      class="fixed inset-0 bg-brand-black z-50 transition-transform duration-500 ease-[cubic-bezier(0.77,0,0.175,1)]"
      :class="isMenuOpen ? 'translate-y-0' : '-translate-y-full'"
    >
      <button @click="toggleMenu" class="absolute top-6 right-8 text-white text-xl">X</button>
      <div class="w-full h-full flex flex-col items-center justify-center pt-20">
        <ul class="flex flex-col items-center gap-8 mb-10">
          <li v-for="link in navLinks" :key="link.name">
            <a :href="link.href" @click="toggleMenu" class="text-3xl font-medium text-white hover:text-gray-300">{{ link.name }}</a>
          </li>
        </ul>
        <LoginButton />
      </div>
    </div>
  </div>
</template>

<style scoped>
.bg-brand-black { background-color: #1f1f1f; }

.header-slide {
  transition: transform 1s cubic-bezier(0.19,1,0.22,1), translate 1s cubic-bezier(0.19,1,0.22,1) !important;
  will-change: transform;
}

.nav-underline {
  width: 100%;
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.3s ease;
}

.group\/link:hover .nav-underline,
.group:hover .nav-underline {
  transform: scaleX(1);
}

/* 1. 下拉菜单定位桥梁 */
.nav-dropdown-wrapper {
  position: absolute;
  left: 50%;
  top: 100%;
  transform: translateX(-50%);
  padding-top: 15px;
  visibility: hidden;
  opacity: 0;
  transition: all 0.2s ease-in-out;
  z-index: 50;
  pointer-events: none;
}

.nav-dropdown-wrapper > * {
  pointer-events: auto;
}

.group:hover .nav-dropdown-wrapper {
  visibility: visible;
  opacity: 1;
}

/* 
  2. 卡片外壳 (默认浅色模式)
*/
.nav-dropdown-card {
  width: fit-content;
  max-width: 160px;
  min-width: 100px;
  background: #3b5998; /* 浅色模式下的蓝底 */
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  position: relative;
  /* 浅色模式下的青色阴影 */
  box-shadow: 0px 6px 26px rgba(6, 182, 212, 0.25), 0px 0px 0px 1px rgba(6, 182, 212, 0.3);
  border-radius: 8px;
  border: 1px solid rgba(6, 182, 212, 0.5); /* 浅色模式下的边框 */
  transition: background 0.3s ease, box-shadow 0.3s ease, border-color 0.3s ease;
}

/* 
  3. 内容层 (默认浅色模式)
*/
.card-content {
  background: rgba(15, 23, 42, 0.85);
  backdrop-filter: blur(12px);
  z-index: 2;
  padding: 10px 5px;
  display: flex;
  flex-direction: column;
  gap: 5px;
  justify-content: center;
  align-items: center;
  width: calc(100% - 4px);
  height: calc(100% - 4px);
  margin: 2px;
  border-radius: 6px;
  position: relative;
  transition: background 0.3s ease;
}

/* Hover时的内部光晕 */
.card-content::before {
  opacity: 0;
  transition: opacity 300ms;
  content: " ";
  display: block;
  background: white;
  width: 5px;
  height: 50px;
  position: absolute;
  filter: blur(50px);
  overflow: hidden;
  pointer-events: none;
}
.nav-dropdown-card:hover .card-content::before {
  opacity: 1;
}

/* 
  4. 旋转的彩色长条 (默认浅色模式：亮青色+白色)
*/
.nav-dropdown-card::before {
  opacity: 0;
  content: " ";
  position: absolute;
  display: block;
  width: 170%;
  height: 170%;
  background: conic-gradient(
    from 0deg,
    transparent 0deg,
    rgba(6, 182, 212, 0.6) 90deg, /* 亮青色 */
    rgba(6, 182, 212, 1) 120deg, /* 纯青色 */
    rgba(255, 255, 255, 1) 150deg, /* 纯白高光 */
    rgba(6, 182, 212, 1) 180deg, /* 纯青色 */
    rgba(6, 182, 212, 0.6) 210deg,
    transparent 270deg,
    transparent 360deg
  );
  transition: opacity 300ms;
  animation: rotation_9018 3000ms infinite linear;
  animation-play-state: paused;
  z-index: 0;
}

/* 只要下拉菜单显示出来，就让它旋转 */
.nav-dropdown-card:hover::before, 
.group:hover .nav-dropdown-card::before {
  opacity: 1;
  animation-play-state: running;
}

/* 底部模糊层 */
.nav-dropdown-card::after {
  position: absolute;
  content: " ";
  display: block;
  width: 100%;
  height: 100%;
  background: #17171733;
  backdrop-filter: blur(50px);
  z-index: 0;
  pointer-events: none;
}

@keyframes rotation_9018 {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 深色模式样式通过全局 style.css 控制 */
</style>