<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getWrongAnswers } from '../api/wrongAnswer'
import { useWrongAnswerPractice } from '../composables/useWrongAnswerPractice'
import { getLessonById } from '../api/lesson'
import BackBar from '../components/BackBar.vue'
import AppButton from '../components/AppButton.vue'
import QuizLesson from '../components/lesson-templates/QuizLesson.vue'
import CalculateLesson from '../components/lesson-templates/CalculateLesson.vue'

const route = useRoute()
const router = useRouter()
const entry = ref(null)
const item = ref(null)
const loading = ref(true)
const attempt = ref(0)
const { error, saving, mastered, canSave, answered, save } = useWrongAnswerPractice(entry, item)
const template = computed(() => entry.value?.questionType === 'QUIZ' ? QuizLesson : CalculateLesson)

onMounted(async () => {
  try {
    const entries = await getWrongAnswers()
    const found = entries.find(value => value.id === Number(route.params.id))
    if (!found || !['QUIZ', 'CALCULATE'].includes(found.questionType)) throw new Error('这道题暂不支持单题重练，请返回错题集')
    const lesson = await getLessonById(found.lessonId)
    const content = typeof lesson.content === 'string' ? JSON.parse(lesson.content) : lesson.content
    const current = content.items?.[found.questionIndex]
    if (!current || lesson.type !== found.questionType) throw new Error('课程题目已变化，请返回整课复习')
    entry.value = found
    item.value = current
  } catch (e) { error.value = e.message || '加载失败，请返回重试' }
  finally { loading.value = false }
})

function retry() {
  if (saving.value || canSave.value) return
  attempt.value++
  error.value = ''
}
</script>

<template>
  <main class="practice-page">
    <BackBar title="错题单题重练" @back="router.replace('/wrong-answers')" />
    <p v-if="loading" role="status">正在加载题目...</p>
    <p v-if="error" role="alert">{{ error }}</p>
    <section v-if="mastered" role="status">
      <p>答对了，已更新为掌握！</p>
      <AppButton @click="router.replace('/wrong-answers')">返回错题集</AppButton>
    </section>
    <template v-else-if="item">
      <component :is="template" :key="attempt" :current-item="item" :current-index="0"
        :total-items="1" :is-last-item="true" last-action-label="再练一次" @answered="answered" @next="retry" />
      <p v-if="saving" role="status">正在保存掌握状态...</p>
      <AppButton v-if="error && canSave" :disabled="saving" @click="save">重试保存</AppButton>
    </template>
  </main>
</template>

<style scoped>
.practice-page { padding: var(--space-4); display: grid; gap: var(--space-4); }
</style>
