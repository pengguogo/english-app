// 本机书签只记阅读位置；站点完成以服务端结果为准。
export function readBookmark(storage, unitId, lessons) {
  try {
    const value = JSON.parse(storage.getItem(`picturebook:${unitId}`) || 'null')
    const lesson = lessons.find((entry) => entry.id === value?.lessonId)
    if (!lesson || !Number.isInteger(value.index)) return null
    const scores = Array.isArray(value.scores) ? value.scores.slice(0, lesson.content.items.length).map((score) => [0, 70, 100].includes(score) ? score : 0) : []
    return { lessonId: lesson.id, index: Math.max(0, Math.min(value.index, lesson.content.items.length - 1)), scores }
  } catch { return null }
}

export function writeBookmark(storage, unitId, lessonId, index, scores = []) {
  try {
    storage.setItem(`picturebook:${unitId}`, JSON.stringify({ lessonId, index, scores }))
    return true
  } catch { return false }
}

export function nextUnfinished(lessons, completedIds) {
  return lessons.findIndex((lesson) => !completedIds.includes(lesson.id))
}

export function quizScore(scores, count) {
  return count ? Math.round(scores.reduce((sum, score) => sum + (score || 0), 0) / count) : 0
}
