<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import BackBar from '../components/BackBar.vue'
import AppButton from '../components/AppButton.vue'
import AudioButton from '../components/AudioButton.vue'
import CharacterLesson from '../components/lesson-templates/CharacterLesson.vue'
import { getCharacterProgress, getCharacterReview } from '../api/characters'

const router = useRouter()
const progress = ref([])
const queue = ref([])
const index = ref(0)
const active = ref(false)
const loading = ref(false)
const error = ref('')
const finished = ref(false)
const current = computed(() => queue.value[index.value])
const counts = computed(() => ({ learned: progress.value.filter(p => p.status !== 'NEW').length,
  reviewing: progress.value.filter(p => p.status === 'REVIEWING').length,
  recognized: progress.value.filter(p => p.status === 'RECOGNIZED').length }))
const statusLabels = { NEW: '未学习', REVIEWING: '待巩固', RECOGNIZED: '跨天认对' }

async function load() {
  loading.value = true
  error.value = ''
  try {
    const results = await Promise.all([getCharacterProgress(), getCharacterReview()])
    progress.value = results[0]
    queue.value = results[1]
  } catch (e) { error.value = '加载失败，请重试' }
  finally { loading.value = false }
}
function start() { index.value = 0; finished.value = false; active.value = true }
async function next() {
  if (index.value < queue.value.length - 1) index.value++
  else { active.value = false; finished.value = true; await load() }
}
function learn(item) {
  router.push({ path: `/lesson/${item.lessonId}`, query: { subjectId: '2', subjectName: '语文', themeId: '10', themeName: '汉字识读' } })
}
onMounted(load)
</script>

<template>
  <main class="character-page">
    <BackBar title="认字小课堂" @back="router.push('/')" />
    <p v-if="error" role="alert">{{ error }}</p>
    <AppButton v-if="error" variant="ghost" @click="load">重新加载</AppButton>
    <p v-if="loading" role="status">正在整理认字记录…</p>
    <CharacterLesson v-if="active && current" :key="`${current.lessonId}-${current.itemIndex}`"
      :current-item="current" :current-index="current.itemIndex" :lesson-id="current.lessonId"
      :items="progress.map(p => p.item)" :is-last-item="index === queue.length - 1" review-only @next="next" />
    <section v-else-if="!loading && !error">
      <h1>每天认一认</h1>
      <p v-if="finished" role="status">这一轮复习完成，认字记录已保存。</p>
      <p>接触 {{ counts.learned }} 字 · 待巩固 {{ counts.reviewing }} 字 · 跨天认对 {{ counts.recognized }} 字</p>
      <p>提示后答对和认错都会进入巩固；在三个不同日期独立认对，才标为跨天认对。</p>
      <AppButton v-if="queue.length" @click="start">开始今日复习（{{ queue.length }} 字）</AppButton>
      <p v-else>今天暂时没有到期复习，可以选择下面一课认识新字。</p>
      <AudioButton text="点开始今日复习，听声音找字。也可以点下面的课程，认识新字。" lan="zh" />
      <h2>{{ progress.filter(p => p.item.itemIndex === 0).length }} 课 · {{ progress.length }} 字</h2>
      <p>选字依据：人教版统编语文一年级上册（2024 年修订版）识字表。按兴趣选学，不要求大班提前学完。</p>
      <div class="lessons">
        <AppButton v-for="row in progress.filter(p => p.item.itemIndex === 0)" :key="row.item.lessonId"
          variant="ghost" @click="learn(row.item)">
          <img :src="row.item.image" alt="课程配图" />
          <span>{{ progress.filter(p => p.item.lessonId === row.item.lessonId).map(p => p.item.word).join(' · ') }}</span>
        </AppButton>
      </div>
      <details>
        <summary>家长查看：每个字的学习记录</summary>
        <ul>
          <li v-for="row in progress" :key="row.item.word">
            {{ row.item.word }}：{{ statusLabels[row.status] }}；独立认对 {{ row.independentDays }} 天，
            认错 {{ row.wrongCount }} 次，提示 {{ row.assistedCount }} 次
            <span v-if="row.dueDate">；下次复习 {{ row.dueDate }}</span>
          </li>
        </ul>
      </details>
    </section>
  </main>
</template>

<style scoped>
.character-page { max-width: 760px; margin: auto; padding: var(--space-6); padding-top: var(--space-8); color: var(--text-primary); }
section { display: grid; gap: var(--space-4); }
p { color: var(--text-secondary); }
.lessons { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: var(--space-3); }
.lessons :deep(.app-btn) { flex-direction: column; min-width: 0; }
.lessons img { width: 100px; height: 100px; object-fit: contain; border-radius: var(--radius-md); }
li { margin: var(--space-2) 0; }
@media (max-width: 480px) { .character-page { padding: var(--space-4); padding-top: var(--space-8); } .lessons { grid-template-columns: 1fr; } }
</style>
