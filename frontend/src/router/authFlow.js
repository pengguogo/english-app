/**
 * authFlow.js - 登录后的下一步与路由守卫
 * 顺序: 未登录 → /login;未选孩子 → /choose-child;否则放行。
 * 监护人同意只控制 Agent(设计 2.2),阶段 0 不拦截,入口在家长中心。
 */

/** 只接受站内相对路径,防止开放重定向 */
function safeRedirect(redirect) {
  return typeof redirect === 'string' && redirect.startsWith('/') && !redirect.startsWith('//') ? redirect : '/'
}

/**
 * 登录/注册/选孩子完成后去哪里。
 * @param {Object} auth auth store
 * @param {string} redirect 原目标路径
 */
export function nextStepAfterAuth(auth, redirect) {
  const query = redirect ? { redirect: safeRedirect(redirect) } : {}
  if (!auth.loggedIn) return { path: '/login', query }
  if (!auth.hasChild) return { path: '/choose-child', query }
  return safeRedirect(redirect)
}

/**
 * 路由守卫: meta.requiresAuth 的页面要求登录、已选孩子;
 * meta.requiresLogin 的页面(同意/选孩子)只要求登录。
 */
export function installAuthGuard(router, useAuthStore) {
  router.beforeEach(async (to) => {
    const auth = useAuthStore()
    await auth.ensureLoaded()
    if (to.meta.guestOnly && auth.loggedIn) return nextStepAfterAuth(auth, to.query.redirect)
    if (to.meta.requiresLogin && !auth.loggedIn) return { path: '/login', query: { redirect: to.fullPath } }
    if (to.meta.requiresAuth) {
      const step = nextStepAfterAuth(auth, to.fullPath)
      if (typeof step === 'object') return step
    }
    return true
  })
}
