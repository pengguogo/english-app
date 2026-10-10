import { watch } from 'vue'
import { readLessonBookmark, saveLessonBookmark, clearLessonBookmark } from '../utils/lessonBookmark'

export function useLessonBookmark(lesson, loading, index, scores, engaged, complete, route) {
  watch([index, scores, engaged, complete], () => {
    if (loading.value || !lesson.value) return
    try {
      if (complete.value) clearLessonBookmark(localStorage, lesson.value.id)
      else saveLessonBookmark(localStorage, lesson.value, index.value, scores.value, engaged.value, route.query)
    } catch { /* 浏览器禁用存储时仍可学习。 */ }
  }, { deep: true, flush: 'sync' })
  return () => {
    try {
      const saved = readLessonBookmark(localStorage, lesson.value)
      if (!saved) return
      index.value = saved.index
      scores.value = saved.scores
      engaged.value = saved.engaged
    } catch { /* 无书签时从第一项开始。 */ }
  }
}
