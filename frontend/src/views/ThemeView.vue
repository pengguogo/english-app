<!-- ThemeView.vue - 单元列表页:展示某主题下的所有单元及学习进度 -->
<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getUnitsByTheme } from '../api/unit'

const route = useRoute()
const router = useRouter()
const units = ref([])
const isLoading = ref(true)
const errorMsg = ref('')

onMounted(async () => {
  const themeId = route.params.themeId
  try {
    units.value = await getUnitsByTheme(themeId)
  } catch (e) {
    errorMsg.value = '加载失败,请返回重试'
    console.error('加载单元失败:', e)
  } finally {
    isLoading.value = false
  }
})
</script>

<template>
  <div class="theme-view">
    <button class="back-btn" @click="router.push('/')">← 返回</button>
    <h1>🚗 交通工具乐园</h1>
    <div v-if="isLoading" class="loading">加载中...</div>
    <div v-else-if="errorMsg" class="error">{{ errorMsg }}</div>
    <div v-else class="unit-list">
      <div
        v-for="unit in units"
        :key="unit.id"
        class="unit-card"
        :class="{ locked: unit.isLocked }"
        @click="!unit.isLocked && router.push(`/unit/${unit.id}`)"
      >
        <div class="unit-header">
          <h2>{{ unit.name }}</h2>
          <span v-if="unit.isLocked">🔒</span>
        </div>
        <p class="progress-text">
          已完成 {{ unit.completedLessons }} / {{ unit.totalLessons }} 课
        </p>
        <div class="progress-bar">
          <div class="progress-fill"
               :style="{ width: (unit.totalLessons ? (unit.completedLessons / unit.totalLessons * 100) : 0) + '%' }">
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.back-btn {
  background: none; border: none; font-size: 1rem;
  color: #667eea; cursor: pointer; margin-bottom: 12px;
}
h1 { text-align: center; margin-bottom: 24px; }
.unit-list { display: flex; flex-direction: column; gap: 16px; }
.unit-card {
  background: white; border-radius: 16px; padding: 20px;
  cursor: pointer; transition: transform 0.2s;
}
.unit-card:hover { transform: translateX(4px); }
.unit-card.locked { opacity: 0.6; cursor: not-allowed; }
.unit-header { display: flex; justify-content: space-between; align-items: center; }
.progress-text { color: #888; font-size: 0.9rem; margin: 8px 0; }
.progress-bar {
  height: 8px; background: #e0e0e0; border-radius: 4px; overflow: hidden;
}
.progress-fill {
  height: 100%; background: #4CAF50; transition: width 0.3s;
}
.loading, .error { text-align: center; padding: 40px; color: #888; }
.error { color: #f44336; }
</style>
