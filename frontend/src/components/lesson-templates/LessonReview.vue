<script setup>
import { onMounted, ref } from 'vue'
import AppButton from '../AppButton.vue'
import { getReviewQuestions, submitReview } from '../../api/review'

const props = defineProps({
  lessonId: { type: Number, required: true },
  saveError: { type: String, default: '' }
})
const emit = defineEmits(['passed'])
const questions = ref([])
const answers = ref([])
const wrong = ref([])
const loading = ref(false)
const error = ref('')

async function load() {
  loading.value = true
  error.value = ''
  try {
    questions.value = await getReviewQuestions(props.lessonId)
    answers.value = new Array(questions.value.length).fill(null)
  } catch (e) {
    error.value = '复习题加载失败，请重试'
  } finally {
    loading.value = false
  }
}

async function submit() {
  if (loading.value || answers.value.some(answer => answer === null)) return
  loading.value = true
  error.value = ''
  try {
    const result = await submitReview(props.lessonId, answers.value)
    if (result.passed) {
      emit('passed')
    } else {
      wrong.value = result.wrongIndexes
      error.value = '还有题目没答对，再试一次'
    }
  } catch (e) {
    error.value = '提交失败，请重试'
  } finally {
    loading.value = false
  }
}

onMounted(load)

function imageUrl(image) {
  if (!image) return ''
  return image.startsWith('/images/') ? image : `/images/${image}.jpg`
}
</script>

<template>
  <section class="review">
    <h2>课后复习</h2>
    <p>全部答对后，本课学习时间才会计入游戏解锁进度。</p>
    <p v-if="saveError" role="alert">{{ saveError }}</p>
    <p v-if="error" role="alert">{{ error }}</p>
    <p v-if="loading && !questions.length" role="status">正在加载复习题…</p>
    <AppButton v-if="!questions.length && !loading" variant="ghost" @click="load">重新加载</AppButton>
    <div v-for="question in questions" :key="question.index" class="review-question">
      <h3>{{ question.prompt }}</h3>
      <img v-if="question.image" :src="imageUrl(question.image)" alt="本题配图" />
      <div v-for="(option, index) in question.options" :key="index">
        <label>
          <input v-model="answers[question.index]" type="radio" :name="`review-${question.index}`" :value="index" />
          <img v-if="option.image" :src="imageUrl(option.image)" :alt="option.text" />
          <span v-if="option.showText">{{ option.text }}</span>
        </label>
      </div>
      <p v-if="wrong.includes(question.index)">这题再想一想</p>
    </div>
    <AppButton :disabled="loading || !questions.length || answers.some(answer => answer === null)" @click="submit">
      {{ loading ? '提交中…' : '提交复习' }}
    </AppButton>
  </section>
</template>

<style scoped>
.review { padding: var(--space-6); background: var(--bg-card); border-radius: var(--radius-lg); }
.review h2 { color: var(--text-primary); margin-bottom: var(--space-3); }
.review > p { color: var(--text-secondary); margin-bottom: var(--space-4); }
.review-question { padding: var(--space-4) 0; border-top: 1px solid var(--border-light); }
.review-question h3 { margin-bottom: var(--space-3); }
.review-question img { display: block; max-width: 240px; max-height: 180px; object-fit: contain; margin-bottom: var(--space-3); }
.review-question label { display: block; padding: var(--space-2); cursor: pointer; }
.review-question input { margin-right: var(--space-2); }
</style>
