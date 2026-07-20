<!-- HomeView.vue - 首页:展示学习主题列表 -->
<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getThemes } from '../api/theme'

const router = useRouter()
const themes = ref([])
const isLoading = ref(true)
const errorMsg = ref('')

onMounted(async () => {
  try {
    themes.value = await getThemes()
  } catch (e) {
    errorMsg.value = '加载失败,请刷新重试'
    console.error('加载主题失败:', e)
  } finally {
    isLoading.value = false
  }
})
</script>

<template>
  <div class="home">
    <header class="header">
      <div class="avatar">👧</div>
      <div class="user-info">
        <h2>小朋友</h2>
        <p>欢迎来学英语!</p>
      </div>
    </header>

    <h2 class="section-title">选择主题</h2>
    <div v-if="isLoading" class="loading">加载中...</div>
    <div v-else-if="errorMsg" class="error">{{ errorMsg }}</div>
    <div v-else class="theme-grid">
      <div
        v-for="theme in themes"
        :key="theme.id"
        class="theme-card"
        :class="{ locked: theme.isLocked }"
        @click="!theme.isLocked && router.push(`/theme/${theme.id}`)"
      >
        <div class="theme-icon">
          {{ theme.id === 1 ? '🍎' : '🚗' }}
        </div>
        <h3>{{ theme.name }}</h3>
        <p v-if="theme.isLocked">🔒 待解锁</p>
        <p v-else>点击进入 →</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border-radius: 16px;
  margin-bottom: 24px;
}
.avatar { font-size: 3rem; }
.theme-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}
.theme-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  text-align: center;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}
.theme-card:hover { transform: translateY(-4px); box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
.theme-card.locked { opacity: 0.6; cursor: not-allowed; }
.theme-icon { font-size: 3rem; margin-bottom: 8px; }
.section-title { margin-bottom: 12px; color: #555; }
.loading, .error { text-align: center; padding: 40px; color: #888; }
.error { color: #f44336; }
</style>
