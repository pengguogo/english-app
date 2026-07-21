/**
 * @file 错题相关 API
 * @description 对应后端:
 *  - POST  /api/v1/wrong-answers/record        记录错题
 *  - GET   /api/v1/wrong-answers                获取错题列表(可选筛选 resolved)
 *  - GET   /api/v1/wrong-answers/stats          获取错题统计
 *  - PATCH /api/v1/wrong-answers/{id}/resolve   标记错题为已掌握
 *  - DELETE /api/v1/wrong-answers/{id}          删除错题
 * @author english-app
 * @since 2026-07-21
 */
import http from './http'

/**
 * 记录一条错题。
 * 调用时机:用户答错 QUIZ/CALCULATE 类型题目时自动上报。
 *
 * @param {Object} data 错题数据,包含 lessonId/lessonName/questionIndex/questionType/questionSnapshot/userAnswer/correctAnswer
 * @return {Promise<Object>} 创建结果
 */
export function recordWrongAnswer(data) {
  return http.post('/wrong-answers/record', data, { params: { userId: 1 } })
}

/**
 * 获取错题列表,可按是否已掌握筛选。
 *
 * @param {boolean|null} resolved 是否已掌握(null=全部,true=已掌握,false=未掌握)
 * @return {Promise<Array>} 错题列表
 */
export function getWrongAnswers(resolved = null) {
  const params = { userId: 1 }
  if (resolved !== null) params.resolved = resolved
  return http.get('/wrong-answers', { params })
}

/**
 * 获取错题统计数据(总数/未掌握/已掌握)。
 *
 * @return {Promise<Object>} 统计对象 { total, unresolved, resolved }
 */
export function getWrongAnswerStats() {
  return http.get('/wrong-answers/stats', { params: { userId: 1 } })
}

/**
 * 标记某条错题为已掌握。
 *
 * @param {number|string} id 错题 ID
 * @return {Promise<Object>} 更新结果
 */
export function resolveWrongAnswer(id) {
  return http.patch(`/wrong-answers/${id}/resolve`, null, { params: { userId: 1 } })
}

/**
 * 删除某条错题。
 *
 * @param {number|string} id 错题 ID
 * @return {Promise<Object>} 删除结果
 */
export function deleteWrongAnswer(id) {
  return http.delete(`/wrong-answers/${id}`, { params: { userId: 1 } })
}
