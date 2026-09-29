<!--
  PicturebooksView.vue - 绘本页版本包装页
  用途: 根据全局 UI 版本状态切换老版绘本页和新版绘本页。
  作者: TRAE Agent
  创建日期: 2026-09-29
-->
<script setup>
import UiVersionPageShell from '../components/ui-version/UiVersionPageShell.vue'
import LegacyPicturebooksPage from '../components/ui-version/legacy/LegacyPicturebooksPage.vue'
import ModernPicturebooksPage from '../components/ui-version/modern/ModernPicturebooksPage.vue'
</script>

<template>
  <UiVersionPageShell :legacy-page="LegacyPicturebooksPage" :modern-page="ModernPicturebooksPage" />
</template>
