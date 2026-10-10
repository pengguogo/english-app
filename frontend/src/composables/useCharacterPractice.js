import { computed, ref, watch, onBeforeUnmount } from 'vue'
import { characterChoices, recognitionOutcome } from '../utils/characterPractice'
import { recordCharacterAttempt } from '../api/characters'
import { stopActiveTts } from './useTts'

export function useCharacterPractice(props, emit) {
  const phase = ref(0)
  const choices = ref([])
  const wrong = ref(false)
  const assisted = ref(false)
  const hint = ref(false)
  const answered = ref(false)
  const heard = ref(false)
  const saved = ref(false)
  const saving = ref(false)
  const error = ref('')
  const feedback = ref('')
  const showPinyin = ref(false)
  let eventId = ''
  let revision = 0
  let advanceTimer = null

  function scheduleAdvance() {
    clearTimeout(advanceTimer)
    const version = revision
    advanceTimer = setTimeout(() => {
      if (version === revision) advance()
    }, 900)
  }
  const labels = ['看图认识', '听音选字', '看字选图', '收起提示再认字', '在词句中找字']
  const title = computed(() => labels[phase.value])
  const isLearn = computed(() => phase.value === 0)
  const isImage = computed(() => phase.value === 2)
  const isContext = computed(() => phase.value === 4)
  const needsAudio = computed(() => phase.value === 1 || phase.value === 3)
  const audioText = computed(() => isLearn.value ? props.currentItem.word
    : isContext.value ? `请在句子中找到${props.currentItem.word}` : `请找出${props.currentItem.word}`)

  function reset() {
    revision++
    clearTimeout(advanceTimer)
    stopActiveTts()
    phase.value = props.reviewOnly ? 3 : 0
    choices.value = characterChoices(props.currentItem, props.items)
    wrong.value = assisted.value = hint.value = answered.value = heard.value = saved.value = saving.value = false
    showPinyin.value = false
    error.value = feedback.value = ''
    eventId = crypto.randomUUID()
  }
  watch(() => [props.lessonId, props.currentIndex, props.currentItem.word], reset, { immediate: true })
  onBeforeUnmount(() => { revision++; clearTimeout(advanceTimer); stopActiveTts() })

  function revealHint() {
    if (answered.value || saving.value) return
    assisted.value = true
    hint.value = true
    heard.value = true
  }
  function choose(word) {
    if (answered.value || saving.value || (needsAudio.value && !heard.value)) return
    if (word !== props.currentItem.word) {
      wrong.value = true
      feedback.value = '再听一次，或者看看提示。'
      return
    }
    answered.value = true
    feedback.value = '找对了！'
    if (props.reviewOnly || phase.value === 4) save()
    else scheduleAdvance()
  }
  function advance() {
    if (saving.value || (!isLearn.value && !answered.value && !saved.value)) return
    clearTimeout(advanceTimer)
    stopActiveTts()
    if (saved.value) { emit('next'); return }
    if (props.reviewOnly || phase.value === 4) { save(); return }
    phase.value++
    choices.value = characterChoices(props.currentItem, props.items)
    answered.value = heard.value = hint.value = false
    showPinyin.value = false
    feedback.value = ''
  }
  async function save(skip = false) {
    if (saving.value || saved.value) return
    if (skip) wrong.value = true
    const version = revision
    const outcome = recognitionOutcome(wrong.value, assisted.value)
    saving.value = true
    error.value = ''
    try {
      await recordCharacterAttempt({ eventId, lessonId: props.lessonId,
        itemIndex: props.currentIndex, outcome })
      if (version !== revision) return
      saved.value = true
      emit('answered', { correct: true, score: outcome === 'INDEPENDENT' ? 100 : outcome === 'ASSISTED' ? 60 : 40,
        assisted: outcome !== 'INDEPENDENT', firstWrong: wrong.value,
        userAnswer: outcome, correctAnswer: props.currentItem.word })
      feedback.value = outcome === 'INDEPENDENT' ? '独立认对，隔天再来认一认。' : '已经记入待巩固，明天再练。'
      if (skip) emit('next')
      else scheduleAdvance()
    } catch (e) {
      if (version === revision) error.value = '保存失败，请重试；认读结果不会被重复计数。'
    } finally {
      if (version === revision) saving.value = false
    }
  }
  return { phase, choices, hint, answered, heard, saved, saving, error, feedback, showPinyin,
    title, isLearn, isImage, isContext, needsAudio, audioText, revealHint, choose, advance, save }
}
