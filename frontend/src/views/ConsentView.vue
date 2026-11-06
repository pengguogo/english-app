<!--
  ConsentView.vue - 监护人同意说明页
  用途: 家长阅读并确认儿童信息处理说明,保存同意版本与时间。
        同意只控制学习伙伴(Agent),不影响学课;入口在家长中心。
  依据: docs/specs/learning-agent-design-review.html 第 2、6 节。
  版本号取自 /auth/me 的 consentVersion。
-->
<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import BackBar from '../components/BackBar.vue'
import AppButton from '../components/AppButton.vue'
import { submitConsent } from '../api/auth'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()
const agreed = ref(false)
const submitting = ref(false)
const error = ref('')

async function confirm() {
  if (!agreed.value || submitting.value) return
  submitting.value = true
  error.value = ''
  try {
    await submitConsent({ version: auth.me?.consentVersion })
    await auth.refresh()
    router.replace('/parent-center')
  } catch (e) {
    error.value = e.response?.data?.message || e.message || '提交失败，请重试'
  } finally {
    submitting.value = false
  }
}

function back() {
  router.push('/parent-center')
}
</script>

<template>
  <main class="consent-page">
    <BackBar title="学习伙伴说明与同意" @back="back" />
    <article class="consent-card">
      <h2>启用学习伙伴前请家长阅读</h2>
      <p>本应用面向 4–6 岁儿童，由监护人注册和管理。根据《儿童个人信息网络保护规定》，学习伙伴（语音对话）上线前需要取得您的同意。不同意不影响孩子学课。</p>
      <h3>我们记录什么</h3>
      <ul>
        <li>孩子档案：昵称，以及您选择填写的出生日期、性别、身高体重和成长照片。</li>
        <li>学习记录：课程进度、星星、错题和学习时长。</li>
        <li>家长账号：邮箱和加密保存的密码。</li>
      </ul>
      <h3>语音如何处理</h3>
      <p>使用跟读、朗读功能时，音频会临时发送到语音服务商（百度智能云）完成识别或合成。我们不保存原始录音，但音频会离开您的设备，服务商的留存规则以其隐私政策为准。</p>
      <h3>保留期限与删除</h3>
      <ul>
        <li>学习记录在账号存续期间保留；您可以在家长中心删除记录。</li>
        <li>数据库每天备份、备份保留 7 天，因此已删除的数据最迟 7 天后从备份中清除。</li>
        <li>家长区域（查看、删除、设置）需要再次输入家长密码。</li>
      </ul>
      <h3>数据隔离</h3>
      <p>每个家庭的数据只有本家庭能看到；同一家庭的孩子之间学习记录相互独立。</p>
    </article>

    <p v-if="auth.consented" class="consent-done" role="status">您已同意当前版本（{{ auth.me?.consentVersion }}）的说明。</p>
    <template v-else>
    <label class="consent-check">
      <input v-model="agreed" type="checkbox" />
      <span>我是孩子的监护人，已阅读并同意以上说明。</span>
    </label>
    <p v-if="error" class="consent-error" role="alert">{{ error }}</p>
    <AppButton block size="lg" :disabled="!agreed || submitting" @click="confirm">{{ submitting ? '提交中...' : '同意' }}</AppButton>
    </template>
    <AppButton block variant="ghost" @click="back">返回家长中心</AppButton>
  </main>
</template>

<style scoped>
.consent-page { max-width: 640px; margin: 0 auto; padding: var(--space-4); display: grid; gap: var(--space-4); }
.consent-card { padding: var(--space-5); background: var(--bg-card); border-radius: var(--radius-lg); box-shadow: var(--shadow-card); color: var(--text-primary); line-height: 1.7; }
.consent-card h2 { font-size: var(--text-xl); margin-bottom: var(--space-3); }
.consent-card h3 { font-size: var(--text-lg); margin: var(--space-4) 0 var(--space-2); }
.consent-card ul { padding-left: var(--space-5); display: grid; gap: var(--space-1); }
.consent-check { display: flex; gap: var(--space-3); align-items: flex-start; color: var(--text-primary); font-weight: var(--font-medium); }
.consent-check input { width: 22px; height: 22px; flex-shrink: 0; }
.consent-error { color: var(--color-warning); }
.consent-done { color: var(--color-success); font-weight: var(--font-medium); }
</style>
