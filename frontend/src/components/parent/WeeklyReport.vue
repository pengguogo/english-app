<script setup>
const props = defineProps({ report: { type: Object, required: true } })
const duration = seconds => `${Math.floor(seconds / 60)}分${seconds % 60}秒`
const rate = count => props.report.attempts ? `${Math.round(count / props.report.attempts * 100)}%` : '暂无样本'
</script>

<template>
  <section class="weekly-report" aria-label="最近七天学习周报">
    <h2>最近七天学习周报</h2>
    <p>{{ report.from }} 至 {{ report.to }}</p>
    <dl>
      <div><dt>实际学习时长</dt><dd>{{ duration(report.totalSeconds) }}</dd></div>
      <div><dt>学习天数</dt><dd>{{ report.activeDays }}天</dd></div>
      <div><dt>复习通过课次</dt><dd>{{ report.passedLessons }}次</dd></div>
      <div><dt>首次答对率</dt><dd>{{ rate(report.firstCorrect) }}</dd></div>
      <div><dt>辅助答对率</dt><dd>{{ rate(report.assistedCorrect) }}</dd></div>
    </dl>
    <p>答题统计包含 {{ report.attempts }} 次选择题/计算题练习。辅助答对表示首次未答对、提示后答对。</p>
    <p>仅统计功能上线后采集的时长和答题样本；游戏密码解锁不增加学习时长。页面隐藏或60秒无操作时暂停计时。</p>
    <div class="table-wrap">
      <table><caption>每日学习记录</caption><thead><tr><th scope="col">日期</th><th scope="col">学习时长</th><th scope="col">复习通过</th></tr></thead>
        <tbody><tr v-for="day in report.days" :key="day.date"><th scope="row">{{ day.date }}</th><td>{{ duration(day.seconds) }}</td><td>{{ day.passedLessons }}次</td></tr></tbody>
      </table>
    </div>
  </section>
</template>

<style scoped>
.weekly-report { display: grid; gap: var(--space-4); padding: var(--space-5); background: var(--bg-card); border-radius: var(--radius-lg); }
dl { display: grid; grid-template-columns: repeat(auto-fit, minmax(130px, 1fr)); gap: var(--space-3); }
dl div { padding: var(--space-3); border-radius: var(--radius-md); background: var(--bg-muted); }
dt, p { color: var(--text-secondary); }
dd { margin: var(--space-2) 0 0; font-size: var(--text-lg); color: var(--text-primary); }
.table-wrap { overflow-x: auto; } table { width: 100%; border-collapse: collapse; } th, td { padding: var(--space-3); border-bottom: 1px solid var(--border-light); text-align: left; }
</style>
