/**
 * @file axios 实例与统一拦截器
 * @description
 *  创建带统一 baseURL 与超时的 axios 实例,并通过响应拦截器统一处理后端 Result 格式。
 *  对于返回二进制流(TTS 语音)的接口,直接透传 Blob,不进行 Result 解析。
 *  家庭账号:携带 Session Cookie,并把 XSRF-TOKEN cookie 回传为 X-XSRF-TOKEN 头;
 *  401 → /login(仅需登录页面),409(4091) → /choose-child,403 家长验证失效 → 弹家长密码框后重试。
 * @author english-app
 * @since 2026-07-20
 */
import axios from 'axios'
import { getCookie } from '../utils/cookie'
import { requestParentVerify } from '../composables/useParentVerify'

// 统一前缀,与 vite.config.js 中 /api 代理配合,实际请求转发到后端 8080
const http = axios.create({
  baseURL: '/api/v1',
  // 15 秒超时,语音合成/评测可能较慢,留足时间
  timeout: 15000,
  withCredentials: true,
  xsrfCookieName: 'XSRF-TOKEN',
  xsrfHeaderName: 'X-XSRF-TOKEN'
})

// 家长二次验证失效时 403 的业务 code(auth 清单 v1)
export const PARENT_VERIFY_CODE = 4031

// 路由实例由 main.js 注入,避免 http ↔ router 循环依赖
let router = null
export function bindRouter(instance) { router = instance }

function redirect(path) {
  if (!router) return
  const current = router.currentRoute.value
  if (current.path === path) return
  router.push({ path, query: { redirect: current.fullPath } })
}

function currentNeedsLogin() {
  const meta = router?.currentRoute.value.meta || {}
  return Boolean(meta.requiresAuth || meta.requiresLogin)
}

/** 读取当前 XSRF token,供 fetch 等非 axios 请求使用 */
export const xsrfHeaders = () => ({ 'X-XSRF-TOKEN': getCookie('XSRF-TOKEN') || '' })

// 响应拦截器:统一处理后端 Result 格式 { code, message, data }
http.interceptors.response.use(
  (response) => {
    // TTS 等接口返回 binary blob,直接透传,不进行 Result 解析
    if (response.data instanceof Blob) {
      return response.data
    }
    const result = response.data
    // 业务成功:剥离外层包装,直接返回 data 字段,调用方拿到的是纯业务数据
    if (result.code === 200) {
      return result.data
    }
    // 业务失败:统一转成 Error 抛出,便于上层 catch
    return Promise.reject(new Error(result.message || '请求失败'))
  },
  async (error) => {
    const status = error.response?.status
    const code = error.response?.data?.code
    const config = error.config || {}
    // 调用方可用 skipAuthRedirect 自行处理(如登录页、/auth/me)
    if (!config.skipAuthRedirect) {
      // 需登录页面才跳转;其他页面(游客浏览)不跳转,错误照常抛给调用方处理
      if (status === 401 && currentNeedsLogin()) redirect('/login')
      else if (status === 409 && code === 4091) redirect('/choose-child')
      else if (status === 403 && code === PARENT_VERIFY_CODE && !config._parentRetried) {
        // 弹家长密码框,验证成功后原请求重试一次
        if (await requestParentVerify()) return http({ ...config, _parentRetried: true })
      }
    }
    // 网络层或 HTTP 状态码错误统一记录日志
    console.error('请求失败:', error.message)
    return Promise.reject(error)
  }
)

export default http
