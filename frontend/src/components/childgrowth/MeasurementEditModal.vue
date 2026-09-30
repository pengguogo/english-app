<!--
  MeasurementEditModal.vue - 测量记录编辑弹窗
  用途: 新增或修改一条身高/体重测量记录;传入 record 时为修改模式并预填数据,
        不传则为新增模式并默认测量日期为今天。保存动作交给父组件完成。
  作者: english-app
  创建日期: 2026-09-30
-->
<script setup>
import { computed, reactive, ref, watch } from 'vue'
import AppModal from '../AppModal.vue'
import AppButton from '../AppButton.vue'
import { today } from '../../utils/date'
import './childgrowth-form.css'

/**
 * 组件 props:
 * @param {Boolean} open - 是否显示弹窗
 * @param {Boolean} saving - 父组件是否正在提交(用于禁用按钮)
 * @param {String} error - 父组件接口返回的错误信息
 * @param {Object} record - 待修改的测量记录;null 表示新增模式
 */
const props = defineProps({
  open: { type: Boolean, default: false },
  saving: { type: Boolean, default: false },
  error: { type: String, default: '' },
  record: { type: Object, default: null }
})

/**
 * 组件事件:
 * @event close - 关闭弹窗
 * @event save - 提交表单,payload 为 { measuredAt, heightCm, weightKg, note }
 */
const emit = defineEmits(['close', 'save'])

// 标题随模式切换,让用户清楚当前是在新增还是修改
const title = computed(() => (props.record ? '修改测量记录' : '添加测量记录'))

// 本地表单: 后端返回的空值为 null,统一转为空串便于输入框绑定
const form = reactive({ measuredAt: today(), heightCm: '', weightKg: '', note: '' })
const localError = ref('')

// 打开时按模式重建表单: 修改模式预填所选记录,新增模式回到默认值
watch(() => props.open, (isOpen) => {
  if (!isOpen) return
  localError.value = ''
  if (props.record) {
    Object.assign(form, {
      measuredAt: props.record.measuredAt,
      heightCm: props.record.heightCm ?? '',
      weightKg: props.record.weightKg ?? '',
      note: props.record.note ?? ''
    })
  } else {
    Object.assign(form, { measuredAt: today(), heightCm: '', weightKg: '', note: '' })
  }
})

/**
 * 提交表单: 身高与体重至少填写一项,通过后把原始值抛给父组件。
 */
function submit() {
  localError.value = ''
  if (!form.heightCm && !form.weightKg) {
    localError.value = '身高和体重至少填写一项'
    return
  }
  emit('save', { ...form })
}
</script>

<template>
  <AppModal :open="open" :title="title" @close="emit('close')">
    <form class="cg-form" @submit.prevent="submit">
      <p v-if="localError || error" role="alert" class="cg-error">{{ localError || error }}</p>
      <label class="cg-field">测量日期
        <input v-model="form.measuredAt" class="cg-input" type="date" :max="today()" required />
      </label>
      <label class="cg-field">身高（cm）
        <input v-model="form.heightCm" class="cg-input" type="number" min="20" max="250" step="0.1" inputmode="decimal" />
      </label>
      <label class="cg-field">体重（kg）
        <input v-model="form.weightKg" class="cg-input" type="number" min="0.5" max="300" step="0.01" inputmode="decimal" />
      </label>
      <label class="cg-field cg-wide">备注（可选）
        <input v-model.trim="form.note" class="cg-input" maxlength="200" placeholder="例如：学校体检" />
      </label>
      <div class="cg-actions">
        <AppButton :disabled="saving" @click="submit">{{ record ? '保存修改' : '保存记录' }}</AppButton>
        <AppButton variant="ghost" :disabled="saving" @click="emit('close')">取消</AppButton>
      </div>
    </form>
  </AppModal>
</template>
