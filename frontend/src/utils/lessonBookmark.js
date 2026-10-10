// 本机学习草稿与服务端完成记录独立；内容变化时丢弃旧草稿。
export function readLessonBookmark(storage, lesson) {
  try {
    const saved = JSON.parse(storage.getItem(`lesson:${lesson.id}`) || 'null')
    if (saved?.content !== JSON.stringify(lesson.content) || !Number.isInteger(saved.index)) return null
    const count = lesson.content.items.length
    const scores = Array.from({ length: count }, (_, i) => {
      const score = saved.scores?.[i]
      return Number.isFinite(score) && score >= 0 && score <= 100 ? score
        : (['WORD', 'SENTENCE', 'PHONICS', 'DIALOGUE'].includes(lesson.type) ? null : 0)
    })
    return { index: Math.max(0, Math.min(saved.index, count - 1)), scores,
      engaged: Array.from({ length: count }, (_, i) => saved.engaged?.[i] === true) }
  } catch { return null }
}

export function saveLessonBookmark(storage, lesson, index, scores, engaged, query) {
  try {
    storage.setItem(`lesson:${lesson.id}`, JSON.stringify({
      content: JSON.stringify(lesson.content), index, scores, engaged
    }))
    storage.setItem('lastLesson', JSON.stringify({ id: lesson.id, name: lesson.name, query }))
  } catch { /* 存储不可用时仍可正常学习。 */ }
}

export function clearLessonBookmark(storage, id) {
  try {
    storage.removeItem(`lesson:${id}`)
    if (JSON.parse(storage.getItem('lastLesson') || 'null')?.id === id) storage.removeItem('lastLesson')
  } catch { /* 不影响服务端完成记录。 */ }
}
