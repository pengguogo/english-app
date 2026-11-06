<!--
  ChooseChildView.vue - 选孩子页(给 4–6 岁孩子用)
  用途: 大头像大按钮,孩子点自己的头像进入;家长可新建孩子。
  孩子字段 { id, nickname, avatarUrl };没有 avatarUrl 时按 id 固定显示一个动物头像。
-->
<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AppButton from '../components/AppButton.vue'
import AppModal from '../components/AppModal.vue'
import { getChildren, createChild, selectChild } from '../api/auth'
import { useAuthStore } from '../stores/auth'
import { nextStepAfterAuth } from '../router/authFlow'

// 无头像时按孩子 id 固定选一个动物头像,同一个孩子每次看到的都一样
const AVATARS = ['🐰', '🐻', '🐱', '🐶', '🐼', '🦊']

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const children = ref([])
const loading = ref(true)
const error = ref('')
const createOpen = ref(false)
const newName = ref('')
const saving = ref(false)

const emojiOf = (child) => AVATARS[child.id % AVATARS.length]

async function load() {
  loading.value = true
  error.value = ''
  try {
    children.value = await getChildren()
    if (!children.value.length) createOpen.value = true
  } catch (e) {
    error.value = e.response?.data?.message || '加载失败，请重试'
  } finally {
    loading.value = false
  }
}

async function choose(child) {
  error.value = ''
  try {
    await selectChild(child.id)
    await auth.refresh()
    router.replace(nextStepAfterAuth(auth, route.query.redirect))
  } catch (e) {
    error.value = e.response?.data?.message || '进入失败，请重试'
  }
}

async function create() {
  const nickname = newName.value.trim()
  if (!nickname || saving.value) return
  saving.value = true
  error.value = ''
  try {
    await createChild({ nickname })
    createOpen.value = false
    newName.value = ''
    await load()
  } catch (e) {
    error.value = e.response?.data?.message || '添加失败，请重试'
  } finally {
    saving.value = false
  }
}

async function logout() {
  await auth.logout()
  router.replace('/')
}

onMounted(load)
</script>

<template>
  <main class="choose-page">
    <h1 class="choose-title">你是谁呀？</h1>
    <p v-if="loading" role="status">正在加载...</p>
    <p v-if="error" class="choose-error" role="alert">{{ error }}</p>

    <ul v-if="!loading" class="child-grid">
      <li v-for="child in children" :key="child.id">
        <button type="button" class="child-btn" :aria-label="`我是${child.nickname}`" @click="choose(child)">
          <img v-if="child.avatarUrl" :src="child.avatarUrl" alt="" class="child-avatar" />
          <span v-else class="child-avatar" aria-hidden="true">{{ emojiOf(child) }}</span>
          <span class="child-name">{{ child.nickname }}</span>
        </button>
      </li>
      <li>
        <button type="button" class="child-btn add" aria-label="添加孩子" @click="createOpen = true">
          <span class="child-avatar" aria-hidden="true">➕</span>
          <span class="child-name">添加孩子</span>
        </button>
      </li>
    </ul>

    <AppButton variant="ghost" @click="logout">退出登录</AppButton>

    <AppModal :open="createOpen" title="添加孩子" @close="createOpen = false">
      <form class="create-form" @submit.prevent="create">
        <label for="child-name">孩子的昵称</label>
        <input id="child-name" v-model="newName" maxlength="50" autocomplete="off" required />
        <AppButton block :disabled="saving || !newName.trim()" @click="create">{{ saving ? '保存中...' : '添加' }}</AppButton>
      </form>
    </AppModal>
  </main>
</template>

<style scoped>
.choose-page { min-height: 100dvh; padding: var(--space-6) var(--space-4); display: grid; grid-template-columns: minmax(0, 1fr); align-content: start; justify-items: center; gap: var(--space-6); background: var(--bg-page); }
.choose-title { font-size: var(--text-xl); font-weight: var(--font-bold); color: var(--text-primary); }
.choose-error { color: var(--color-warning); }
.child-grid { list-style: none; padding: 0; margin: 0; width: 100%; max-width: 720px; display: grid; grid-template-columns: repeat(auto-fill, minmax(150px, 1fr)); gap: var(--space-5); }
.child-btn { width: 100%; display: grid; justify-items: center; gap: var(--space-3); padding: var(--space-5) var(--space-3); border: 3px solid var(--border-light); border-radius: var(--radius-lg); background: var(--bg-card); box-shadow: var(--shadow-card); cursor: pointer; font: inherit; transition: transform var(--duration-fast) var(--ease-bounce), box-shadow var(--duration-fast) var(--ease-smooth); }
.child-btn:hover { box-shadow: var(--shadow-hover); }
.child-btn:active { transform: scale(0.96); }
.child-btn:focus-visible { outline: none; box-shadow: var(--focus-ring); }
.child-btn.add { border-style: dashed; }
.child-avatar { width: 110px; height: 110px; display: grid; place-items: center; font-size: 64px; border-radius: 50%; background: var(--color-primary-bg); object-fit: cover; }
.child-name { font-size: var(--text-lg); font-weight: var(--font-bold); color: var(--text-primary); }
.create-form { display: grid; gap: var(--space-3); }
.create-form input { min-height: var(--touch-target); padding: var(--space-3); color: var(--text-primary); background: var(--bg-card); border: 1px solid var(--border-light); border-radius: var(--radius-sm); font: inherit; }
</style>
