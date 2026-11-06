<!--
  AgentSessionDetail.vue - 学习伙伴单次会话详情(家长中心)
  按时间显示完整文字对话(孩子 / 豆豆 / 豆豆安全回复三种样式)和答题记录(题目、孩子的选择、对错)。
-->
<script setup>
import AppButton from '../AppButton.vue'
import { SOURCE_LABEL, formatTime } from '../../composables/useAgentRecords'

defineProps({ detail: { type: Object, required: true }, deleting: { type: Boolean, default: false } })
const emit = defineEmits(['back', 'delete'])
const RESULT = { right: '答对', wrong: '答错', none: '未作答' }
</script>

<template>
  <section class="detail" aria-label="会话详情">
    <header class="head">
      <div>
        <h2>{{ detail.session.childName }} · {{ detail.session.topic || '自由聊天' }}</h2>
        <p class="meta">
          {{ SOURCE_LABEL[detail.session.sourcePage] || '自由提问' }} · {{ formatTime(detail.session.createdAt) }}
          · {{ detail.session.turns ?? detail.bubbles.filter((b) => b.kind === 'child').length }} 回合
        </p>
        <p v-if="detail.session.flagged" class="flag">建议和孩子聊聊：{{ detail.session.flagLabel }}</p>
      </div>
      <div class="actions">
        <AppButton variant="ghost" @click="emit('back')">返回列表</AppButton>
        <AppButton variant="warning" :disabled="deleting" @click="emit('delete', detail.session)">删除这次记录</AppButton>
      </div>
    </header>

    <h3>对话</h3>
    <p v-if="!detail.bubbles.length" class="empty">这次会话没有文字记录。</p>
    <ol class="chat">
      <li v-for="(b, i) in detail.bubbles" :key="i" :class="['msg', b.kind]">
        <span class="who">{{ b.who }}</span><time>{{ b.time }}</time>
        <strong v-if="b.title" class="title">【{{ b.title }}】</strong>
        <p>{{ b.body }}</p>
      </li>
    </ol>

    <h3>答题记录</h3>
    <p v-if="!detail.answers.length" class="empty">这次会话没有答题。</p>
    <table v-else class="answers">
      <thead><tr><th>题目</th><th>选项</th><th>孩子的选择</th><th>结果</th><th>时间</th></tr></thead>
      <tbody>
        <tr v-for="(a, i) in detail.answers" :key="i">
          <td>{{ a.question }}</td>
          <td>{{ a.options.join(' / ') }}</td>
          <td>{{ a.chosen }}</td>
          <td><span :class="['result', a.result]">{{ RESULT[a.result] }}</span></td>
          <td>{{ a.time }}</td>
        </tr>
      </tbody>
    </table>
  </section>
</template>

<style scoped>
.detail { display: grid; gap: var(--space-3); padding: var(--space-4); background: var(--bg-card); border-radius: var(--radius-md); }
.head { display: flex; flex-wrap: wrap; justify-content: space-between; gap: var(--space-3); }
.head h2 { font-size: var(--text-base); }
.meta, .empty, time { color: var(--text-secondary); font-size: var(--text-sm); }
.flag { margin-top: var(--space-1); padding: var(--space-1) var(--space-3); color: var(--color-orange-hover); background: var(--color-primary-bg); border-radius: var(--radius-pill); font-size: var(--text-sm); display: inline-block; }
.actions { display: flex; flex-wrap: wrap; gap: var(--space-2); align-items: flex-start; }
h3 { font-size: var(--text-sm); color: var(--text-secondary); }
.chat { display: grid; gap: var(--space-2); list-style: none; padding: 0; }
.msg { display: grid; gap: var(--space-1); max-width: 85%; padding: var(--space-2) var(--space-3); border-radius: var(--radius-sm); border: 1px solid var(--border-light); }
.msg .who { font-weight: var(--font-bold); font-size: var(--text-sm); }
.msg time { font-size: var(--text-xs); }
.msg p { white-space: pre-wrap; }
.msg.child { justify-self: end; background: var(--color-primary-bg); }
.msg.agent { justify-self: start; background: var(--bg-muted); }
.msg.safe { justify-self: start; background: var(--color-warning-bg); border-style: dashed; }
.answers { width: 100%; border-collapse: collapse; font-size: var(--text-sm); }
.answers th, .answers td { padding: var(--space-2); border-bottom: 1px solid var(--border-muted); text-align: left; vertical-align: top; }
.result.right { color: var(--color-success-hover); }
.result.wrong { color: var(--color-warning); }
.result.none { color: var(--text-tertiary); }
@media (max-width: 640px) { .answers th:nth-child(2), .answers td:nth-child(2) { display: none; } }
</style>
