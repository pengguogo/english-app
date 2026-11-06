<!--
  App.vue - 应用根组件
  用途: 提供全局页面容器,限制最大宽度适配移动端与桌面端。
  作者: english-app
  创建日期: 2026-07-20
-->
<script setup>
import ParentVerifyModal from './components/ParentVerifyModal.vue'
</script>

<template>
  <div class="app-container">
    <router-view />
    <!-- 家长二次验证弹窗,由 http 拦截器触发 -->
    <ParentVerifyModal />
  </div>
</template>

<style>
.app-container {
  max-width: var(--content-max);
  margin: 0 auto;
  min-height: 100dvh;
  background: var(--bg-page);
  position: relative;
}
</style>
