/**
 * @file 学科相关 API
 * @description 对应后端 GET /api/v1/subjects，获取所有学科。
 * @author english-app
 * @since 2026-07-21
 */
import http from './http'

/**
 * 获取所有学科列表。
 * 拦截器已剥离 Result 外层，直接返回 data（学科数组）。
 *
 * @return {Promise<Array>} 学科列表
 */
export const getSubjects = () => http.get('/subjects')

/**
 * 获取某学科下的所有主题列表。
 *
 * @param {number} subjectId 学科 ID
 * @return {Promise<Array>} 主题列表
 */
export const getThemesBySubject = (subjectId) => http.get(`/themes/subject/${subjectId}`)
