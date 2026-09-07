<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getThemes } from '../api/theme'
import { getUnitsByTheme } from '../api/unit'
import BackBar from '../components/BackBar.vue'
import AppButton from '../components/AppButton.vue'
const router = useRouter()
const books = ref([])
const loading = ref(true)
const error = ref('')
async function load() {
  loading.value = true
  error.value = ''
  try {
    const theme = (await getThemes()).find((item) => item.name === '绘本小火车')
    if (!theme) throw new Error('绘本课程尚未安装')
    books.value = await getUnitsByTheme(theme.id)
  } catch { error.value = '绘本列车还没准备好，请稍后重试。' }
  finally { loading.value = false }
}
onMounted(load)
</script>

<template>
  <main class="book-library">
    <BackBar title="绘本小火车" @back="router.push('/')" />
    <header class="library-hero">
      <div><p class="eyebrow">PICTUREBOOK EXPRESS</p><h1>小司机，今天去哪一站？</h1><p>一本绘本，一段旅程。把会说的英语装进小火车。</p></div>
      <img :src="'/images/picturebooks/train-station.jpg'" alt="绘本列车在溪边车站等你" />
    </header>
    <p v-if="loading" role="status">正在检修绘本列车…</p>
    <div v-else-if="error" role="alert"><p>{{ error }}</p><AppButton @click="load">重新加载</AppButton></div>
    <section v-else class="books">
      <article v-for="book in books" :key="book.id" class="book-cover">
        <img :src="'/images/picturebooks/stream-swing/page-01.jpg'" alt="孩子们和 Floppy 来到小溪边" />
        <div><span class="eyebrow">{{ book.sortOrder === 1 ? '本周线下课' : '绘本旅程' }} · 3 站旅程</span>
          <h2>{{ book.name }}</h2><p>故事站 → 问答站 → 小司机站</p>
          <p>{{ book.completedLessons }} / {{ book.totalLessons }} 站已到达 · 每次约 5–8 分钟</p>
          <AppButton variant="success" @click="router.push(`/picturebooks/${book.id}`)">{{ book.completedLessons === book.totalLessons ? '再开一趟' : '检票 / 继续旅程' }} →</AppButton>
        </div>
      </article>
      <p class="library-note">车票靠参与收集。答错可以再试，下一站随时出发。</p>
    </section>
  </main>
</template>

<style scoped>
.book-library { max-width: var(--content-max); margin: auto; padding: var(--space-4); color: var(--text-primary); }
.library-hero { display: grid; grid-template-columns: 1fr 1fr; align-items: center; background: var(--gradient-warm); border-radius: var(--radius-lg); overflow: hidden; margin-bottom: var(--space-8); }
.library-hero > div { padding: var(--space-6); }
.library-hero img { width: 100%; height: 280px; object-fit: cover; }
h1 { font-size: var(--text-xl); margin: var(--space-3) 0; } h2 { margin: var(--space-3) 0; }
p { line-height: 1.7; color: var(--text-secondary); }
.eyebrow { font-size: var(--text-xs); letter-spacing: .1em; color: var(--text-secondary); }
.book-cover { display: flex; align-items: center; gap: var(--space-8); padding: var(--space-6); border: 2px solid var(--border-light); border-radius: var(--radius-lg); background: var(--bg-card); box-shadow: var(--shadow-card); }
.book-cover > img { width: 200px; border-radius: var(--radius-md); }
.book-cover .app-btn { margin-top: var(--space-4); }
.library-note { text-align: center; margin: var(--space-6) 0; }
@media(max-width: 600px) { .library-hero { grid-template-columns: 1fr; } .library-hero img { height: 180px; } h1 { font-size: var(--text-lg); } .book-cover { flex-direction: column; gap: var(--space-3); align-items: stretch; } .book-cover > img { width: 150px; align-self: center; } }
</style>
