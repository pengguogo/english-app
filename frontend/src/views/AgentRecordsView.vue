<!--
  AgentRecordsView.vue - 家长中心「学习伙伴记录」
  「对话记录」:按孩子 / 只看提醒筛选,时间倒序,加载更多;点开看详情,删除需二次确认。
  「主题」:各孩子主题的学习情况、依据和复习建议。
  需家长二次验证:403/4031 时 http 拦截器弹 ParentVerifyModal,家长取消则显示「验证并进入」。
-->
<script setup>
import { onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import BackBar from '../components/BackBar.vue'
import AppButton from '../components/AppButton.vue'
import AppModal from '../components/AppModal.vue'
import AgentSessionDetail from '../components/parent/AgentSessionDetail.vue'
import AgentTopicList from '../components/parent/AgentTopicList.vue'
import { SOURCE_LABEL, formatTime, useAgentRecords } from '../composables/useAgentRecords'

const router = useRouter()
const r = useAgentRecords()
const { locked, error, children, filter, sessions, hasMore, loading, detail, detailLoading, deleting, topicGroups, topicsLoading } = r
const tab = ref('sessions')
const confirming = ref(null)

function reload() {
  if (tab.value === 'topics') return r.loadTopics()
  return r.loadSessions()
}

onMounted(async () => {
  await r.loadChildren()
  await r.loadSessions()
})

watch(() => [filter.value.childId, filter.value.flaggedOnly], () => {
  r.closeDetail()
  reload()
})

function switchTab(next) {
  tab.value = next
  r.closeDetail()
  if (next === 'topics') r.loadTopics()
}

function openFromTopic(id) {
  tab.value = 'sessions'
  r.openDetail(id)
}

async function confirmDelete() {
  const target = confirming.value
  if (!target) return
  if (await r.removeSession(target.id)) confirming.value = null
}
</script>

<template>
  <main class="agent-records">
    <BackBar title="学习伙伴记录" @back="router.push('/parent-center')" />
    <p class="retention">只保存文字对话和答题结果，不保存录音。记录保存 180 天，到期自动删除；你也可以随时手动删除。</p>
    <p v-if="error" role="alert" class="error">{{ error }}</p>

    <section v-if="locked" class="access-form">
      <h2>验证家长身份</h2>
      <p>查看学习伙伴记录需要再次输入家长密码，10 分钟内有效。</p>
      <AppButton @click="reload">验证并进入</AppButton>
    </section>

    <template v-else>
      <nav class="tabs" role="tablist" aria-label="记录分类">
        <AppButton role="tab" :aria-selected="tab === 'sessions'" :variant="tab === 'sessions' ? 'primary' : 'ghost'" @click="switchTab('sessions')">对话记录</AppButton>
        <AppButton role="tab" :aria-selected="tab === 'topics'" :variant="tab === 'topics' ? 'primary' : 'ghost'" @click="switchTab('topics')">主题</AppButton>
      </nav>

      <form class="filters" @submit.prevent>
        <label>孩子
          <select v-model="filter.childId">
            <option value="">全部孩子</option>
            <option v-for="c in children" :key="c.id" :value="c.id">{{ c.nickname }}</option>
          </select>
        </label>
        <label v-if="tab === 'sessions'" class="check"><input v-model="filter.flaggedOnly" type="checkbox" /> 只看提醒</label>
      </form>

      <template v-if="tab === 'sessions'">
        <p v-if="detailLoading" class="muted">加载详情...</p>
        <AgentSessionDetail v-else-if="detail" :detail="detail" :deleting="deleting" @back="r.closeDetail()" @delete="confirming = $event" />
        <section v-else class="list" aria-label="会话列表">
          <p v-if="!loading && !sessions.length" class="muted">{{ filter.flaggedOnly ? '没有需要留意的会话。' : '还没有学习伙伴记录。' }}</p>
          <table v-if="sessions.length">
            <thead><tr><th>孩子</th><th>主题</th><th>来源</th><th>时间</th><th>回合</th><th></th></tr></thead>
            <tbody>
              <tr v-for="s in sessions" :key="s.id" :class="{ flagged: s.flagged }">
                <td>{{ s.childName }}</td>
                <td>
                  {{ s.topic || '自由聊天' }}
                  <span v-if="s.flagged" class="flag-tag" :title="s.flagLabel">建议和孩子聊聊<template v-if="s.flagLabel">：{{ s.flagLabel }}</template></span>
                </td>
                <td>{{ SOURCE_LABEL[s.sourcePage] || '自由提问' }}</td>
                <td>{{ formatTime(s.updatedAt || s.createdAt) }}</td>
                <td>{{ s.turns }}</td>
                <td class="ops">
                  <AppButton variant="ghost" @click="r.openDetail(s.id)">查看</AppButton>
                  <AppButton variant="ghost" :disabled="deleting" @click="confirming = s">删除</AppButton>
                </td>
              </tr>
            </tbody>
          </table>
          <AppButton v-if="hasMore" variant="ghost" :disabled="loading" @click="r.loadMore()">{{ loading ? '加载中...' : '加载更多' }}</AppButton>
          <p v-else-if="loading" class="muted">加载中...</p>
        </section>
      </template>

      <template v-else>
        <p v-if="topicsLoading" class="muted">加载中...</p>
        <AgentTopicList v-else :groups="topicGroups" @open-session="openFromTopic" @review="router.push($event)" />
      </template>
    </template>

    <AppModal :open="Boolean(confirming)" title="删除这次记录？" @close="confirming = null">
      <div class="confirm">
        <p v-if="confirming">将删除 {{ confirming.childName }} 在 {{ formatTime(confirming.createdAt) }} 的对话和答题记录，删除后无法恢复，相关的复习建议会重新计算。</p>
        <div class="confirm-actions">
          <AppButton variant="ghost" @click="confirming = null">取消</AppButton>
          <AppButton variant="warning" :disabled="deleting" @click="confirmDelete">{{ deleting ? '删除中...' : '确认删除' }}</AppButton>
        </div>
      </div>
    </AppModal>
  </main>
</template>

<style scoped>
.agent-records { max-width: var(--content-max); margin: auto; padding: var(--space-4); display: grid; gap: var(--space-4); }
.retention, .muted { color: var(--text-secondary); font-size: var(--text-sm); }
.error { color: var(--color-warning); }
.access-form { display: grid; gap: var(--space-3); padding: var(--space-5); background: var(--bg-card); border-radius: var(--radius-lg); }
.tabs, .filters { display: flex; flex-wrap: wrap; gap: var(--space-3); align-items: center; }
.filters label { display: flex; gap: var(--space-2); align-items: center; font-size: var(--text-sm); }
.filters select { min-height: var(--touch-target); padding: 0 var(--space-3); color: var(--text-primary); background: var(--bg-card); border: 1px solid var(--border-light); border-radius: var(--radius-sm); font: inherit; }
.list { display: grid; gap: var(--space-3); padding: var(--space-4); background: var(--bg-card); border-radius: var(--radius-md); overflow-x: auto; }
table { width: 100%; border-collapse: collapse; font-size: var(--text-sm); }
th, td { padding: var(--space-2); border-bottom: 1px solid var(--border-muted); text-align: left; vertical-align: middle; }
tr.flagged td:first-child { box-shadow: inset 3px 0 0 var(--color-orange); }
.flag-tag { display: inline-block; margin-left: var(--space-2); padding: 2px var(--space-2); color: var(--color-orange-hover); background: var(--color-primary-bg); border-radius: var(--radius-pill); font-size: var(--text-xs); }
.ops { display: flex; gap: var(--space-2); justify-content: flex-end; }
.confirm { display: grid; gap: var(--space-4); }
.confirm-actions { display: flex; gap: var(--space-3); justify-content: flex-end; }
@media (max-width: 640px) { th:nth-child(3), td:nth-child(3), th:nth-child(5), td:nth-child(5) { display: none; } }
</style>
