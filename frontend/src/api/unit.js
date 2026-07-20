/**
 * @file 单元相关 API
 * @description 对应后端 GET /api/v1/units/theme/{themeId},获取某主题下的单元列表(含进度)。
 * @author english-app
 * @since 2026-07-20
 */
import http from './http'

/**
 * 根据主题 ID 获取单元列表。
 * 注意:后端原接口支持 userId 查询参数,当前前端按统一 userId=1 处理,
 * 后续接入登录态后需替换为真实用户 ID。
 *
 * @param {number|string} themeId 主题 ID
 * @return {Promise<Array>} 单元列表(含学习进度)
 */
export const getUnitsByTheme = (themeId) => http.get(`/units/theme/${themeId}`)
