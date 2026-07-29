/**
 * 按单元与课时顺序查找下一节阅读课。
 *
 * @param {number|string} currentLessonId 当前课时 ID
 * @param {number|string} currentUnitId 当前单元 ID
 * @param {Array<Object>} units 已按学习顺序排列的单元
 * @param {(unitId: number|string) => Promise<Array<Object>>} loadLessons 加载单元课时的方法
 * @return {Promise<{lesson: Object, unitId: number|string}|null>}
 */
export async function findNextReadingLesson(
  currentLessonId,
  currentUnitId,
  units,
  loadLessons
) {
  const currentUnitIndex = units.findIndex(
    unit => Number(unit.id) === Number(currentUnitId)
  )
  if (currentUnitIndex < 0) return null

  for (let unitIndex = currentUnitIndex; unitIndex < units.length; unitIndex++) {
    const unit = units[unitIndex]
    const lessons = await loadLessons(unit.id)
    const currentLessonIndex = unitIndex === currentUnitIndex
      ? lessons.findIndex(lesson => Number(lesson.id) === Number(currentLessonId))
      : -1
    if (unitIndex === currentUnitIndex && currentLessonIndex < 0) return null
    const startIndex = unitIndex === currentUnitIndex ? currentLessonIndex + 1 : 0
    const nextLesson = lessons
      .slice(startIndex)
      .find(lesson => lesson.type === 'READING')

    if (nextLesson) {
      return { lesson: nextLesson, unitId: unit.id }
    }
  }

  return null
}
