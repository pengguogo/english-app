# 前端开发说明

英语启蒙 APP 的 Vue 3 前端，构建产物嵌入 Spring Boot 静态资源实现单 JAR 部署。

## 技术栈

- Vue 3.5 + Vite 6.3 + Pinia 4.0 + Vue Router 4.6(history 模式)
- Axios 1.18(开发期代理 `/api` 到后端 8080)
- 纯 CSS 动效(不引入动画库)

## 目录结构

```
src/
├── api/                      # Axios 实例 + 各资源 API(subject/theme/unit/lesson/progress/wrongAnswer/voice)
├── components/
│   ├── AppButton.vue         # 统一按钮(variant: primary/success/warning/ghost)
│   ├── BackBar.vue           # 统一返回栏
│   ├── StarBar.vue           # 星级展示
│   ├── AudioButton.vue       # TTS 播放按钮
│   ├── RecordButton.vue      # 录音按钮(wav 16k 16bit mono)
│   └── lesson-templates/     # 7 种课时模板
│       ├── WordLesson.vue / SentenceLesson.vue / ReadingLesson.vue
│       ├── QuizLesson.vue / CalculateLesson.vue
│       ├── PhonicsLesson.vue / DialogueLesson.vue
│       └── LessonComplete.vue
├── composables/              # useRecorder / useTts
├── views/                    # Home / Subject / Theme / Unit / Lesson / WrongAnswers / Learned
├── stores/                   # Pinia: progress / audio
├── router/                   # Vue Router(history 模式)
├── styles/tokens.css         # 设计令牌系统(禁止硬编码色值,一律 var(--xxx))
├── assets/mascot/            # 吉祥物 Mimi(灰老鼠 + 黄围巾)
├── App.vue                   # 容器(max-width 800px 居中)
└── main.js
```

## 设计令牌系统

所有色值、间距、圆角、阴影、动效统一定义在 `styles/tokens.css`，组件通过 `var(--xxx)` 引用，**禁止硬编码色值**。

核心令牌：

| 令牌 | 用途 |
|------|------|
| `--color-primary` `#6B7CFF` | 主按钮/导航/链接 |
| `--color-accent` `#FFD93D` | 星星/奖励/CTA |
| `--color-success` `#4CD964` | 完成/正确反馈 |
| `--color-warning` `#FF6B6B` | 错误/未解锁 |
| `--bg-page` `#FFF8F0` | 页面暖底 |
| `--bg-card` `#FFFFFF` | 卡片白底 |

动效只用 CSS `transition`/`keyframes`，装饰性动画须包裹在 `@media (prefers-reduced-motion: no-preference)` 中。

## 组件规范

- 通用组件 PascalCase 命名，含顶部 Javadoc 风格注释(用途/作者/日期)
- 按钮统一用 `AppButton`，返回栏统一用 `BackBar`
- Props/emits 必须有类型标注和注释
- 课时模板只负责交互展示，公共逻辑(加载/评分/完成/错题上报)由 `LessonView.vue` 统一管理

## 开发命令

```bash
npm install        # 安装依赖
npm run dev        # 开发服务器 http://localhost:5173,代理 /api 到 8080
npm run build      # 构建产物写入 backend/src/main/resources/static/
```

## 响应式适配

移动优先，`App.vue` 容器 `max-width: 800px` 居中：

- 手机(<480px)：单列布局，吉祥物缩小 20%
- 平板(480-800px)：横向布局
- 桌面(>800px)：居中 800px 容器，两侧留白
