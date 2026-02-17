import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import tailwindcss from '@tailwindcss/vite' // 导入tailwindcss
import { resolve } from 'path'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    tailwindcss() // 使用tailwindcss
  ],
  server: {
    hmr: {
      overlay: false
    },
    proxy: {
      '/front': {
        target: 'http://localhost:9000',
        changeOrigin: true
      }
    }
  },
  resolve: {
    alias: {
      '@': resolve(__dirname, './src')
    }
  }
})