<!--
  ChildProfileView.vue - 孩子成长档案页
  用途: 家长查看孩子资料摘要、身高体重轨迹与测量记录;
        页面只做展示,编辑动作(资料/记录)通过弹窗完成,数据与接口调用
        统一托管在 useChildGrowth 组合式函数中;成长照片已独立为相册页
        (/child-photos),此处仅保留入口卡展示照片数量。
  作者: english-app
  创建日期: 2026-07-20 (2026-09-30 重构为摘要+弹窗布局)
-->
<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import BackBar from '../components/BackBar.vue'
import AppButton from '../components/AppButton.vue'
import GrowthChart from '../components/GrowthChart.vue'
import ProfileEditModal from '../components/childgrowth/ProfileEditModal.vue'
import MeasurementEditModal from '../components/childgrowth/MeasurementEditModal.vue'
import { useChildGrowth } from '../composables/useChildGrowth'
import { formatAge } from '../utils/date'

const router = useRouter()
const accessKey = ref('')

// 解构组合式函数: 顶层 ref 在模板中自动解包
const {
  profile, records, photos, unlocked, loading, saving, pageError, notice,
  profileOpen, measurementOpen, editingRecord, modalError,
  load, unlock,
  openProfile, closeProfile, saveProfile,
  openMeasurement, closeMeasurement, saveMeasurement, removeMeasurement
} = useChildGrowth()

// 摘要卡上的只读文案
const age = computed(() => (profile.birthDate ? formatAge(profile.birthDate) : '未填写生日'))
const sexText = computed(() => (profile.sex === 'MALE' ? '男' : profile.sex === 'FEMALE' ? '女' : '未填写'))

// 历史记录倒序展示(最新在前),与既有行为保持一致
const latestRecords = computed(() => [...records.value].reverse())

// 会话内已有口令时自动加载,否则停留在解锁界面
onMounted(() => {
  if (sessionStorage.getItem('childGrowthKey')) load()
})
</script>

<template>
  <main class="profile-page">
    <BackBar title="孩子成长档案" @back="router.push('/')" />

    <p class="intro">记录孩子的身体成长。点击「修改」或「添加」会弹出编辑窗口，不会打断浏览。</p>

    <p v-if="pageError" role="alert" class="error">{{ pageError }}</p>
    <p v-if="notice" role="status" class="notice">{{ notice }}</p>
    <p v-if="loading" role="status">正在加载...</p>

    <!-- 家长访问解锁 -->
    <section v-if="!unlocked && !loading" class="panel">
      <h2>家长访问</h2>
      <p class="muted">请输入成长档案访问口令。本次浏览会话中会记住它。</p>
      <form class="access-form" @submit.prevent="unlock(accessKey)">
        <label>访问口令
          <input v-model="accessKey" type="password" autocomplete="off" required />
        </label>
        <AppButton :disabled="loading" @click="unlock(accessKey)">进入档案</AppButton>
      </form>
    </section>

    <template v-if="unlocked && !loading">
      <!-- 孩子资料摘要: 只读展示,编辑走弹窗 -->
      <section class="panel">
        <div class="panel-head">
          <h2>孩子资料</h2>
          <AppButton @click="openProfile">修改</AppButton>
        </div>
        <dl class="summary">
          <div class="summary-item"><dt>昵称</dt><dd>{{ profile.nickname || '未填写' }}</dd></div>
          <div class="summary-item"><dt>年龄</dt><dd>{{ age }}</dd></div>
          <div class="summary-item"><dt>生日</dt><dd>{{ profile.birthDate || '未填写' }}</dd></div>
          <div class="summary-item"><dt>性别</dt><dd>{{ sexText }}</dd></div>
        </dl>
      </section>

      <!-- 生长轨迹 -->
      <section class="charts" aria-label="生长轨迹">
        <GrowthChart title="身高轨迹" unit="cm" field="heightCm" :records="records" />
        <GrowthChart title="体重轨迹" unit="kg" field="weightKg" :records="records" />
      </section>

      <!-- 测量记录 -->
      <section class="panel">
        <div class="panel-head">
          <h2>测量记录</h2>
          <AppButton @click="openMeasurement()">添加记录</AppButton>
        </div>
        <p v-if="!records.length" class="muted">还没有测量记录，点击「添加记录」开始记录。</p>
        <ul v-else class="records">
          <li v-for="item in latestRecords" :key="item.id">
            <div class="record-main">
              <strong>{{ item.measuredAt }}</strong>
              <span>{{ item.heightCm == null ? '—' : `${item.heightCm} cm` }} · {{ item.weightKg == null ? '—' : `${item.weightKg} kg` }}</span>
              <small v-if="item.note">{{ item.note }}</small>
            </div>
            <div class="row-actions">
              <button type="button" @click="openMeasurement(item)">修改</button>
              <button type="button" class="danger" @click="removeMeasurement(item)">删除</button>
            </div>
          </li>
        </ul>
      </section>

      <!-- 成长相册入口: 照片墙已独立成页,支持月份分组/分类/批量上传与打包下载 -->
      <section class="panel photo-entry" aria-label="成长相册入口">
        <div class="entry-main">
          <h2>成长相册</h2>
          <p class="muted">
            {{ photos.length ? `共 ${photos.length} 张照片，按月份整理` : '还没有照片，去留下第一张' }}
          </p>
        </div>
        <AppButton @click="router.push('/child-photos')">
          {{ photos.length ? '查看相册' : '去添加照片' }}
        </AppButton>
      </section>
    </template>

    <!-- 两个编辑弹窗: 状态与接口调用在 useChildGrowth 中 -->
    <ProfileEditModal
      :open="profileOpen" :saving="saving" :error="modalError" :profile="profile"
      @close="closeProfile" @save="saveProfile"
    />
    <MeasurementEditModal
      :open="measurementOpen" :saving="saving" :error="modalError" :record="editingRecord"
      @close="closeMeasurement" @save="saveMeasurement"
    />
  </main>
