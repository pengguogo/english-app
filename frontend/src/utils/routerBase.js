/**
 * routerBase.js - 路由基础路径工具
 * 用途: 统一处理 Vite `base` 对应的 Router history base，避免预览地址带子路径时无法匹配首页路由。
 * 作者: english-app
 * 创建日期: 2026-07-30
 */

/**
 * 规范化路由基础路径。
 * @param {string} baseUrl Vite 注入的基础路径
 * @return {string} 可供 createWebHistory 使用的基础路径
 */
export function normalizeRouterBase(baseUrl) {
  if (!baseUrl) return '/'
  return baseUrl
}

