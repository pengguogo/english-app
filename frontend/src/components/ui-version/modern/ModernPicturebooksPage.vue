<!--
  ModernPicturebooksPage.vue - 新版绘本页
  用途: 将绘本入口改造成站点旅程卡，突出第一站并保留真实阅读路由。
  作者: TRAE Agent
  创建日期: 2026-09-29
-->
<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getThemes } from '../../../api/theme'
import { getUnitsByTheme } from '../../../api/unit'
import { getLessonsByUnit } from '../../../api/lesson'
import BackBar from '../../BackBar.vue'
import AppButton from '../../AppButton.vue'
import { picturebookCover } from '../../../utils/picturebookCover'

const router = useRouter()
const books = ref([])
const loading = ref(true)
const error = ref('')

const firstStation = computed(() => books.value[0] || null)
const followingStations = computed(() => books.value.slice(1))

onMounted(load)

async function load() {
  loading.value = true
  error.value = ''

  try {
    const theme = (await getThemes()).find((item) => item.name === '绘本小火车')
    if (!theme) {
      throw new Error('绘本课程尚未安装')
    }

    const units = await getUnitsByTheme(theme.id)
    books.value = await Promise.all(
      units.map(async (unit) => {
        const lessons = await getLessonsByUnit(unit.id)
        const firstReadingLesson = lessons.find((lesson) => lesson.type === 'READING') || lessons[0]

        if (!firstReadingLesson) {
          return { ...unit, coverImage: '/images/picturebooks/train-station.jpg' }
        }

        const content = typeof firstReadingLesson.content === 'string'
          ? JSON.parse(firstReadingLesson.content)
          : firstReadingLesson.content

        return { ...unit, coverImage: picturebookCover(content) }
      })
    )
  } catch (loadError) {
    error.value = '绘本站点暂时还没整理好，请稍后重试。'
    console.error('加载绘本站点失败:', loadError)
  } finally {
    loading.value = false
  }
}

function openPicturebook(unitId) {
  router.push(`/picturebooks/${unitId}`)
}
</script>

<template>
  <!-- 绘本页固定用暖橙色作为品牌强调色，呼应小火车旅程 -->
  <main
    class="modern-picturebooks modern-page-shell modern-page-shell--top-spaced"
    :style="{ '--brand-accent': 'var(--color-orange)' }"
  >
    <BackBar title="绘本小火车" @back="router.push('/')" />

    <section class="modern-brand-card modern-brand-hero picturebooks-hero">
      <p class="modern-brand-kicker">绘本小火车</p>
      <h1 class="modern-brand-title">坐上小火车，一个故事一个故事听</h1>
      <p class="modern-brand-desc">小火车会带你去一个一个故事站，点一站就能听故事。</p>
    </section>

    <section v-if="loading" class="modern-brand-card modern-brand-state" role="status">正在整理今天的列车站点…</section>
    <section v-else-if="error" class="modern-brand-card modern-brand-state" role="alert">
      <p>{{ error }}</p>
      <AppButton variant="ghost" @click="load">重新加载</AppButton>
    </section>
    <section v-else-if="books.length === 0" class="modern-brand-card modern-brand-state">暂时还没有可出发的站点。</section>

    <template v-else>
      <!-- 第一站是整页唯一主动作：大封面 + 大按钮，直接进入真实绘本 -->
      <section class="modern-brand-card modern-brand-split-card picturebooks-first">
        <img
          :src="firstStation.coverImage"
          :alt="`${firstStation.name}封面`"
          class="modern-brand-media-cover picturebooks-first-cover"
        />
        <div>
          <p class="modern-brand-kicker">第一站</p>
          <h2 class="modern-brand-title">{{ firstStation.name }}</h2>
          <p class="modern-brand-desc">
            已完成 {{ firstStation.completedLessons }} / {{ firstStation.totalLessons }} 小站，
            从这里最适合开始今天的绘本旅程。
          </p>
          <div class="modern-brand-actions">
            <AppButton size="lg" variant="success" @click="openPicturebook(firstStation.id)">
              {{ firstStation.completedLessons === firstStation.totalLessons ? '再听一遍' : '进入第一站' }}
            </AppButton>
          </div>
        </div>
      </section>

      <section class="picturebooks-section" aria-labelledby="picturebooks-stations-title">
        <div class="modern-brand-section-head">
          <div>
            <p class="modern-brand-kicker">后面的小站</p>
            <h2 id="picturebooks-stations-title" class="modern-brand-section-title">下一站去哪儿</h2>
          </div>
          <p class="modern-brand-section-note">先听完第一站，再坐小火车去下一站。</p>
        </div>

        <div class="modern-brand-grid">
          <button
            v-for="book in followingStations"
            :key="book.id"
            type="button"
            class="modern-brand-card modern-brand-row-card"
            @click="openPicturebook(book.id)"
          >
            <img
              :src="book.coverImage"
              :alt="`${book.name}封面`"
              class="modern-brand-media-cover modern-brand-media-cover--thumb"
              loading="lazy"
            />
            <div>
              <h3>{{ book.name }}</h3>
              <p class="modern-brand-note">{{ book.completedLessons }} / {{ book.totalLessons }} 站已完成</p>
            </div>
          </button>
        </div>
      </section>
    </template>
  </main>
</template>

<style scoped>
@import './modern-brand.css';

/* 绘本页只保留间距与第一站封面尺寸，视觉全部来自品牌类 */
.modern-picturebooks {
  min-height: 100vh;
}

.picturebooks-hero,
.picturebooks-first {
  margin-bottom: var(--space-5);
}

/* 第一站封面比通用封面略高，突出主站点 */
.picturebooks-first-cover {
  height: 220px;
}

.picturebooks-section {
  padding-bottom: var(--space-8);
}
</style>
