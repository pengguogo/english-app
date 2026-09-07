<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import BackBar from '../components/BackBar.vue'
import AppButton from '../components/AppButton.vue'
import MimiMascot from '../components/MimiMascot.vue'
import PicturebookActivity from '../components/picturebooks/PicturebookActivity.vue'
import TrainTrack from '../components/picturebooks/TrainTrack.vue'
import { usePicturebookJourney } from '../composables/usePicturebookJourney'
const route = useRoute()
const router = useRouter()
const interactive = ref(true)
const calm = ref(false)
const { lessons, completedIds, active, index, lesson, item, loading, error, saving, saveError, bookmarkError,
  arrival, allComplete, load, start, resume, move, answer, finish, timetable } = usePicturebookJourney(route)
function next() { index.value < lesson.value.content.items.length - 1 ? move(1) : finish() }
</script>

<template>
  <main class="book-journey">
    <BackBar :title="active < 0 ? '绘本出发站' : '绘本小火车'" @back="active < 0 ? router.push('/picturebooks') : timetable()" />
    <p v-if="loading" role="status">车票准备中…</p>
    <section v-else-if="error" role="alert"><p>{{ error }}</p><AppButton @click="load">重新加载</AppButton></section>
    <template v-else>
      <header class="journey-heading"><span class="eyebrow">THE STREAM JOURNEY · 本周线下课</span><h1>{{ lessons[0]?.content.bookTitle }}</h1></header>
      <section v-if="active < 0" class="departure">
        <img class="hero-train" :src="'/images/picturebooks/train-station.jpg'" alt="你的绘本列车已停靠车站" />
        <div class="departure-copy"><h2>{{ allComplete ? '整本车票已集齐！再开一趟吧。' : '你好，小司机。列车等你发车！' }}</h2>
          <p>检票 → 听故事 → 回答问题 → 当小站长。完成每一站，就在车票上盖一个章。</p>
          <AppButton variant="success" @click="resume">检票 / 继续旅程 →</AppButton>
        </div>
        <div class="station-list"><AppButton v-for="(entry, position) in lessons" :key="entry.id" class="station" variant="ghost" @click="start(position)">
          <span class="station-number">{{ completedIds.includes(entry.id) ? '✓' : `0${position + 1}` }}</span><span>{{ entry.name }}<small>{{ completedIds.includes(entry.id) ? '车票已盖章 · 可以重玩' : '可以直接出发' }}</small></span>
        </AppButton></div>
      </section>
      <section v-else-if="arrival" class="arrival">
        <MimiMascot variant="celebrate" size="md" />
        <p class="eyebrow">ARRIVAL TICKET</p><h2>{{ allComplete ? '溪边探险，全线到站！' : '叮咚！这一站完成啦！' }}</h2>
        <div class="souvenir"><img :src="'/images/picturebooks/train-station.jpg'" alt="溪边探险纪念车票" /><div><strong>绘本小火车 · 溪边探险</strong><p>{{ completedIds.length }} / {{ lessons.length }} 枚到站印章</p><span v-for="(entry, position) in lessons" :key="entry.id" class="stamp">{{ completedIds.includes(entry.id) ? '✓' : '○' }} {{ position + 1 }} 站</span></div></div>
        <p>会听、会观察，还愿意开口。谢谢小司机的努力！</p>
        <div class="arrival-actions"><AppButton v-if="active + 1 < lessons.length" variant="success" @click="start(active + 1)">下一大站 →</AppButton><AppButton variant="ghost" @click="timetable">返回路线 / 再开一趟</AppButton></div>
      </section>
      <template v-else-if="lesson && item">
        <div class="journey-toolbar"><strong>{{ lesson.name }}</strong><div><label><input v-model="interactive" type="checkbox" /> 故事途中提问</label><label><input v-model="calm" type="checkbox" /> 关闭火车动效</label></div></div>
        <TrainTrack :step="index" :total="lesson.content.items.length" :calm="calm" />
        <PicturebookActivity :key="`${lesson.id}:${index}`" :item="item" :quiz="lesson.type === 'QUIZ'" :interactive="interactive" :first="index === 0" :last="index === lesson.content.items.length - 1" :saving="saving" @next="next" @prev="move(-1)" @answer="answer" />
        <div v-if="saveError" class="save-error" role="alert"><p>{{ saveError }}</p><AppButton :disabled="saving" @click="finish">重试保存</AppButton></div>
      </template>
      <p v-if="bookmarkError" role="status">{{ bookmarkError }}</p>
    </template>
  </main>
</template>

<style scoped>
.book-journey { max-width: 1060px; margin: auto; padding: var(--space-4); color: var(--text-primary); }
.journey-heading { margin-bottom: var(--space-5); }.eyebrow { color: var(--text-secondary); font-size: var(--text-xs); letter-spacing: .1em; }h1 { font-size: var(--text-lg); margin-top: var(--space-2); } h2 { font-size: var(--text-lg); line-height: 1.5; }
.departure { background: var(--bg-card); border: 1px solid var(--border-light); border-radius: var(--radius-lg); overflow: hidden; }
.hero-train { width: 100%; max-height: 340px; object-fit: cover; object-position: center 65%; display: block; }
.departure-copy { padding: var(--space-6); }.departure-copy p { margin: var(--space-3) 0 var(--space-5); color: var(--text-secondary); line-height: 1.7; }
.station-list { display: grid; grid-template-columns: repeat(3, minmax(0,1fr)); padding: 0 var(--space-6) var(--space-6); gap: var(--space-3); }
.station-list .station { justify-content: start; white-space: normal; border: 1px solid var(--border-light); border-radius: var(--radius-md); text-align: left; padding: var(--space-4); }
.station-number { font-size: var(--text-lg); color: var(--subject-math); }.station small { display: block; color: var(--text-secondary); font-size: var(--text-xs); margin-top: var(--space-2); }
.journey-toolbar { display: flex; flex-wrap: wrap; justify-content: space-between; gap: var(--space-3); font-size: var(--text-sm); }.journey-toolbar > div { display: flex; gap: var(--space-3); }.journey-toolbar label { display: flex; gap: var(--space-1); align-items: center; min-height: var(--touch-target); color: var(--text-secondary); }
.arrival { text-align: center; padding: var(--space-6); border: 2px dashed var(--color-warning); border-radius: var(--radius-lg); background: var(--gradient-warm); }.arrival > p { margin: var(--space-4) 0; }
.souvenir { display: flex; max-width: 640px; margin: var(--space-5) auto; text-align: left; border-radius: var(--radius-md); overflow: hidden; background: var(--bg-card); box-shadow: var(--shadow-card); }.souvenir img { width: 40%; object-fit: cover; }.souvenir > div { padding: var(--space-5); }.souvenir p { margin: var(--space-3) 0; }.stamp { display: inline-block; border: 1px dashed var(--subject-math); padding: var(--space-2); margin: var(--space-1); border-radius: var(--radius-sm); color: var(--text-primary); }
.arrival-actions { display: flex; flex-wrap: wrap; justify-content: center; gap: var(--space-3); }.save-error { margin: var(--space-5) 0; color: var(--text-primary); }
@media(max-width: 650px) { .station-list { grid-template-columns: 1fr; }.hero-train { height: 220px; }.departure-copy { padding: var(--space-4); }.souvenir { flex-direction: column; }.souvenir img { width: 100%; max-height: 170px; }.arrival { padding: var(--space-4); } }
</style>
