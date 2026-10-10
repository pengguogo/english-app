import { watch } from 'vue'
import { recordLearningAttempt } from '../api/learning'

/** 同一次练习保留首次结果，提示后答对只更新辅助掌握；切题开始新事件。 */
export function useLearningAttempts(lesson, index) {
  let event = null
  let pending = Promise.resolve()
  watch([lesson, index], () => { event = null }, { flush: 'sync' })
  function recordAttempt(result) {
    if (!['QUIZ', 'CALCULATE'].includes(lesson.value?.type)) return
    if (!event) event = { eventId: crypto.randomUUID(), lessonId: lesson.value.id,
      questionIndex: index.value, firstCorrect: result.correct && !result.assisted, assistedCorrect: false }
    if (result.correct && result.assisted) event.assistedCorrect = true
    const request = { ...event }
    pending = pending.then(() => recordLearningAttempt(request)).catch(error => {
      console.error('保存答题统计失败:', error)
    })
  }
  return { recordAttempt, resetAttempts: () => { event = null } }
}
