/**
 * useParentVerify.js - 家长二次验证弹窗的全局状态
 * 用途: http 拦截到“家长验证失效”的 403 时调用 requestParentVerify(),
 *       由 App.vue 挂载的 ParentVerifyModal 展示密码框;验证成功 resolve(true),取消 resolve(false)。
 *       多个并发请求共用同一次弹窗。
 */
import { ref } from 'vue'

export const parentVerifyOpen = ref(false)
let pending = null
let resolvePending = null

/** 打开家长密码框,返回是否验证成功 */
export function requestParentVerify() {
  if (!pending) {
    parentVerifyOpen.value = true
    pending = new Promise((resolve) => { resolvePending = resolve })
  }
  return pending
}

/** 弹窗结束时调用 */
export function finishParentVerify(ok) {
  parentVerifyOpen.value = false
  const resolve = resolvePending
  pending = null
  resolvePending = null
  resolve?.(ok)
}
