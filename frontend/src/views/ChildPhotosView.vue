<!--
  ChildPhotosView.vue - 成长相册页
  用途: 照片独立页面:按月份分组的缩略图墙 + 分类筛选 + 多选批量下载 zip +
        批量上传 + 全屏灯箱浏览 + 单张编辑/删除;交互参考主流相册
        (Google Photos/Immich:月份分组、点图浏览、选中态下点图即勾选);
        数据与动作统一托管在 useChildPhotos,本页只负责展示与编排。
  作者: english-app
  创建日期: 2026-09-30
-->
<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import BackBar from '../components/BackBar.vue'
import AppButton from '../components/AppButton.vue'
import PhotoEditModal from '../components/childgrowth/PhotoEditModal.vue'
import PhotoLightbox from '../components/childgrowth/PhotoLightbox.vue'
import BatchUploadModal from '../components/childgrowth/BatchUploadModal.vue'
import { useChildPhotos } from '../composables/useChildPhotos'

const router = useRouter()
const accessKey = ref('')
// 与成长档案页共用同一口令:会话内已解锁则直接进入相册
const unlocked = ref(Boolean(sessionStorage.getItem('childGrowthKey')))

// 解构组合式函数: 顶层 ref 在模板中自动解包
const {
  photos, categories, loading, downloading, error, notice, selectedCount,
  loadPhotos, loadOrigin,
  isSelected, toggleSelect, toggleSelectAll, clearSelection,
  uploadBatch, downloadZip, removePhoto, updatePhoto
} = useChildPhotos()

// ===== 分类筛选 =====
const filterCategory = ref('')

// 筛选后的照片:按拍摄日期倒序(后端排序仅作兜底,前端保证分组正确)
const filteredPhotos = computed(() => {
  const list = filterCategory.value
    ? photos.value.filter(item => (item.category || '') === filterCategory.value)
    : photos.value
  return [...list].sort((a, b) =>
    (b.takenAt || '').localeCompare(a.takenAt || '') || b.id - a.id)
})

// 当前可见 id: 全选只作用于筛选结果,不影响其他分类下已勾选的照片
const visibleIds = computed(() => filteredPhotos.value.map(item => item.id))
const allVisibleSelected = computed(() =>
  visibleIds.value.length > 0 && visibleIds.value.every(id => isSelected(id)))

// 按月份分组: 列表已倒序,连续同月归为一组
const monthGroups = computed(() => {
  const groups = []
  let lastKey = ''
  for (const photo of filteredPhotos.value) {
    const key = photo.takenAt ? photo.takenAt.slice(0, 7) : 'unknown'
    if (key !== lastKey) {
      lastKey = key
      groups.push({ key, label: monthLabel(key), photos: [] })
    }
    groups[groups.length - 1].photos.push(photo)
  }
  return groups
})

/**
 * 月份键转中文文案。
 * @param {String} key 形如 2026-09,或 unknown(未记录日期)
 * @returns {String} 如 "2026年9月"
 */
function monthLabel(key) {
  if (key === 'unknown') return '未记录日期'
  const [year, month] = key.split('-')
  return `${year}年${Number(month)}月`
}

// ===== 灯箱 =====
const lightboxOpen = ref(false)
const lightboxIndex = ref(0)

/**
 * 打开某张照片的灯箱(在当前筛选列表内定位下标,切图不跨分类)。
 * @param {Object} photo 被点击的照片
 */
function openLightbox(photo) {
  const idx = filteredPhotos.value.findIndex(item => item.id === photo.id)
  lightboxIndex.value = Math.max(0, idx)
  lightboxOpen.value = true
}

/**
 * 点击缩略图: 已有选中时点图即勾选(选择模式),否则打开灯箱浏览,
 * 与 Google Photos / Immich 的交互保持一致。
 * @param {Object} photo 被点击的照片
 */
function handleThumbClick(photo) {
  if (selectedCount.value > 0) toggleSelect(photo.id)
  else openLightbox(photo)
}

// ===== 单张编辑弹窗 =====
const editOpen = ref(false)
const editingPhoto = ref(null)
const editSaving = ref(false)
const editError = ref('')

/**
 * 打开某张照片的信息编辑弹窗。
 * @param {Object} photo 待编辑的照片
 */
function openEdit(photo) {
  editingPhoto.value = photo
  editError.value = ''
  editOpen.value = true
}

/**
 * 保存单张照片信息(日期/说明/分类);失败时把错误挪进弹窗展示,
 * 避免与页面横幅重复报同一句话。
 * @param {Object} payload { takenAt, caption, category }
 */
