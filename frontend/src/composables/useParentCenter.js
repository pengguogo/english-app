import { ref } from 'vue'
import { getWeekReport } from '../api/learning'

// 家长中心:周报需家长二次验证,失效时由 http 拦截器弹家长密码框
export function useParentCenter() {
  const unlocked = ref(false)
  const loading = ref(false)
  const error = ref('')
  const report = ref(null)
  async function load() {
    loading.value = true
    error.value = ''
    try {
      report.value = await getWeekReport()
      unlocked.value = true
    } catch (e) {
      unlocked.value = false
      error.value = e.response?.data?.message || '加载失败，请重试'
    } finally { loading.value = false }
  }
  return { unlocked, loading, error, report, load }
}
