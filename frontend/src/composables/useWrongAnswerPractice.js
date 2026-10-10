import { ref } from 'vue'
import { recordWrongAnswer, resolveWrongAnswer } from '../api/wrongAnswer'

/** 单题重练按错误写入→掌握更新的顺序保存，失败时保留待重试操作。 */
export function useWrongAnswerPractice(entry, item) {
  const error = ref('')
  const saving = ref(false)
  const mastered = ref(false)
  const canSave = ref(false)
  let wrongRequest = null
  let pendingWrite = Promise.resolve(null)

  function writeWrong(request = wrongRequest) {
    return recordWrongAnswer(request).then(() => {
      if (wrongRequest === request) wrongRequest = null
      return null
    }, e => e)
  }

  async function save() {
    if (saving.value || mastered.value || !canSave.value) return
    saving.value = true
    error.value = ''
    try {
      await pendingWrite
      if (wrongRequest) {
        const failure = await writeWrong()
        if (failure) throw failure
      }
      await resolveWrongAnswer(entry.value.id)
      mastered.value = true
    } catch { error.value = '保存失败，本题尚未标记掌握，请重试保存' }
    finally { saving.value = false }
  }

  function answered(result) {
    if (saving.value || mastered.value) return
    if (result.firstWrong) {
      wrongRequest = { ...entry.value, questionSnapshot: JSON.stringify(item.value),
        userAnswer: String(result.userAnswer), correctAnswer: String(result.correctAnswer) }
      const request = wrongRequest
      pendingWrite = pendingWrite.then(() => writeWrong(request))
    }
    canSave.value = result.correct
    if (result.correct) return save()
  }

  return { error, saving, mastered, canSave, answered, save }
}
