import { computed, onMounted, ref } from 'vue'
import { getGameAccess } from '../api/games'

export function useGameAccess() {
  const access = ref(null)
  const isLoading = ref(true)
  const errorMsg = ref('')
  const progressPercent = computed(() => access.value
    ? Math.round(access.value.studiedSeconds / access.value.requiredSeconds * 100)
    : 0)

  async function refreshAccess() {
    isLoading.value = true
    errorMsg.value = ''
    try {
      access.value = await getGameAccess()
    } catch (error) {
      errorMsg.value = '暂时无法验证学习时长，请稍后重试'
      console.error('读取游戏解锁状态失败:', error)
    } finally {
      isLoading.value = false
    }
  }

  onMounted(refreshAccess)
  return { access, isLoading, errorMsg, progressPercent, refreshAccess }
}
