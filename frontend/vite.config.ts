import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import tailwindcss from '@tailwindcss/vite' // 导入tailwindcss

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    tailwindcss() // 使用tailwindcss
  ],
})
