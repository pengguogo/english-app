/**
 * @file Vue Router 路由配置
 * @description 定义前端应用的五级学习流路由：学科→主题→单元→课时。
 *              所有页面均采用路由懒加载，按需打包，减小首屏体积。
 * @author english-app
 * @since 2026-07-20
 */
import { createRouter, createWebHistory } from 'vue-router'

// 路由表：home / subject / theme / unit / lesson 五级学习流 + 错题集/我学过的快捷入口
const routes = [
  { path: '/', name: 'home', component: () => import('../views/HomeView.vue') },
  { path: '/subject/:subjectId', name: 'subject', component: () => import('../views/SubjectView.vue') },
  { path: '/theme/:themeId', name: 'theme', component: () => import('../views/ThemeView.vue') },
  { path: '/unit/:unitId', name: 'unit', component: () => import('../views/UnitView.vue') },
  { path: '/lesson/:lessonId', name: 'lesson', component: () => import('../views/LessonView.vue') },
  // 错题集:展示答错的 QUIZ/CALCULATE 题目,支持标记掌握与重做
  { path: '/wrong-answers', name: 'wrong-answers', component: () => import('../views/WrongAnswersView.vue') },
  // 我学过的:展示已学课时记录,支持复习跳转
  { path: '/learned', name: 'learned', component: () => import('../views/LearnedView.vue') }
]

// 创建 history 模式路由
const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
