<!--
  App.vue - 应用根组件
  用途: 提供全局页面容器,限制最大宽度适配移动端与桌面端。
  作者: english-app
  创建日期: 2026-07-20
-->
<script setup>
</script>

<template>
  <div class="app-container">
    <router-view />
  </div>
</template>

<style>
.app-container {
  max-width: 800px;
  margin: 0 auto;
  min-height: 100vh;
  background: var(--bg-page);
  position: relative;
}
</style>
