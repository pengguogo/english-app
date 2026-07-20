/**
 * @file 学习进度相关 API
 * @description 对应后端:
 *  - GET  /api/v1/progress/lesson/{lessonId}              获取课时进度
 *  - POST /api/v1/progress/lesson/{lessonId}/complete      完成课时
 * @author english-app
 * @since 2026-07-20
 */
import http from './http'

/**
 * 获取某课时的学习进度。
 * 当前按统一 userId=1 处理,后续接入登录态后需补充 userId 参数。
 *
 * @param {number|string} lessonId 课时 ID
 * @return {Promise<Object>} 课时进度信息
 */
export const getLessonProgress = (lessonId) =>
  http.get(`/progress/lesson/${lessonId}`)

/**
 * 提交课时完成,记录获得星星数与分数。
 *
 * @param {number|string} lessonId 课时 ID
 * @param {number} stars 获得星星数(1-3)
 * @param {number} score 得分
 * @return {Promise<Object>} 完成结果
 */
export const completeLesson = (lessonId, stars, score) =>
  http.post(`/progress/lesson/${lessonId}/complete`, { stars, score })
