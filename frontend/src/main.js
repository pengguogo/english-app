import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import { bindRouter } from './api/http'
import './styles/tokens.css'
import './style.css'

// 创建 Vue 应用,注册 Pinia 状态管理与 Vue Router,挂载到 #app
const app = createApp(App)
app.use(createPinia())
app.use(router)
// http 拦截器 401/409 跳转需要路由实例
bindRouter(router)
app.mount('#app')
