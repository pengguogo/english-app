/**
 * @file 主题相关 API
 * @description 对应后端 GET /api/v1/themes,获取所有学习主题。
 * @author english-app
 * @since 2026-07-20
 */
import http from './http'

/**
 * 获取所有主题列表。
 * 拦截器已剥离 Result 外层,直接返回 data(主题数组)。
 *
 * @return {Promise<Array>} 主题列表
 */
export const getThemes = () => http.get('/themes')
