/**
 * @file axios 实例与统一拦截器
 * @description
 *  创建带统一 baseURL 与超时的 axios 实例,并通过响应拦截器统一处理后端 Result 格式。
 *  对于返回二进制流(TTS 语音)的接口,直接透传 Blob,不进行 Result 解析。
 * @author english-app
 * @since 2026-07-20
 */
import axios from 'axios'

// 统一前缀,与 vite.config.js 中 /api 代理配合,实际请求转发到后端 8080
const http = axios.create({
  baseURL: '/api/v1',
  // 15 秒超时,语音合成/评测可能较慢,留足时间
  timeout: 15000
})

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
  (error) => {
    // 网络层或 HTTP 状态码错误统一记录日志
    console.error('请求失败:', error.message)
    return Promise.reject(error)
  }
)

export default http
