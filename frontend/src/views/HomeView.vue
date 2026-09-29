<!--
  HomeView.vue - 首页版本包装页
  用途: 根据全局 UI 版本状态切换老版首页和新版首页。
  作者: TRAE Agent
  创建日期: 2026-09-29
-->
<script setup>
import UiVersionPageShell from '../components/ui-version/UiVersionPageShell.vue'
import LegacyHomePage from '../components/ui-version/legacy/LegacyHomePage.vue'
import ModernHomePage from '../components/ui-version/modern/ModernHomePage.vue'
</script>

<template>
  <UiVersionPageShell :legacy-page="LegacyHomePage" :modern-page="ModernHomePage" />
</template>
