import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  build: {
    // 构建产物输出到 Spring Boot 静态资源目录,实现单 JAR 部署
    outDir: '../backend/src/main/resources/static',
    // 构建前清空输出目录,避免残留旧文件
    emptyOutDir: true
  },
  server: {
    // 前端开发服务器端口
    port: 5173,
    proxy: {
      // 开发环境下 /api 请求代理到后端 8080 端口,避免跨域问题
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
