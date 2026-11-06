/**
 * @file learningContext.js - 当前课程学习上下文(给学习伙伴面板读)
 * @description 课程学习流程在切换学习项时写入 {lessonId, itemIndex},离开课程页时清空。
 *  面板发 /agent/chat 时把它带进 context。只是一个模块级 ref,不需要 pinia。
 */
import { ref } from 'vue'

export const learningContext = ref(null)

/**
 * 写入当前学习项;lessonId 无效时视为不在课程中。
 * @param {number|string} lessonId 课程 id
 * @param {number} itemIndex 学习项下标
 */
export function setLearningContext(lessonId, itemIndex) {
  const id = Number(lessonId)
  learningContext.value = Number.isInteger(id) && id > 0
    ? { lessonId: id, itemIndex: Number(itemIndex) || 0 }
    : null
}

/** 离开课程页时清空 */
export function clearLearningContext() {
  learningContext.value = null
}
