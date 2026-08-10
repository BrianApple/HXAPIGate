import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// base: './' 相对路径 —— 打包后由 Spring Boot 静态托管，可部署在任意子路径
export default defineConfig({
  base: './',
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      // 开发模式代理到本地后端
      '/inner': { target: 'http://127.0.0.1:18080', changeOrigin: true },
      '/account': { target: 'http://127.0.0.1:18080', changeOrigin: true }
    }
  },
  build: {
    outDir: 'dist',
    chunkSizeWarningLimit: 1500
  }
})
