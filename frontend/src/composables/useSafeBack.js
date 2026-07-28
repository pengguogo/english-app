import { useRouter } from 'vue-router'

/**
 * 优先返回真实来源；直接打开页面时替换为安全兜底地址。
 */
export function useSafeBack() {
  const router = useRouter()

  function safeBack(fallback = '/') {
    if (window.history.state?.back) {
      router.back()
      return
    }
    router.replace(fallback)
  }

  return { safeBack }
}
