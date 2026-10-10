<script setup>
import { ref } from 'vue'
import AppButton from '../AppButton.vue'
const props = defineProps({ configured: Boolean, saving: Boolean, save: { type: Function, required: true } })
const password = ref('')
const confirm = ref('')
const error = ref('')
async function submit() {
  if (props.saving) return
  error.value = ''
  if (!/^[0-9]{6}$/.test(password.value)) { error.value = '请输入6位数字密码'; return }
  if (password.value !== confirm.value) { error.value = '两次输入的密码不一致'; return }
  if (await props.save(password.value)) password.value = confirm.value = ''
}
</script>

<template>
  <form class="password-form" @submit.prevent="submit">
    <h2>{{ configured ? '修改游戏密码' : '设置游戏密码' }}</h2>
    <p>密码只用于临时解锁游戏。忘记后，可用成长档案访问口令进入此页重新设置。</p>
    <label for="parent-password">新的6位数字密码</label>
    <input id="parent-password" v-model="password" type="password" inputmode="numeric" maxlength="6" autocomplete="new-password" required />
    <label for="parent-password-confirm">再次输入密码</label>
    <input id="parent-password-confirm" v-model="confirm" type="password" inputmode="numeric" maxlength="6" autocomplete="new-password" required />
    <p v-if="error" role="alert">{{ error }}</p>
    <AppButton :disabled="saving" @click="submit">{{ saving ? '保存中...' : '保存游戏密码' }}</AppButton>
  </form>
</template>

<style scoped>
.password-form { display: grid; gap: var(--space-3); padding: var(--space-5); border-radius: var(--radius-lg); background: var(--bg-card); }
.password-form input { padding: var(--space-3); border: 1px solid var(--border-light); border-radius: var(--radius-sm); color: var(--text-primary); background: var(--bg-card); }
.password-form p { color: var(--text-secondary); }
</style>
