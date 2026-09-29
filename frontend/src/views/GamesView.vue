<!--
  GamesView.vue - 游戏页版本包装页
  用途: 根据全局 UI 版本状态切换老版游戏页和新版游戏页。
  作者: TRAE Agent
  创建日期: 2026-09-29
-->
<script setup>
import UiVersionPageShell from '../components/ui-version/UiVersionPageShell.vue'
import LegacyGamesPage from '../components/ui-version/legacy/LegacyGamesPage.vue'
import ModernGamesPage from '../components/ui-version/modern/ModernGamesPage.vue'
</script>

<template>
  <UiVersionPageShell :legacy-page="LegacyGamesPage" :modern-page="ModernGamesPage" />
</template>