</template>

<style scoped>
.profile-page { max-width: 960px; margin: 0 auto; padding: var(--space-4); color: var(--text-primary); }
h2 { margin: 0; font-size: var(--text-base); }
.intro, .muted, .notice { color: var(--text-secondary); }
.error { color: var(--color-warning); }

/* 卡片与卡片头部: 标题与主操作按钮同行,右对齐 */
.panel { background: var(--bg-card); border: 1px solid var(--border-light); border-radius: var(--radius-md); padding: var(--space-5); margin: var(--space-5) 0; }
.panel-head { display: flex; align-items: center; justify-content: space-between; gap: var(--space-3); margin-bottom: var(--space-4); }

/* 资料摘要: 四格只读信息 */
.summary { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: var(--space-4); margin: 0; }
.summary-item { display: flex; flex-direction: column; gap: var(--space-1); padding: var(--space-3); background: var(--bg-muted); border-radius: var(--radius-sm); }
.summary-item dt { color: var(--text-secondary); font-size: var(--text-sm); }
.summary-item dd { margin: 0; font-weight: var(--font-bold); }

/* 解锁表单 */
.access-form { display: flex; align-items: end; gap: var(--space-3); flex-wrap: wrap; }
.access-form label { display: flex; flex-direction: column; gap: var(--space-2); flex: 1; min-width: 200px; font-weight: var(--font-medium); }
.access-form input { box-sizing: border-box; min-height: 44px; padding: var(--space-2) var(--space-3); border: 1px solid var(--border-light); border-radius: var(--radius-sm); background: var(--bg-card); color: var(--text-primary); font: inherit; }
.access-form input:focus { outline: 2px solid var(--color-primary); outline-offset: 2px; }

/* 轨迹图双列 */
.charts { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: var(--space-4); }

/* 测量记录列表 */
.records { padding: 0; margin: 0; list-style: none; }
.records li { display: flex; justify-content: space-between; gap: var(--space-3); padding: var(--space-3) 0; border-top: 1px solid var(--border-light); }
.record-main { display: flex; flex-wrap: wrap; gap: var(--space-3); align-items: baseline; }
.record-main small { width: 100%; color: var(--text-secondary); }

/* 行内轻量操作按钮(修改/删除) */
.row-actions { display: flex; gap: var(--space-2); }
.row-actions button { border: 0; background: transparent; color: var(--color-primary); cursor: pointer; font: inherit; min-height: var(--touch-target); }
.row-actions button.danger { color: var(--color-warning); }

/* 成长相册入口卡: 说明文案与跳转按钮左右布局,窄屏自动换行 */
.photo-entry { display: flex; align-items: center; justify-content: space-between; gap: var(--space-4); flex-wrap: wrap; }
.photo-entry .entry-main { display: flex; flex-direction: column; gap: var(--space-1); }

/* 手机端: 摘要与轨迹图退化为单列 */
@media (max-width: 640px) {
  .summary, .charts { grid-template-columns: 1fr 1fr; }
  .records li { flex-direction: column; }
}
@media (max-width: 480px) {
  .summary { grid-template-columns: 1fr; }
}
</style>
