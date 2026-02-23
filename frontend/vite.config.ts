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
      },
      // 使用 /api 前缀代理后端，避免与前端 /admin 路由冲突
      '/api': {
        target: 'http://localhost:9000',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  },
  resolve: {
    alias: {
      '@': resolve(__dirname, './src')
    }
  }
})