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

/**
 * 获取已学课时列表(状态为 IN_PROGRESS 或 COMPLETED)。
 * 用于"我学过的"页面展示历史学习记录,支持复习跳转。
 *
 * @return {Promise<Array>} 已学课时列表,每项包含课时名/类型/分数/星星/完成时间/所属层级等
 */
export const getLearnedLessons = () =>
  http.get('/progress/learned', { params: { userId: 1 } })

/**
 * 获取学习统计数据(已学课时数/总星星/平均分/学科分布)。
 * 用于"我学过的"页面顶部统计卡片与学科分布展示。
 *
 * @return {Promise<Object>} 统计对象 { learnedCount, totalStars, averageScore, subjectDistribution }
 */
export const getLearnedStats = () =>
  http.get('/progress/learned/stats', { params: { userId: 1 } })