async function saveEdit(payload) {
  editSaving.value = true
  editError.value = ''
  const ok = await updatePhoto(editingPhoto.value, {
    takenAt: payload.takenAt,
    caption: payload.caption,
    category: payload.category
  })
  editSaving.value = false
  if (ok) {
    editOpen.value = false
    notice.value = '照片信息已更新'
  } else {
    editError.value = error.value
    error.value = ''
  }
}

// ===== 批量上传 =====
const uploadOpen = ref(false)
const uploading = ref(false)
const uploadProgress = ref(null)

/**
 * 执行批量上传并实时回传进度;完成后关闭弹窗并汇总结果。
 * @param {Object} payload { takenAt, category, files }
 */
async function handleUpload(payload) {
  uploading.value = true
  uploadProgress.value = { done: 0, total: payload.files.length, failed: 0 }
  const result = await uploadBatch(payload, (done, total, failed) => {
    uploadProgress.value = { done, total, failed }
  })
  uploading.value = false
  uploadOpen.value = false
  notice.value = result.failed
    ? `已上传 ${result.total - result.failed} 张，${result.failed} 张失败，可重新上传失败项`
    : `已上传 ${result.total} 张照片`
}

// ===== 解锁与初始加载 =====
/**
 * 输入口令进入相册: 写入会话后立即加载;口令错误时列表接口会返回错误提示。
 */
function unlock() {
  if (!accessKey.value) return
  sessionStorage.setItem('childGrowthKey', accessKey.value.trim())
  unlocked.value = true
  loadPhotos()
}

/**
 * 口令失效/输错时重新解锁: 清掉会话密钥回到解锁界面。
 */
function relock() {
  sessionStorage.removeItem('childGrowthKey')
  unlocked.value = false
  accessKey.value = ''
  error.value = ''
}

// 会话内已有口令时自动加载,否则停留在解锁界面
onMounted(() => {
  if (sessionStorage.getItem('childGrowthKey')) loadPhotos()
})
</script>

