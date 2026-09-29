/**
 * @file 主题/单元新版页面共享逻辑
 * @description 统一管理课时类型标签、进度状态与推荐课时选择，避免新版页面重复实现。
 * @author TRAE Agent
 * @since 2026-09-29
 */

/**
 * 课时类型展示元数据。
 */
const LESSON_TYPE_META = {
  WORD: { text: '单词练习', short: '词' },
  SENTENCE: { text: '句型练习', short: '句' },
  READING: { text: '阅读练习', short: '读' },
  QUIZ: { text: '问答挑战', short: '答' },
  CALCULATE: { text: '计算练习', short: '算' },
  PHONICS: { text: '自然拼读', short: '拼' },
  DIALOGUE: { text: '对话任务', short: '说' }
}

/**
 * 获取课时类型的完整文案。
 * @param {string} type 课时类型
 * @return {string} 课时类型中文文案
 */
export function getLessonTypeText(type) {
  return LESSON_TYPE_META[type]?.text || '课程'
}

/**
 * 获取贴纸徽章里的简短文案。
 * @param {string} type 课时类型
 * @return {string} 贴纸短文案
 */
export function getLessonTypeShort(type) {
  return LESSON_TYPE_META[type]?.short || '课'
}

/**
 * 根据进度对象返回统一状态。
 * @param {{status?: string}=} progress 课时进度
 * @return {'completed'|'in_progress'|'not_started'} 状态标识
 */
export function getLessonStatus(progress) {
  if (progress?.status === 'COMPLETED') return 'completed'
  if (progress?.status === 'IN_PROGRESS') return 'in_progress'
  return 'not_started'
}

/**
 * 获取课时状态文案。
 * @param {{status?: string}=} progress 课时进度
 * @return {{label: string, action: string}} 状态说明与按钮提示
 */
export function getLessonStatusCopy(progress) {
  const status = getLessonStatus(progress)

  if (status === 'completed') {
    return { label: '已完成', action: '再学一次' }
  }

  if (status === 'in_progress') {
    return { label: '进行中', action: '继续学习' }
  }

  return { label: '未开始', action: '开始学习' }
}

/**
 * 从课时列表中挑选推荐入口。
 * 优先继续进行中的课时，其次选择第一个未完成课时，最后退回首课。
 * @param {Array} lessons 课时列表
 * @return {Object|null} 推荐课时
 */
export function getRecommendedLesson(lessons = []) {
  if (!Array.isArray(lessons) || lessons.length === 0) return null

  return lessons.find(item => getLessonStatus(item.progress) === 'in_progress')
    || lessons.find(item => getLessonStatus(item.progress) !== 'completed')
    || lessons[0]
}

/**
 * 计算百分比，统一向下保底为 0。
 * @param {number} current 当前值
 * @param {number} total 总值
 * @return {number} 百分比 0-100
 */
export function toPercent(current, total) {
  if (!total) return 0
  return Math.round((current / total) * 100)
}
