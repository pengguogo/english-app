/**
 * @file 课时相关 API
 * @description 对应后端 GET /api/v1/lessons/unit/{unitId} 与 GET /api/v1/lessons/{id}。
 * @author english-app
 * @since 2026-07-20
 */
import http from './http'

/**
 * 根据单元 ID 获取该单元下的课时列表。
 *
 * @param {number|string} unitId 单元 ID
 * @return {Promise<Array>} 课时列表
 */
export const getLessonsByUnit = (unitId) => http.get(`/lessons/unit/${unitId}`)

/**
 * 根据课时 ID 获取课时详情(含词汇、句子等学习内容)。
 *
 * @param {number|string} id 课时 ID
 * @return {Promise<Object>} 课时详情
 */
export const getLessonById = (id) => http.get(`/lessons/${id}`)