<template>
  <main class="cp-page" :class="{ 'cp-selecting': selectedCount > 0 }">
    <BackBar title="成长相册" @back="router.push('/child-profile')" />

    <p class="intro">按月份整理孩子的成长瞬间。点击照片全屏浏览，勾选照片批量下载。</p>

    <p v-if="error" role="alert" class="error">
      {{ error }}
      <button v-if="unlocked" type="button" class="cp-relock" @click="relock">重新输入口令</button>
    </p>
    <p v-if="notice" role="status" class="notice">{{ notice }}</p>
    <p v-if="loading" role="status">正在加载...</p>

    <!-- 家长访问解锁 -->
    <section v-if="!unlocked && !loading" class="panel">
      <h2>家长访问</h2>
      <p class="muted">请输入成长档案访问口令。本次浏览会话中会记住它。</p>
      <form class="access-form" @submit.prevent="unlock">
        <label>访问口令
          <input v-model="accessKey" type="password" autocomplete="off" required />
        </label>
        <AppButton :disabled="loading" @click="unlock">进入相册</AppButton>
      </form>
    </section>

    <template v-if="unlocked && !loading">
      <!-- 工具栏: 分类筛选 + 主操作 -->
      <div class="cp-toolbar">
        <div class="cp-filters" role="group" aria-label="分类筛选">
          <button type="button" class="cp-chip" :class="{ 'is-on': !filterCategory }"
            @click="filterCategory = ''">全部 {{ photos.length }}</button>
          <button v-for="tag in categories" :key="tag" type="button" class="cp-chip"
            :class="{ 'is-on': filterCategory === tag }" @click="filterCategory = tag">{{ tag }}</button>
        </div>
        <div class="cp-actions">
          <AppButton @click="uploadOpen = true">批量上传</AppButton>
          <AppButton variant="ghost" :disabled="downloading" @click="downloadZip">
            {{ downloading ? '打包中…' : selectedCount ? `下载选中 ${selectedCount} 张` : '下载全部' }}
          </AppButton>
        </div>
      </div>

      <p v-if="!photos.length" class="cp-empty muted">还没有成长照片，点击「批量上传」留下第一张。</p>
      <p v-else-if="!filteredPhotos.length" class="cp-empty muted">这个分类下还没有照片。</p>

      <!-- 月份分组照片墙 -->
      <section v-for="group in monthGroups" :key="group.key" class="cp-group" :aria-label="group.label">
        <h2 class="cp-month">{{ group.label }}<span class="cp-month-count">{{ group.photos.length }} 张</span></h2>
        <ul class="cp-grid">
          <li v-for="photo in group.photos" :key="photo.id" class="cp-card"
            :class="{ 'is-selected': isSelected(photo.id) }">
            <button type="button" class="cp-thumb" @click="handleThumbClick(photo)">
              <img v-if="photo.src" :src="photo.src" :alt="photo.caption || `${photo.takenAt} 的成长照片`" loading="lazy" />
              <span v-else class="cp-fallback">预览不可用</span>
            </button>
            <!-- 勾选圆钮: 悬浮在缩略图左上角,与浏览按钮分离保证语义清晰 -->
            <button type="button" class="cp-check" :class="{ 'is-on': isSelected(photo.id) }"
              :aria-pressed="isSelected(photo.id)" :aria-label="`选择 ${photo.takenAt} 的照片`"
              @click="toggleSelect(photo.id)">✓</button>
            <div class="cp-meta">
              <strong>{{ photo.takenAt }}</strong>
              <span v-if="photo.caption" class="cp-caption">{{ photo.caption }}</span>
              <div class="cp-row-actions">
                <span v-if="photo.category" class="cp-tag">{{ photo.category }}</span>
                <button type="button" class="cp-link" @click="openEdit(photo)">修改</button>
                <button type="button" class="cp-link danger" @click="removePhoto(photo)">删除</button>
              </div>
            </div>
          </li>
        </ul>
      </section>
    </template>

    <!-- 底部批量操作条: 有选中时滑入 -->
    <Transition name="cp-bar">
      <div v-if="selectedCount > 0" class="cp-batch-bar">
        <div class="cp-batch-inner">
          <span class="cp-batch-count">已选 {{ selectedCount }} 张</span>
          <div class="cp-batch-actions">
            <button type="button" class="cp-batch-btn" @click="toggleSelectAll(visibleIds)">
              {{ allVisibleSelected ? '取消全选' : '全选本组' }}
            </button>
            <button type="button" class="cp-batch-btn danger" @click="clearSelection">清空</button>
            <AppButton :disabled="downloading" @click="downloadZip">{{ downloading ? '打包中…' : '下载' }}</AppButton>
          </div>
        </div>
      </div>
    </Transition>

    <!-- 灯箱/批量上传/单张编辑三类弹层 -->
    <PhotoLightbox v-model:index="lightboxIndex" :open="lightboxOpen" :photos="filteredPhotos"
      :load-image="loadOrigin" @close="lightboxOpen = false" />
    <BatchUploadModal :open="uploadOpen" :categories="categories" :uploading="uploading"
      :progress="uploadProgress" @close="uploadOpen = false" @upload="handleUpload" />
    <PhotoEditModal :open="editOpen" :saving="editSaving" :error="editError" :photo="editingPhoto"
      :categories="categories" @close="editOpen = false" @save="saveEdit" />
  </main>
</template>

<style scoped>
.cp-page { max-width: 960px; margin: 0 auto; padding: var(--space-4); color: var(--text-primary); }
h2 { margin: 0; font-size: var(--text-base); }
.intro, .muted, .notice { color: var(--text-secondary); }
.error { color: var(--color-warning); }
.cp-relock { margin-left: var(--space-2); border: 0; background: transparent; color: var(--color-primary); font: inherit; cursor: pointer; }

/* 解锁面板: 与成长档案页同一卡片形态 */
.panel { background: var(--bg-card); border: 1px solid var(--border-light); border-radius: var(--radius-md); padding: var(--space-5); margin: var(--space-5) 0; }
.access-form { display: flex; align-items: end; gap: var(--space-3); flex-wrap: wrap; }
.access-form label { display: flex; flex-direction: column; gap: var(--space-2); flex: 1; min-width: 200px; font-weight: var(--font-medium); }
.access-form input { box-sizing: border-box; min-height: 44px; padding: var(--space-2) var(--space-3); border: 1px solid var(--border-light); border-radius: var(--radius-sm); background: var(--bg-card); color: var(--text-primary); font: inherit; }
.access-form input:focus { outline: 2px solid var(--color-primary); outline-offset: 2px; }

/* 工具栏: 筛选与主操作同行,空间不足换行 */
.cp-toolbar { display: flex; flex-wrap: wrap; align-items: center; justify-content: space-between; gap: var(--space-3); margin: var(--space-4) 0; }
.cp-filters { display: flex; flex-wrap: wrap; gap: var(--space-2); }
.cp-chip { min-height: var(--touch-target); padding: var(--space-1) var(--space-3); border: 1px solid var(--border-light); border-radius: var(--radius-pill); background: var(--bg-card); color: var(--text-secondary); font: inherit; font-size: var(--text-sm); cursor: pointer; }
.cp-chip.is-on { border-color: transparent; background: var(--color-primary); color: var(--text-on-primary); }
.cp-actions { display: flex; gap: var(--space-2); }

