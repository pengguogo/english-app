<!-- UnitView.vue - 课时列表页:展示某单元下的所有课时 -->
<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getLessonsByUnit } from '../api/lesson'

const route = useRoute()
const router = useRouter()
const lessons = ref([])
const isLoading = ref(true)
const errorMsg = ref('')

onMounted(async () => {
  const unitId = route.params.unitId
  try {
    lessons.value = await getLessonsByUnit(unitId)
  } catch (e) {
    errorMsg.value = '加载失败,请返回重试'
    console.error('加载课时失败:', e)
  } finally {
    isLoading.value = false
  }
})
</script>

<template>
  <div class="unit-view">
    <button class="back-btn" @click="router.back()">← 返回</button>
    <h1>课时列表</h1>
    <div v-if="isLoading" class="loading">加载中...</div>
    <div v-else-if="errorMsg" class="error">{{ errorMsg }}</div>
    <div v-else class="lesson-list">
      <div
        v-for="(lesson, index) in lessons"
        :key="lesson.id"
        class="lesson-card"
        :class="{ locked: index > 0 }"
        @click="index === 0 && router.push(`/lesson/${lesson.id}`)"
      >
        <div class="lesson-num">{{ index + 1 }}</div>
        <div class="lesson-info">
          <h3>{{ lesson.name }}</h3>
          <p>{{ lesson.type === 'WORD' ? '单词课' : '句型课' }}</p>
        </div>
        <span v-if="index === 0" class="status">▶</span>
        <span v-else class="status">🔒</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.back-btn { background: none; border: none; font-size: 1rem; color: #667eea; cursor: pointer; margin-bottom: 12px; }
h1 { margin-bottom: 24px; }
.lesson-list { display: flex; flex-direction: column; gap: 12px; }
.lesson-card {
  display: flex; align-items: center; gap: 16px;
  background: white; border-radius: 12px; padding: 16px;
  cursor: pointer; transition: transform 0.2s;
}
.lesson-card:hover { transform: translateX(4px); }
.lesson-card.locked { opacity: 0.6; cursor: not-allowed; }
.lesson-num {
  width: 40px; height: 40px; border-radius: 50%;
  background: #667eea; color: white; display: flex;
  align-items: center; justify-content: center; font-weight: bold;
}
.lesson-info { flex: 1; }
.lesson-info h3 { margin-bottom: 4px; }
.lesson-info p { color: #888; font-size: 0.85rem; }
.status { font-size: 1.5rem; }
.loading, .error { text-align: center; padding: 40px; color: #888; }
.error { color: #f44336; }
</style>
