<script setup>
import { computed, onMounted, onUnmounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import BackBar from '../components/BackBar.vue'
import AppButton from '../components/AppButton.vue'
import GrowthChart from '../components/GrowthChart.vue'
import {
  getChildProfile, updateChildProfile, getGrowthMeasurements,
  createGrowthMeasurement, updateGrowthMeasurement, deleteGrowthMeasurement,
  getChildPhotos, addChildPhoto, deleteChildPhoto, getChildPhotoImage, updateChildPhoto
} from '../api/childGrowth'

const today = () => {
  const date = new Date()
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}
const profile = reactive({ nickname: '', birthDate: '', sex: '' })
const router = useRouter()
const form = reactive({ measuredAt: today(), heightCm: '', weightKg: '', note: '' })
const records = ref([])
const photos = ref([])
const accessKey = ref(sessionStorage.getItem('childGrowthKey') || '')
const unlocked = ref(false)
const photoUrls = new Map()
const photoDate = ref(today())
const photoCaption = ref('')
const photoFile = ref(null)
const photoEditingId = ref(null)
const photoInput = ref(null)
const editingId = ref(null)
const loading = ref(true)
const saving = ref(false)
const error = ref('')
const notice = ref('')

const age = computed(() => {
  if (!profile.birthDate) return '填写生日后自动计算'
  const [year, month, day] = profile.birthDate.split('-').map(Number)
  const now = new Date()
  let years = now.getFullYear() - year
  let months = now.getMonth() + 1 - month
  if (now.getDate() < day) months--
  if (months < 0) { years--; months += 12 }
  if (years < 0) return '生日不能晚于今天'
  return `${years} 岁 ${months} 个月`
})

async function load() {
  loading.value = true
  error.value = ''
  try {
    const [child, measurements, savedPhotos] = await Promise.all([getChildProfile(), getGrowthMeasurements(), getChildPhotos()])
    Object.assign(profile, { nickname: child.nickname || '', birthDate: child.birthDate || '', sex: child.sex || '' })
    records.value = measurements
    await setPhotos(savedPhotos)
    unlocked.value = true
  } catch (e) {
    if (e.response?.status === 401) unlocked.value = false
    error.value = e.response?.data?.message || '加载失败，请重试'
  }
  finally { loading.value = false }
}

async function unlock() {
  sessionStorage.setItem('childGrowthKey', accessKey.value)
  await load()
}

async function setPhotos(items) {
  for (const url of photoUrls.values()) URL.revokeObjectURL(url)
  photoUrls.clear()
  photos.value = await Promise.all(items.map(async item => {
    const blob = await getChildPhotoImage(item.id)
    const src = URL.createObjectURL(blob)
    photoUrls.set(item.id, src)
    return { ...item, src }
  }))
}

async function saveProfile() {
  error.value = ''; notice.value = ''; saving.value = true
  try {
    await updateChildProfile({ ...profile, birthDate: profile.birthDate || null, sex: profile.sex || null })
    notice.value = '孩子资料已保存'
  } catch (e) { error.value = e.response?.data?.message || '资料保存失败' }
  finally { saving.value = false }
}

async function saveMeasurement() {
  error.value = ''; notice.value = ''
  if (!form.heightCm && !form.weightKg) { error.value = '身高和体重至少填写一项'; return }
  saving.value = true
  const data = { measuredAt: form.measuredAt, heightCm: form.heightCm || null,
    weightKg: form.weightKg || null, note: form.note || null }
  try {
    if (editingId.value) await updateGrowthMeasurement(editingId.value, data)
    else await createGrowthMeasurement(data)
    records.value = await getGrowthMeasurements()
    resetForm()
    notice.value = '测量记录已保存'
  } catch (e) { error.value = e.response?.data?.message || '记录保存失败，请检查输入' }
  finally { saving.value = false }
}

function edit(item) {
  editingId.value = item.id
  Object.assign(form, { measuredAt: item.measuredAt, heightCm: item.heightCm ?? '',
    weightKg: item.weightKg ?? '', note: item.note ?? '' })
  notice.value = '正在修改所选记录'
}

function resetForm() {
  editingId.value = null
  Object.assign(form, { measuredAt: today(), heightCm: '', weightKg: '', note: '' })
}

async function remove(item) {
  if (!window.confirm(`删除 ${item.measuredAt} 的测量记录？`)) return
  error.value = ''; notice.value = ''
  try {
    await deleteGrowthMeasurement(item.id)
    records.value = await getGrowthMeasurements()
    if (editingId.value === item.id) resetForm()
    notice.value = '记录已删除'
  } catch (e) { error.value = e.response?.data?.message || '删除失败' }
}

function selectPhoto(event) {
  photoFile.value = event.target.files?.[0] || null
}

async function savePhoto() {
  error.value = ''; notice.value = ''
  if (!photoEditingId.value && !photoFile.value) { error.value = '请先选择照片'; return }
  if (photoFile.value && photoFile.value.size > 8 * 1024 * 1024) { error.value = '照片不能超过 8 MB'; return }
  saving.value = true
  try {
    if (photoEditingId.value) {
      await updateChildPhoto(photoEditingId.value, { takenAt: photoDate.value, caption: photoCaption.value })
    } else {
      const data = new FormData()
      data.append('takenAt', photoDate.value)
      data.append('caption', photoCaption.value)
      data.append('image', photoFile.value)
      await addChildPhoto(data)
    }
    await setPhotos(await getChildPhotos())
    resetPhotoForm()
    notice.value = '照片已保存'
  } catch (e) { error.value = e.response?.data?.message || '照片保存失败，请使用 JPG 或 PNG 文件' }
  finally { saving.value = false }
}

function editPhoto(item) {
  photoEditingId.value = item.id
  photoDate.value = item.takenAt
  photoCaption.value = item.caption || ''
  photoFile.value = null
  if (photoInput.value) photoInput.value.value = ''
  notice.value = '正在修改照片日期和说明'
}

function resetPhotoForm() {
  photoEditingId.value = null
  photoDate.value = today(); photoCaption.value = ''; photoFile.value = null
  if (photoInput.value) photoInput.value.value = ''
}

async function removePhoto(item) {
  if (!window.confirm(`删除 ${item.takenAt} 的照片？`)) return
  error.value = ''; notice.value = ''
  try {
    await deleteChildPhoto(item.id)
    await setPhotos(await getChildPhotos())
    notice.value = '照片已删除'
  } catch (e) { error.value = e.response?.data?.message || '照片删除失败' }
}

onMounted(() => { if (accessKey.value) load(); else loading.value = false })
onUnmounted(() => { for (const url of photoUrls.values()) URL.revokeObjectURL(url) })
</script>

<template>
  <main class="profile-page">
    <BackBar title="孩子成长档案" @back="router.push('/')" />
    <h1>孩子成长档案</h1>
    <p class="intro">为家长记录身体变化。年龄按生日计算，测量日期默认今天，都可以修改。</p>
    <p v-if="error" role="alert" class="error">{{ error }}</p>
    <p v-if="notice" role="status" class="notice">{{ notice }}</p>
    <p v-if="loading" role="status">正在加载...</p>
    <section v-if="!unlocked && !loading" class="panel">
      <h2>家长访问</h2>
      <p class="muted">请输入成长档案访问口令。本次浏览会话中会记住它。</p>
      <form @submit.prevent="unlock" class="access-form">
        <label>访问口令<input v-model="accessKey" type="password" autocomplete="off" required /></label>
        <AppButton @click="unlock">进入档案</AppButton>
      </form>
    </section>
    <template v-if="unlocked && !loading">
      <section class="panel">
        <h2>孩子资料</h2>
        <form @submit.prevent="saveProfile" class="fields">
          <label>昵称<input v-model.trim="profile.nickname" required maxlength="50" autocomplete="off" /></label>
          <label>生日<input v-model="profile.birthDate" type="date" :max="today()" /></label>
          <label>年龄<output class="age">{{ age }}</output></label>
          <label>性别（可选）<select v-model="profile.sex"><option value="">暂不填写</option><option value="MALE">男</option><option value="FEMALE">女</option></select></label>
          <div class="actions"><AppButton :disabled="saving" @click="saveProfile">保存资料</AppButton></div>
        </form>
      </section>

      <section class="panel">
        <h2>{{ editingId ? '修改测量记录' : '添加测量记录' }}</h2>
        <form @submit.prevent="saveMeasurement" class="fields">
          <label>测量日期<input v-model="form.measuredAt" type="date" :max="today()" required /></label>
          <label>身高（cm）<input v-model="form.heightCm" type="number" min="20" max="250" step="0.1" inputmode="decimal" /></label>
          <label>体重（kg）<input v-model="form.weightKg" type="number" min="0.5" max="300" step="0.01" inputmode="decimal" /></label>
          <label class="wide">备注（可选）<input v-model.trim="form.note" maxlength="200" placeholder="例如：学校体检" /></label>
          <div class="actions"><AppButton :disabled="saving" @click="saveMeasurement">{{ editingId ? '保存修改' : '保存记录' }}</AppButton><AppButton v-if="editingId" variant="ghost" @click="resetForm">取消修改</AppButton></div>
        </form>
      </section>

      <section class="charts" aria-label="生长轨迹">
        <GrowthChart title="身高轨迹" unit="cm" field="heightCm" :records="records" />
        <GrowthChart title="体重轨迹" unit="kg" field="weightKg" :records="records" />
      </section>

      <section class="panel">
        <h2>历史记录</h2>
        <p v-if="!records.length" class="muted">还没有测量记录。</p>
        <ul v-else class="records">
          <li v-for="item in [...records].reverse()" :key="item.id">
            <div><strong>{{ item.measuredAt }}</strong><span>{{ item.heightCm == null ? '—' : `${item.heightCm} cm` }} · {{ item.weightKg == null ? '—' : `${item.weightKg} kg` }}</span><small v-if="item.note">{{ item.note }}</small></div>
            <div class="record-actions"><button type="button" @click="edit(item)">修改</button><button type="button" @click="remove(item)">删除</button></div>
          </li>
        </ul>
      </section>

      <section class="panel">
        <h2>成长照片</h2>
        <form class="fields" @submit.prevent="savePhoto">
          <label>拍摄日期<input v-model="photoDate" type="date" :max="today()" required /></label>
          <label v-if="!photoEditingId">照片（JPG/PNG，最多 8 MB）<input ref="photoInput" type="file" accept="image/jpeg,image/png" @change="selectPhoto" /></label>
          <label class="wide">一句话记录<input v-model.trim="photoCaption" maxlength="200" placeholder="例如：第一次骑上自行车" /></label>
          <div class="actions"><AppButton :disabled="saving" @click="savePhoto">{{ photoEditingId ? '保存修改' : '保存照片' }}</AppButton><AppButton v-if="photoEditingId" variant="ghost" @click="resetPhotoForm">取消修改</AppButton></div>
        </form>
        <p v-if="!photos.length" class="muted">还没有成长照片。</p>
        <ul v-else class="photo-grid">
          <li v-for="item in photos" :key="item.id">
            <img :src="item.src" :alt="item.caption || `${item.takenAt} 的成长照片`" loading="lazy" />
            <div><strong>{{ item.takenAt }}</strong><span>{{ item.caption }}</span></div>
            <button type="button" @click="editPhoto(item)">修改记录</button>
            <button type="button" @click="removePhoto(item)">删除照片</button>
          </li>
        </ul>
      </section>
    </template>
  </main>
</template>

<style scoped>
.profile-page { max-width: 960px; margin: 0 auto; padding: var(--space-4); color: var(--text-primary); }
h1 { margin: var(--space-4) 0 var(--space-2); font-size: var(--text-lg); }
h2 { margin: 0 0 var(--space-4); font-size: var(--text-base); }
.intro, .muted { color: var(--text-secondary); }
.panel { background: var(--bg-card); border: 1px solid var(--border-light); border-radius: var(--radius-md); padding: var(--space-5); margin: var(--space-5) 0; }
.fields { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: var(--space-4); }
label { display: flex; flex-direction: column; gap: var(--space-2); font-weight: var(--font-medium); }
input, select, .age { box-sizing: border-box; width: 100%; min-height: 44px; padding: var(--space-2) var(--space-3); border: 1px solid var(--border-light); border-radius: var(--radius-sm); background: var(--bg-card); color: var(--text-primary); font: inherit; }
.age { background: var(--bg-muted); }
input:focus, select:focus, button:focus-visible { outline: 2px solid var(--color-primary); outline-offset: 2px; }
.wide, .actions { grid-column: 1 / -1; }
.actions { display: flex; gap: var(--space-2); }
.charts { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: var(--space-4); }
.records { padding: 0; margin: 0; list-style: none; }
.records li { display: flex; justify-content: space-between; gap: var(--space-3); padding: var(--space-3) 0; border-top: 1px solid var(--border-light); }
.records li div:first-child { display: flex; flex-wrap: wrap; gap: var(--space-3); }
.records small { width: 100%; color: var(--text-secondary); }
.record-actions { display: flex; gap: var(--space-2); }
.record-actions button { border: 0; background: transparent; color: var(--color-primary); cursor: pointer; font: inherit; }
.error { color: var(--color-warning); }
.notice { color: var(--text-secondary); }
.access-form { display: flex; align-items: end; gap: var(--space-3); flex-wrap: wrap; }
.access-form label { flex: 1; min-width: 200px; }
.photo-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(180px, 1fr)); gap: var(--space-4); padding: 0; list-style: none; }
.photo-grid li { border: 1px solid var(--border-light); border-radius: var(--radius-sm); overflow: hidden; }
.photo-grid img { display: block; width: 100%; aspect-ratio: 4 / 3; object-fit: cover; }
.photo-grid li div { display: flex; flex-direction: column; gap: var(--space-1); padding: var(--space-2); }
.photo-grid li button { margin: var(--space-2); border: 0; background: transparent; color: var(--color-primary); cursor: pointer; }
@media (max-width: 640px) { .fields, .charts { grid-template-columns: 1fr; } .records li { flex-direction: column; } }
</style>
