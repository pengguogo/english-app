<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import BackBar from '../components/BackBar.vue'
import AppButton from '../components/AppButton.vue'
import ParentPasswordForm from '../components/parent/ParentPasswordForm.vue'
import WeeklyReport from '../components/parent/WeeklyReport.vue'
import { useParentCenter } from '../composables/useParentCenter'
const router = useRouter()
const accessKey = ref('')
const { unlocked, loading, saving, error, notice, report, configured, load, unlock, savePassword } = useParentCenter()
onMounted(() => { if (sessionStorage.getItem('childGrowthKey')) load() })
</script>

<template>
  <main class="parent-center">
    <BackBar title="家长中心" @back="router.push('/')" />
    <p v-if="error" role="alert">{{ error }}</p>
    <p v-if="notice" role="status">{{ notice }}</p>
    <form v-if="!unlocked" class="access-form" @submit.prevent="unlock(accessKey)">
      <h2>验证家长身份</h2>
      <label for="parent-access">成长档案访问口令</label>
      <input id="parent-access" v-model="accessKey" type="password" autocomplete="off" required />
      <p>使用与成长档案相同的访问口令，查看周报并设置游戏密码。</p>
      <AppButton :disabled="loading || !accessKey" @click="unlock(accessKey)">{{ loading ? '验证中...' : '进入家长中心' }}</AppButton>
    </form>
    <template v-else>
      <!-- 原首页的家长类入口统一收在这里 -->
      <nav class="parent-links" aria-label="家长工具">
        <AppButton variant="ghost" @click="router.push('/child-profile')">记录身高体重</AppButton>
        <AppButton variant="ghost" @click="router.push('/wrong-answers')">错题集</AppButton>
        <AppButton variant="ghost" @click="router.push('/learned')">学习记录</AppButton>
      </nav>
      <AppButton variant="ghost" :disabled="loading" @click="load">刷新周报</AppButton>
      <WeeklyReport v-if="report" :report="report" />
      <ParentPasswordForm :configured="configured" :saving="saving" :save="savePassword" />
    </template>
  </main>
</template>

<style scoped>
.parent-center { max-width: var(--content-max); margin: auto; padding: var(--space-4); display: grid; gap: var(--space-4); }
.access-form { display: grid; gap: var(--space-3); padding: var(--space-5); background: var(--bg-card); border-radius: var(--radius-lg); }
.access-form input { min-height: var(--touch-target); padding: var(--space-3); color: var(--text-primary); background: var(--bg-card); border: 1px solid var(--border-light); border-radius: var(--radius-sm); }
.parent-links { display: flex; flex-wrap: wrap; gap: var(--space-3); }
</style>
