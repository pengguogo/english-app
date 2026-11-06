/**
 * @file 单元相关 API
 * @description 对应后端 GET /api/v1/units/theme/{themeId},获取某主题下的单元列表(含进度)。
 * @author english-app
 * @since 2026-07-20
 */
import http from './http'

/**
 * 根据主题 ID 获取单元列表。
 * 进度按当前会话中选中的孩子计算(后端从 Session 取)。
 *
 * @param {number|string} themeId 主题 ID
 * @return {Promise<Array>} 单元列表(含学习进度)
 */
export const getUnitsByTheme = (themeId) => http.get(`/units/theme/${themeId}`)
