import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './styles/tokens.css'
import './style.css'

// 创建 Vue 应用,注册 Pinia 状态管理与 Vue Router,挂载到 #app
const app = createApp(App)
app.use(createPinia())
app.use(router)
app.mount('#app')
