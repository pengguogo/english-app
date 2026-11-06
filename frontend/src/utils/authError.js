/**
 * authError.js - 账号相关错误判断与提示文案(错误码见 ea-auth 接口清单 v1)
 */

/**
 * 判断请求是否因未登录被拒(401)
 * 用途: 游客学完一课时 completeLesson 返回 401,页面据此提示“家长登录后可以保存进度”。
 * @param {unknown} error axios 错误
 * @return {boolean}
 */
export const isUnauthorized = (error) => error?.response?.status === 401

const MESSAGES = {
  4011: '邮箱或密码不对',
  4012: '尝试次数过多，15 分钟后再试',
  4032: '家长密码不对',
  4092: '这个邮箱已注册'
}

/**
 * 把登录/注册/家长验证的错误转成给家长看的提示。
 * @param {unknown} error axios 错误
 * @param {string} fallback 未知错误时的提示
 * @return {string}
 */
export function authErrorMessage(error, fallback) {
  const data = error?.response?.data
  return MESSAGES[data?.code] || data?.message || fallback
}
