/**
 * date.js - 日期工具函数
 * 用途: 提供成长档案等模块共用的日期格式化与年龄计算,避免各组件重复实现。
 * 作者: english-app
 * 创建日期: 2026-09-30
 */

/**
 * 返回今天的日期字符串(格式 YYYY-MM-DD)。
 * @returns {String} 今天的日期,如 2026-09-30
 */
export function today() {
  const date = new Date()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${date.getFullYear()}-${month}-${day}`
}

/**
 * 根据生日计算年龄文案(如 "3 岁 2 个月"),按当前日期实时推算。
 * @param {String} birthDate 生日字符串(格式 YYYY-MM-DD),可为空
 * @returns {String} 年龄文案;生日为空返回空串,晚于今天返回 "生日不能晚于今天"
 */
export function formatAge(birthDate) {
  if (!birthDate) return ''
  const [year, month, day] = birthDate.split('-').map(Number)
  const now = new Date()
  let years = now.getFullYear() - year
  let months = now.getMonth() + 1 - month
  if (now.getDate() < day) months--
  if (months < 0) { years--; months += 12 }
  if (years < 0) return '生日不能晚于今天'
  return `${years} 岁 ${months} 个月`
}
