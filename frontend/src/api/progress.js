/**
 * @file 学习进度相关 API
 * @description 对应后端:
 *  - GET  /api/v1/progress/lesson/{lessonId}              获取课时进度
 *  - GET  /api/v1/progress/unit/{unitId}                  批量获取单元内所有课时进度
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
 * @return {Promise<Object>} 课时进度信息 { lessonId, status, stars, score }
 */
export const getLessonProgress = (lessonId) =>
  http.get(`/progress/lesson/${lessonId}`)

/**
 * 批量获取某单元下所有课时的进度。
 * 一次请求返回整单元进度,供课时列表页渲染锁定/可学/已完成状态,避免逐课 N+1 请求。
 *
 * @param {number|string} unitId 单元 ID
 * @return {Promise<Array>} 课时进度列表 [{ lessonId, status, stars, score }]
 */
export const getUnitProgress = (unitId) =>
  http.get(`/progress/unit/${unitId}`)

/**
 * 提交课时完成,记录获得星星数与分数。
 *
 * @param {number|string} lessonId 课时 ID
 * @param {number} stars 获得星星数(1-3)
 * @param {number} score 得分
 * @return {Promise<Object>} 完成结果 { nextLessonId, unlocked }
 */
export const completeLesson = (lessonId, stars, score) =>
  http.post(`/progress/lesson/${lessonId}/complete`, { stars, score })
