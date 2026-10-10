import { ref } from 'vue'
import { getWeekReport, getParentPasswordStatus, setParentPassword } from '../api/learning'

export function useParentCenter() {
  const unlocked = ref(false)
  const loading = ref(false)
  const saving = ref(false)
  const error = ref('')
  const notice = ref('')
  const report = ref(null)
  const configured = ref(false)
  async function load() {
    loading.value = true
    error.value = ''
    try {
      const [week, status] = await Promise.all([getWeekReport(), getParentPasswordStatus()])
      report.value = week
      configured.value = status.configured
      unlocked.value = true
    } catch (e) {
      if (e.response?.status === 401) unlocked.value = false
      error.value = e.response?.data?.message || '加载失败，请重试'
    } finally { loading.value = false }
  }
  function unlock(key) {
    sessionStorage.setItem('childGrowthKey', key)
    return load()
  }
  async function savePassword(password) {
    saving.value = true
    error.value = notice.value = ''
    try {
      await setParentPassword(password)
      configured.value = true
      notice.value = '游戏密码已保存，可以用新密码解锁今日游戏。'
      return true
    } catch (e) {
      if (e.response?.status === 401) unlocked.value = false
      error.value = e.response?.data?.message || '密码保存失败，请重试'
      return false
    } finally { saving.value = false }
  }
  return { unlocked, loading, saving, error, notice, report, configured, load, unlock, savePassword }
}
