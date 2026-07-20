import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import fs from 'node:fs'
import path from 'node:path'

// 读取 mkcert 生成的本地 HTTPS 证书(位于 frontend 根目录)
// 使局域网手机访问 https://<电脑IP>:5173 时也能使用录音等需要安全上下文的 API
const certFile = path.resolve(__dirname, '192.168.0.106+2.pem')
const keyFile = path.resolve(__dirname, '192.168.0.106+2-key.pem')
const hasCerts = fs.existsSync(certFile) && fs.existsSync(keyFile)

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
    // 暴露到局域网,允许手机等设备通过 https://<电脑IP>:5173 访问
    host: '0.0.0.0',
    // 启用 HTTPS(基于 mkcert 本地 CA 签发),手机信任根证书后无安全警告
    https: hasCerts
      ? { cert: fs.readFileSync(certFile), key: fs.readFileSync(keyFile) }
      : undefined,
    proxy: {
      // 开发环境下 /api 请求代理到后端 8080 端口,避免跨域问题
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
