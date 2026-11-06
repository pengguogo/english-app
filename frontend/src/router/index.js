/**
 * @file Vue Router 路由配置
 * @description 定义前端应用的五级学习流路由：学科→主题→单元→课时。
 *              所有页面均采用路由懒加载，按需打包，减小首屏体积。
 * @author english-app
 * @since 2026-07-20
 */
import { createRouter, createWebHistory } from 'vue-router'
import { normalizeRouterBase } from '../utils/routerBase'
import { installAuthGuard } from './authFlow'
import { useAuthStore } from '../stores/auth'

// 路由表：home / subject / theme / unit / lesson 五级学习流 + 错题集/我学过的快捷入口
const routes = [
  // 家庭账号: 登录/注册给家长用;选孩子页只要求登录;同意页从家长中心进入
  { path: '/login', name: 'login', component: () => import('../views/LoginView.vue'), meta: { guestOnly: true } },
  { path: '/register', name: 'register', component: () => import('../views/RegisterView.vue'), meta: { guestOnly: true } },
  { path: '/consent', name: 'consent', component: () => import('../views/ConsentView.vue'), meta: { requiresLogin: true } },
  { path: '/choose-child', name: 'choose-child', component: () => import('../views/ChooseChildView.vue'), meta: { requiresLogin: true } },
  { path: '/parent-center', name: 'parent-center', component: () => import('../views/ParentCenterView.vue'), meta: { requiresAuth: true } },
  { path: '/wrong-answers/:id/practice', name: 'wrong-answer-practice', component: () => import('../views/WrongAnswerPracticeView.vue'), meta: { requiresAuth: true } },
  { path: '/character-review', name: 'character-review', component: () => import('../views/CharacterReviewView.vue') },
  { path: '/games/picture-puzzle', name: 'picture-puzzle', component: () => import('../views/PicturePuzzleView.vue'), meta: { requiresAuth: true } },
  { path: '/games/shape-puzzle', name: 'shape-puzzle', component: () => import('../views/ShapePuzzleView.vue'), meta: { requiresAuth: true } },
  { path: '/games/memory-match', name: 'memory-match', component: () => import('../views/MemoryMatchView.vue'), meta: { requiresAuth: true } },
  { path: '/games/pattern-play', name: 'pattern-play', component: () => import('../views/PatternPlayView.vue'), meta: { requiresAuth: true } },
  { path: '/games/tic-tac-toe', name: 'tic-tac-toe', component: () => import('../views/TicTacToeView.vue'), meta: { requiresAuth: true } },
  { path: '/games', name: 'games', component: () => import('../views/GamesView.vue'), meta: { requiresAuth: true } },
  { path: '/games/gomoku', name: 'gomoku', component: () => import('../views/GomokuView.vue'), meta: { requiresAuth: true } },
  { path: '/games/flight-chess', name: 'flight-chess', component: () => import('../views/FlightChessView.vue'), meta: { requiresAuth: true } },
  { path: '/games/adventure-chess', name: 'adventure-chess', component: () => import('../views/AdventureChessView.vue'), meta: { requiresAuth: true } },
  { path: '/games/go', name: 'go', component: () => import('../views/GoView.vue'), meta: { requiresAuth: true } },
  { path: '/models', name: 'models', component: () => import('../views/ModelsView.vue') },
  { path: '/picturebooks', name: 'picturebooks', component: () => import('../views/PicturebooksView.vue') },
  { path: '/picturebooks/:unitId', name: 'picturebook', component: () => import('../views/PicturebookView.vue') },
  { path: '/', name: 'home', component: () => import('../views/HomeView.vue') },
  { path: '/child-profile', name: 'child-profile', component: () => import('../views/ChildProfileView.vue'), meta: { requiresAuth: true } },
  // 成长相册:独立照片墙页面,月份分组+多选下载+分类筛选+批量上传
  { path: '/child-photos', name: 'child-photos', component: () => import('../views/ChildPhotosView.vue'), meta: { requiresAuth: true } },
  { path: '/subject/:subjectId', name: 'subject', component: () => import('../views/SubjectView.vue') },
  { path: '/theme/:themeId', name: 'theme', component: () => import('../views/ThemeView.vue') },
  { path: '/unit/:unitId', name: 'unit', component: () => import('../views/UnitView.vue') },
  { path: '/lesson/:lessonId', name: 'lesson', component: () => import('../views/LessonView.vue') },
  // 错题集:展示答错的 QUIZ/CALCULATE 题目,支持标记掌握与重做
  { path: '/wrong-answers', name: 'wrong-answers', component: () => import('../views/WrongAnswersView.vue'), meta: { requiresAuth: true } },
  // 我学过的:展示已学课时记录,支持复习跳转
  { path: '/learned', name: 'learned', component: () => import('../views/LearnedView.vue'), meta: { requiresAuth: true } }
]

// 创建 history 模式路由
const router = createRouter({
  history: createWebHistory(normalizeRouterBase(import.meta.env.BASE_URL)),
  routes,
  scrollBehavior(to, from, savedPosition) {
    return savedPosition || { top: 0 }
  }
})

// 游客可浏览课程;meta.requiresAuth 的页面需登录、已同意、已选孩子
installAuthGuard(router, useAuthStore)

export default router
