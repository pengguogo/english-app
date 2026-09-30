<!--
  ProfileEditModal.vue - 孩子资料编辑弹窗
  用途: 在弹窗中修改昵称、生日与性别,年龄由生日实时推算展示;
        弹窗只负责表单交互,保存动作通过 save 事件交给父组件调用接口。
  作者: english-app
  创建日期: 2026-09-30
-->
<script setup>
import { computed, reactive, ref, watch } from 'vue'
import AppModal from '../AppModal.vue'
import AppButton from '../AppButton.vue'
import { formatAge, today } from '../../utils/date'
import './childgrowth-form.css'

/**
 * 组件 props:
 * @param {Boolean} open - 是否显示弹窗
 * @param {Boolean} saving - 父组件是否正在提交(用于禁用按钮)
 * @param {String} error - 父组件接口返回的错误信息
 * @param {Object} profile - 当前的孩子资料快照(打开弹窗时克隆进表单)
 */
const props = defineProps({
  open: { type: Boolean, default: false },
  saving: { type: Boolean, default: false },
  error: { type: String, default: '' },
  profile: { type: Object, default: () => ({}) }
})

/**
 * 组件事件:
 * @event close - 关闭弹窗(ESC/遮罩/关闭按钮)
 * @event save - 提交表单,payload 为 { nickname, birthDate, sex }
 */
const emit = defineEmits(['close', 'save'])

// 本地表单: 与资料快照隔离,取消修改不污染页面展示
const form = reactive({ nickname: '', birthDate: '', sex: '' })
const localError = ref('')

// 年龄为只读推导值,随生日输入实时变化
const age = computed(() => (form.birthDate ? formatAge(form.birthDate) : '填写生日后自动计算'))

// 每次打开时从最新资料快照重建表单,避免残留上次未保存的输入
watch(() => props.open, (isOpen) => {
  if (isOpen) {
    localError.value = ''
    Object.assign(form, {
      nickname: props.profile.nickname || '',
      birthDate: props.profile.birthDate || '',
      sex: props.profile.sex || ''
    })
  }
})

/**
 * 提交表单: 昵称为必填(AppButton 是 type=button,原生 required 不会被触发,
 * 需在 JS 中兜底校验),通过后把原始值抛给父组件(空值转 null 由父组件统一处理)。
 */
function submit() {
  localError.value = ''
  if (!form.nickname.trim()) {
    localError.value = '请填写昵称'
    return
  }
  emit('save', { nickname: form.nickname.trim(), birthDate: form.birthDate, sex: form.sex })
}
</script>

<template>
  <AppModal :open="open" title="修改孩子资料" @close="emit('close')">
    <form class="cg-form" @submit.prevent="submit">
      <p v-if="localError || error" role="alert" class="cg-error">{{ localError || error }}</p>
      <label class="cg-field cg-wide">昵称
        <input v-model.trim="form.nickname" class="cg-input" required maxlength="50" autocomplete="off" />
      </label>
      <label class="cg-field">生日
        <input v-model="form.birthDate" class="cg-input" type="date" :max="today()" />
      </label>
      <label class="cg-field">年龄
        <output class="cg-input cg-static">{{ age }}</output>
      </label>
      <label class="cg-field cg-wide">性别（可选）
        <select v-model="form.sex" class="cg-select">
          <option value="">暂不填写</option>
          <option value="MALE">男</option>
          <option value="FEMALE">女</option>
        </select>
      </label>
      <div class="cg-actions">
        <AppButton :disabled="saving" @click="submit">保存资料</AppButton>
        <AppButton variant="ghost" :disabled="saving" @click="emit('close')">取消</AppButton>
      </div>
    </form>
  </AppModal>
</template>
