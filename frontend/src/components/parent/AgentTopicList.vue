<!--
  AgentTopicList.vue - 孩子各主题的学习情况(家长中心「主题」标签页)
  状态只描述孩子做过什么:接触过 / 回答过 / 建议再练习,不判断“掌握”。
  每项可展开看依据(来自哪几次会话、答对和答错次数);有复习建议时显示「去复习」,route 由服务端给。
-->
<script setup>
import { ref } from 'vue'
import AppButton from '../AppButton.vue'
import { STATUS_LABEL, formatTime } from '../../composables/useAgentRecords'

defineProps({ groups: { type: Array, required: true } })
const emit = defineEmits(['open-session', 'review'])
const expanded = ref(new Set())
const keyOf = (t) => `${t.childId}-${t.themeId}`
function toggle(t) {
  const next = new Set(expanded.value)
  next.has(keyOf(t)) ? next.delete(keyOf(t)) : next.add(keyOf(t))
  expanded.value = next
}
</script>

<template>
  <section class="topics" aria-label="主题学习情况">
    <p class="note">状态规则：有过讲解为“接触过”；最近 3 次答题全对为“回答过”；最近 3 次里答错 2 次及以上为“建议再练习”。这些只反映和豆豆聊天时的表现。</p>
    <p v-if="!groups.length" class="empty">还没有主题记录。</p>
    <div v-for="g in groups" :key="g.childId" class="child">
      <h3>{{ g.childName }}</h3>
      <table>
        <thead><tr><th>主题</th><th>状态</th><th>答对 / 答错</th><th>最近</th><th></th></tr></thead>
        <tbody>
          <template v-for="t in g.topics" :key="keyOf(t)">
            <tr>
              <td>{{ t.themeName }}</td>
              <td><span :class="['status', t.status]">{{ STATUS_LABEL[t.status] || t.status }}</span></td>
              <td>{{ t.correct ?? 0 }} / {{ t.wrong ?? 0 }}</td>
              <td>{{ formatTime(t.lastAt) }}</td>
              <td class="ops">
                <AppButton variant="ghost" :aria-expanded="expanded.has(keyOf(t))" @click="toggle(t)">
                  {{ expanded.has(keyOf(t)) ? '收起' : '依据' }}
                </AppButton>
                <AppButton v-if="t.suggestion?.route" variant="success" @click="emit('review', t.suggestion.route)">去复习</AppButton>
              </td>
            </tr>
            <tr v-if="expanded.has(keyOf(t))" class="basis">
              <td colspan="5">
                <p v-if="t.suggestion?.basis">复习建议：{{ t.suggestion.basis }}</p>
                <p>答对 {{ t.correct ?? 0 }} 次，答错 {{ t.wrong ?? 0 }} 次。</p>
                <p v-if="t.sessions?.length">
                  来自 {{ t.sessions.length }} 次会话（最近 5 次）：
                  <button v-for="(id, i) in t.sessions" :key="id" type="button" class="link" @click="emit('open-session', id)">
                    第 {{ i + 1 }} 次
                  </button>
                </p>
                <p v-else>依据会话已删除或已过期。</p>
              </td>
            </tr>
          </template>
        </tbody>
      </table>
    </div>
  </section>
</template>

<style scoped>
.topics { display: grid; gap: var(--space-4); }
.note, .empty { color: var(--text-secondary); font-size: var(--text-sm); }
.child { padding: var(--space-4); background: var(--bg-card); border-radius: var(--radius-md); }
.child h3 { font-size: var(--text-base); margin-bottom: var(--space-2); }
table { width: 100%; border-collapse: collapse; font-size: var(--text-sm); }
th, td { padding: var(--space-2); border-bottom: 1px solid var(--border-muted); text-align: left; vertical-align: middle; }
.ops { display: flex; flex-wrap: wrap; gap: var(--space-2); justify-content: flex-end; }
.status { padding: 2px var(--space-2); border-radius: var(--radius-pill); background: var(--bg-muted); white-space: nowrap; }
.status.ANSWERED { background: var(--color-success-bg); }
.status.PRACTICE { background: var(--color-primary-bg); color: var(--color-primary-hover); }
.basis td { background: var(--bg-muted); display: table-cell; }
.basis p + p { margin-top: var(--space-1); }
.link { margin-left: var(--space-2); color: var(--color-primary); background: none; border: 0; text-decoration: underline; cursor: pointer; font: inherit; }
</style>
