import { computed, onBeforeUnmount, ref, watch } from 'vue'
import { getLessonById, getLessonsByUnit } from '../api/lesson'
import { completeLesson, getUnitProgress } from '../api/progress'
import { nextUnfinished, quizScore, readBookmark, writeBookmark } from '../utils/picturebookProgress'
import { stopActiveTts } from './useTts'

export function usePicturebookJourney(route) {
  const lessons = ref([])
  const completedIds = ref([])
  const active = ref(-1)
  const index = ref(0)
  const loading = ref(true)
  const error = ref('')
  const saving = ref(false)
  const saveError = ref('')
  const arrival = ref(false)
  const scores = ref([])
  const bookmarkError = ref('')
  let loadVersion = 0
  let activityVersion = 0
  const lesson = computed(() => lessons.value[active.value])
  const item = computed(() => lesson.value?.content.items[index.value])
  const allComplete = computed(() => lessons.value.length > 0 && lessons.value.every((entry) => completedIds.value.includes(entry.id)))

  function bookmark() {
    if (!lesson.value) return
    try {
      if (!writeBookmark(localStorage, route.params.unitId, lesson.value.id, index.value, scores.value)) throw new Error()
    } catch { bookmarkError.value = '本机书签暂时无法保存，已完成的站点仍会保存到学习记录。' }
  }

  async function load() {
    const version = ++loadVersion
    stopActiveTts()
    loading.value = true
    error.value = ''
    active.value = -1
    arrival.value = false
    try {
      const [list, progress] = await Promise.all([getLessonsByUnit(route.params.unitId), getUnitProgress(route.params.unitId)])
      const details = await Promise.all(list.map((entry) => getLessonById(entry.id)))
      if (version !== loadVersion) return
      lessons.value = details.map((entry) => ({ ...entry, content: typeof entry.content === 'string' ? JSON.parse(entry.content) : entry.content }))
        .filter((entry) => entry.content?.picturebook === true && entry.content.items?.length)
      if (!lessons.value.length) throw new Error('绘本不存在')
      completedIds.value = progress.filter((entry) => entry.status === 'COMPLETED').map((entry) => entry.lessonId)
      const requested = lessons.value.findIndex((entry) => entry.id === Number(route.query.lesson))
      if (requested >= 0) start(requested)
    } catch { if (version === loadVersion) error.value = '绘本列车加载失败，请重试。' }
    finally { if (version === loadVersion) loading.value = false }
  }

  function start(position) {
    activityVersion++
    stopActiveTts()
    let saved = null
    try { saved = readBookmark(localStorage, route.params.unitId, lessons.value) } catch { /* 禁用存储时仍可学习 */ }
    active.value = position
    index.value = saved?.lessonId === lesson.value.id ? saved.index : 0
    scores.value = saved?.lessonId === lesson.value.id ? saved.scores : []
    arrival.value = false
    saveError.value = ''
    bookmark()
  }

  function resume() {
    let saved = null
    try { saved = readBookmark(localStorage, route.params.unitId, lessons.value) } catch { /* 使用服务端已完成站点 */ }
    const unfinished = nextUnfinished(lessons.value, completedIds.value)
    const shouldResume = saved && (saved.index > 0 || !completedIds.value.includes(saved.lessonId) || unfinished < 0)
    const position = shouldResume ? lessons.value.findIndex((entry) => entry.id === saved.lessonId) : unfinished
    start(Math.max(0, position))
  }

  function move(delta) {
    if (saving.value) return
    stopActiveTts()
    index.value = Math.max(0, Math.min(index.value + delta, lesson.value.content.items.length - 1))
    bookmark()
  }

  function answer(score) {
    scores.value[index.value] = Math.max(scores.value[index.value] || 0, score)
    bookmark()
  }

  async function finish() {
    if (saving.value) return
    stopActiveTts()
    saving.value = true
    saveError.value = ''
    const source = lesson.value
    const version = loadVersion
    const activity = activityVersion
    try {
      const score = source.type === 'QUIZ' ? quizScore(scores.value, source.content.items.length) : 0
      await completeLesson(source.id, 3, score)
      if (version !== loadVersion) return
      if (!completedIds.value.includes(source.id)) completedIds.value.push(source.id)
      if (activity !== activityVersion) return
      index.value = 0
      scores.value = []
      bookmark()
      arrival.value = true
    } catch { if (version === loadVersion) saveError.value = '车票还没保存成功，请点“重试保存”。' }
    finally { saving.value = false }
  }

  function timetable() { activityVersion++; stopActiveTts(); active.value = -1; arrival.value = false }
  watch(() => route.params.unitId, load, { immediate: true })
  onBeforeUnmount(() => { loadVersion++; stopActiveTts() })
  return { lessons, completedIds, active, index, lesson, item, loading, error, saving, saveError, bookmarkError,
    arrival, allComplete, load, start, resume, move, answer, finish, timetable }
}
