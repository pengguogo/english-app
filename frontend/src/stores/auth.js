/**
 * @file 家庭账号会话 store
 * @description 缓存 /auth/me 结果,供路由守卫与页面判断登录、选孩子状态及同意状态。
 *  me: { familyId, email, consented, consentVersion, currentChildId, currentChild:{id,nickname,avatarUrl}|null },未登录为 null。
 */
import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { getMe, logout as apiLogout } from '../api/auth'

export const useAuthStore = defineStore('auth', () => {
  const me = ref(null)
  const loaded = ref(false)

  const loggedIn = computed(() => Boolean(me.value))
  const consented = computed(() => Boolean(me.value?.consented))
  const hasChild = computed(() => me.value?.currentChildId != null)

  /** 拉取会话;失败(网络等)按游客处理 */
  async function refresh() {
    try {
      me.value = await getMe()
    } catch {
      me.value = null
    } finally {
      loaded.value = true
    }
    return me.value
  }

  /** 首次进入时加载一次 */
  async function ensureLoaded() {
    if (!loaded.value) await refresh()
    return me.value
  }

  async function logout() {
    try { await apiLogout() } finally {
      me.value = null
      // 退出后 CSRF token 会轮换,重新拉一次 me 以拿到新 cookie
      await refresh()
    }
  }

  return { me, loaded, loggedIn, consented, hasChild, refresh, ensureLoaded, logout }
})