.cp-empty { padding: var(--space-8) 0; text-align: center; }

/* 月份分组: 大标题 + 张数角标 */
.cp-group { margin: var(--space-5) 0; }
.cp-month { display: flex; align-items: baseline; gap: var(--space-2); margin-bottom: var(--space-3); font-size: var(--text-lg); }
.cp-month-count { font-size: var(--text-sm); font-weight: var(--font-normal); color: var(--text-tertiary); }

/* 照片卡片: 方形缩略图自适应网格 */
.cp-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(150px, 1fr)); gap: var(--space-3); padding: 0; list-style: none; }
.cp-card { position: relative; display: flex; flex-direction: column; background: var(--bg-card); border: 1px solid var(--border-light); border-radius: var(--radius-sm); overflow: hidden; }
.cp-thumb { display: block; width: 100%; padding: 0; border: 0; background: var(--bg-muted); cursor: zoom-in; }
.cp-thumb img { display: block; width: 100%; aspect-ratio: 1 / 1; object-fit: cover; }
.cp-fallback { display: flex; width: 100%; aspect-ratio: 1 / 1; align-items: center; justify-content: center; color: var(--text-tertiary); font-size: var(--text-xs); }
.cp-card.is-selected { outline: 3px solid var(--color-primary); outline-offset: -3px; }

/* 勾选圆钮: 半透明白底悬浮在照片上,选中后主色填充 */
.cp-check { position: absolute; top: var(--space-2); left: var(--space-2); z-index: 1; width: 30px; height: 30px; border: 2px solid var(--bg-card); border-radius: var(--radius-pill); background: var(--lb-btn-bg); color: transparent; font-size: var(--text-sm); line-height: 1; cursor: pointer; }
.cp-check.is-on { background: var(--color-primary); border-color: var(--color-primary); color: var(--text-on-primary); }

/* 卡片底部信息: 日期 + 说明 + 分类/操作 */
.cp-meta { display: flex; flex-direction: column; gap: var(--space-1); padding: var(--space-2) var(--space-3); }
.cp-meta strong { font-size: var(--text-sm); }
.cp-caption { color: var(--text-secondary); font-size: var(--text-sm); }
.cp-row-actions { display: flex; align-items: center; gap: var(--space-3); }
.cp-tag { margin-right: auto; padding: 2px var(--space-2); border-radius: var(--radius-pill); background: var(--bg-muted); color: var(--text-secondary); font-size: var(--text-xs); }
.cp-link { min-height: var(--touch-target); padding: 0; border: 0; background: transparent; color: var(--color-primary); font: inherit; font-size: var(--text-sm); cursor: pointer; }
.cp-link.danger { color: var(--color-warning); }

/* 批量操作条: 悬浮页面底部,给内容预留避让空间 */
.cp-batch-bar { position: fixed; left: 0; right: 0; bottom: 0; z-index: 900; background: var(--bg-card); border-top: 1px solid var(--border-light); box-shadow: var(--shadow-card); }
.cp-batch-inner { display: flex; align-items: center; justify-content: space-between; gap: var(--space-3); max-width: 960px; margin: 0 auto; padding: var(--space-3) var(--space-4); }
.cp-batch-count { font-weight: var(--font-medium); }
.cp-batch-actions { display: flex; align-items: center; gap: var(--space-2); }
.cp-batch-btn { min-height: var(--touch-target); border: 0; background: transparent; color: var(--color-primary); font: inherit; cursor: pointer; }
.cp-batch-btn.danger { color: var(--color-warning); }
.cp-selecting { padding-bottom: calc(var(--space-4) + 72px); }

/* 手机端: 工具栏纵向排布,网格更密 */
@media (max-width: 640px) {
  .cp-toolbar { flex-direction: column; align-items: stretch; }
  .cp-actions { justify-content: space-between; }
  .cp-grid { grid-template-columns: repeat(auto-fill, minmax(120px, 1fr)); }
}

/* 操作条滑入/滑出: 尊重"减弱动态效果"设置 */
@media (prefers-reduced-motion: no-preference) {
  .cp-bar-enter-active, .cp-bar-leave-active {
    transition: transform var(--duration-normal) var(--ease-smooth),
      opacity var(--duration-normal) var(--ease-smooth);
  }
  .cp-bar-enter-from, .cp-bar-leave-to { transform: translateY(100%); opacity: 0; }
}
</style>
