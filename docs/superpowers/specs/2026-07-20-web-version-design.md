# 网页版英语学习应用 - 技术设计文档

> **日期**: 2026-07-20
> **状态**: 已批准,待实现计划
> **作者**: 用户 + TRAE 协作
> **关联**: 替代原 Android 优先路径,改为先做网页版快速让孩子使用并获取反馈

---

## 一、背景与目标

### 1.1 背景
Phase 1 已完成骨架搭建:Spring Boot 后端(5 张表、4 个 REST API、TTS 抽象层)、Android 应用(Jetpack Compose 骨架)、水果乐园种子数据。

但 Android 真机调试链路长、反馈慢,无法快速让孩子试用并收集反馈。决定先做**电脑网页版**,用最短路径让孩子用上、拿到真实反馈,再决定后续迭代方向。

### 1.2 目标
- **核心目标**:让孩子在电脑浏览器中完成"听发音 → 跟读 → 评分 → 进度持久化"的完整学习闭环
- **速度优先**:今晚/明天就能让孩子用上
- **验证假设**:孩子是否愿意持续使用?发音评分是否真的有激励作用?
- **次要目标**:面试展示用作品(不影响核心决策)

### 1.3 非目标
- 不做用户登录注册系统
- 不做多用户切换
- 不做完整后台管理
- 不做 Android 版同步更新(Android 版暂停,待网页版验证后再定)

---

## 二、整体架构

### 2.1 架构图

```
┌─────────────────────────────────────────┐
│         浏览器(孩子用的电脑)            │
│  ┌───────────────────────────────────┐  │
│  │  Vue 3 SPA                       │  │
│  │  - 路由: / /theme/:id /unit/:id  │  │
│  │           /lesson/:id            │  │
│  │  - Pinia: 进度/音频状态          │  │
│  │  - Web Audio API: 录制 wav       │  │
│  │  - <audio>: 播放 TTS            │  │
│  └──────────────┬────────────────────┘  │
└─────────────────┼───────────────────────┘
                  │ HTTP/JSON(同源,无 CORS)
                  ▼
┌─────────────────────────────────────────┐
│   Spring Boot 单体(端口 8080)         │
│  ┌───────────────────────────────────┐  │
│  │  static/ ← Vue 构建产物(生产)   │  │
│  │  Controller 层(已有 + 新增)     │  │
│  │  Service 层(Lesson/Progress)    │  │
│  │  Voice 抽象层(BaiduVoiceService)│  │
│  │  Repository(JPA + SQLite)       │  │
│  └───────────────────────────────────┘  │
│           │           │                │
│           ▼           ▼                │
│      SQLite      百度语音 API          │
│   (english.db)  (TTS + ASR + 评测)     │
└─────────────────────────────────────────┘
```

### 2.2 关键架构决策

| 决策 | 选择 | 理由 |
|------|------|------|
| 前端技术栈 | Vue 3 + Vite + Pinia + Vue Router | 用户目标学习栈之一;Vite 热更新快;生态轻量 |
| 部署方式 | Vue 构建产物嵌入 Spring Boot `static/` 目录 | 一个 jar 包即可运行,无 CORS,孩子电脑装 Java 就能用 |
| 用户模型 | 单用户固定 `user_id=1`,无登录 | 省掉 80% 用户管理代码,集中验证学习闭环 |
| 课程主题 | 交通工具(新增)+ 保留水果乐园 | 无版权问题;男宝女宝都感兴趣;内容制作快 |
| 语音服务 | 百度 TTS + 发音评测 | 已配置密钥(`backend/.env`),真实评分闭环 |
| 进度持久化 | 复用 `user_progress` 表,后端管理解锁 | 数据模型已就绪,无需改表 |
| 录音格式 | wav 16kHz 16bit mono | 百度语音 API 要求 |

### 2.3 开发与部署流程

**开发期**:
- 后端:`cd backend && ./run.sh`(端口 8080,自动加载 `.env`)
- 前端:`cd frontend && npm run dev`(端口 5173,Vite proxy `/api` → 8080)
- 两端并行,前端热更新秒级生效

**生产构建**:
```bash
cd frontend && npm run build
# 产物自动输出到 ../backend/src/main/resources/static/
cd ../backend && ./mvnw clean package -DskipTests
java -jar target/english-app-backend-0.0.1-SNAPSHOT.jar
# 浏览器访问 http://localhost:8080
```

### 2.4 vite.config.ts 关键配置
```ts
export default defineConfig({
  build: { outDir: '../backend/src/main/resources/static' },
  server: {
    proxy: {
      '/api': { target: 'http://localhost:8080', changeOrigin: true }
    }
  }
})
```

---

## 三、前端页面与路由

### 3.1 路由表(Vue Router,history 模式)

| 路径 | 页面组件 | 功能 |
|------|----------|------|
| `/` | `HomeView` | 主题地图(显示主题卡 + 累计星星数) |
| `/theme/:themeId` | `ThemeView` | 单元列表(显示锁定状态) |
| `/unit/:unitId` | `UnitView` | 课时列表(显示完成状态和星星) |
| `/lesson/:lessonId` | `LessonView` | 核心学习页(展示 + TTS + 录音 + 评分 + 完成) |

### 3.2 页面流程
```
首页 → 主题卡 → 单元列表 → 课时列表 → 学习页
                                  ↓
                              完成后返回课时列表(下一课解锁)
```

### 3.3 页面元素

**HomeView**:
- 顶部:孩子头像 + 昵称"小朋友" + 累计星星总数 ⭐
- 主体:主题卡(交通工具乐园 🚗🚌✈️ + 水果乐园保留)
- 底部:设置按钮(预留,第一版放"重置进度"按钮)

**LessonView 核心交互布局**:
```
┌─────────────────────────────────┐
│  ← 返回         ⭐⭐⭐ 进度    │
├─────────────────────────────────┤
│        🚗  (大图标)            │
│        car                      │
│        /kɑːr/  (音标)          │
│        小汽车                    │
│   🔊 听发音    🎤 跟读          │
├─────────────────────────────────┤
│  [上一切]      得分:--      [下一步] │
└─────────────────────────────────┘
```

**LessonView 交互**:
- **听发音**:点 🔊 → 调 TTS API → `<audio>` 播放
- **跟读**:点 🎤 开始录音(最长 5 秒)→ 再点停止 → 上传 → 后端调百度评测 → 返回分数 → 显示星星
- **评分映射**:≥80 三星,60-79 两星,40-59 一星,<40 鼓励重试(不显示失败)
- **下一步**:标记完成 + 存进度 → 解锁下一课时 → 跳转

---

## 四、数据模型

### 4.1 现有表(保持不变)
`theme`, `unit`, `lesson`, `app_user`, `user_progress`

### 4.2 lesson 表 content 字段 JSON 结构

**单词课(WORD 类型)**:
```json
{
  "type": "WORD",
  "items": [
    {"word": "car", "phonetic": "/kɑːr/", "translation": "小汽车", "emoji": "🚗"},
    {"word": "bus", "phonetic": "/bʌs/", "translation": "公交车", "emoji": "🚌"}
  ]
}
```

**句型课(SENTENCE 类型)**:
```json
{
  "type": "SENTENCE",
  "items": [
    {"sentence": "I see a car.", "translation": "我看见一辆小汽车。", "emoji": "🚗"},
    {"sentence": "The bus is big.", "translation": "公交车很大。", "emoji": "🚌"}
  ]
}
```

### 4.3 user_progress 字段语义
- `status`: `LOCKED` / `UNLOCKED` / `COMPLETED`
- `stars`: 0-3(基于评分映射)
- `score`: 百度评测返回的历史最高分

### 4.4 解锁逻辑
- 首次进入:某 unit 下第一课时 `UNLOCKED`,其余 `LOCKED`
- 完成当前课时(`COMPLETED`)→ 下一课时变 `UNLOCKED`
- 某 unit 所有课时完成 → 下一 unit 解锁
- 默认每课时可重复挑战(取最高分)

---

## 五、API 设计

### 5.1 现有 API(保留)
| 方法 | 路径 | 功能 |
|------|------|------|
| GET | `/api/v1/themes` | 获取所有主题 |
| GET | `/api/v1/lessons/unit/{unitId}` | 获取某单元下课时 |
| GET | `/api/v1/lessons/{id}` | 获取课时详情 |
| POST | `/api/v1/voice/tts` | 文字转语音 |

### 5.2 新增 API

**进度相关**:
| 方法 | 路径 | 功能 | 请求体 | 响应 |
|------|------|------|--------|------|
| GET | `/api/v1/progress` | 获取全部进度概览 | - | 主题树 + 进度 |
| GET | `/api/v1/progress/lesson/{lessonId}` | 获取某课时进度 | - | `{status, stars, score}` |
| POST | `/api/v1/progress/lesson/{lessonId}/complete` | 标记完成并解锁下一课 | `{stars, score}` | `{nextLessonId, unlocked}` |

**单元相关(补全)**:
| 方法 | 路径 | 功能 |
|------|------|------|
| GET | `/api/v1/units/theme/{themeId}` | 获取某主题下所有单元(带进度) |

**语音相关(扩展)**:
| 方法 | 路径 | 功能 | 请求 | 响应 |
|------|------|------|------|------|
| POST | `/api/v1/voice/score` | 发音评测 | `multipart`: audio + `text` | `{score: 85, feedback: "..."}` |

### 5.3 统一响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

---

## 六、后端架构调整

### 6.1 新增类清单

```
controller/
├── ProgressController.java     # 进度查询 + 完成
├── UnitController.java        # 单元列表(补全)
└── VoiceController.java        # 扩展 /score

service/
├── ProgressService.java + Impl
├── UnitService.java + Impl
└── voice/
    ├── VoiceService.java        # 接口(扩展 scorePronunciation)
    ├── BaiduVoiceService.java   # 百度实现(扩展评测)
    └── dto/
        ├── TtsRequest.java
        ├── ScoreRequest.java
        └── ScoreResponse.java

config/
└── WebConfig.java              # 静态资源映射(Vue history 模式 fallback)
```

### 6.2 WebConfig 关键职责
- 让 Vue Router history 模式生效:非 `/api` 请求统一返回 `index.html`
- 开发期 CORS 放行 `localhost:5173`
- 静态资源缓存头配置

### 6.3 百度语音集成
- **TTS**:调百度语音合成 REST API → 返回 mp3/wav 二进制
- **评测**:前端录音(wav 16k 16bit mono)→ multipart 上传 → 后端转 base64 → 调百度语音评测 API → 解析分数返回
- **降级**:百度 API 失败时返回 503 + 友好错误信息,前端提示"再试一次"

---

## 七、前端组件设计

### 7.1 目录结构
```
frontend/
├── src/
│   ├── api/
│   │   ├── http.js          # axios 实例 + 拦截器
│   │   ├── theme.js
│   │   ├── unit.js
│   │   ├── lesson.js
│   │   ├── progress.js
│   │   └── voice.js
│   ├── components/
│   │   ├── StarBar.vue      # 星星显示组件
│   │   ├── AudioButton.vue  # 播放发音按钮
│   │   ├── RecordButton.vue # 录音按钮
│   │   ├── LessonCard.vue   # 课时卡片
│   │   └── UnitCard.vue     # 单元卡片
│   ├── views/
│   │   ├── HomeView.vue
│   │   ├── ThemeView.vue
│   │   ├── UnitView.vue
│   │   └── LessonView.vue
│   ├── stores/
│   │   ├── progress.js      # Pinia: 进度状态
│   │   └── audio.js         # Pinia: 播放/录音状态
│   ├── composables/
│   │   ├── useRecorder.js   # Web Audio API 录音封装
│   │   └── useTts.js        # TTS 请求 + 播放封装
│   ├── router/
│   │   └── index.js
│   ├── App.vue
│   └── main.js
├── vite.config.ts
└── package.json
```

### 7.2 关键组件职责

**AudioButton.vue**(复用度高):
- 输入:`text` prop
- 点击:调 TTS API → 创建 Blob URL → `<audio>` 播放
- 加载/播放状态视觉反馈

**RecordButton.vue**:
- 点击开始录音(`MediaRecorder` API),再点停止
- 录音中显示波形动画(第一版用简单红圈即可)
- 停止后 emit `recorded` 事件,传出 wav Blob

**LessonView.vue**(核心):
- 加载课时 content JSON,按 WORD/SENTENCE 类型渲染
- 每个 item 一张卡片:emoji + 文字 + 音标 + 中文
- AudioButton + RecordButton 组合
- 完成所有 item 后显示"完成本课"按钮 → 调 complete API

### 7.3 useRecorder.js 录音格式约束
- 采样率 16000Hz
- 单声道
- 16 位深
- 格式 wav(百度 API 要求)
- 最长录音时长 5 秒

---

## 八、课程内容(交通工具主题)

### 8.1 主题结构

```
🚗 交通工具乐园(1 个主题)
├── Unit 1: 陆地交通(4 课时)
│   ├── L1: 认识汽车(WORD: car, bus, bike, truck)
│   ├── L2: 认识火车(WORD: train, subway, taxi, police car)
│   ├── L3: 我看见了(SENTENCE: I see a car. I see a bus. ...)
│   └── L4: 它是什么颜色(SENTENCE: The car is red. The bus is yellow. ...)
├── Unit 2: 空中交通(3 课时)
│   ├── L5: 认识飞机(WORD: airplane, helicopter, rocket, balloon)
│   ├── L6: 飞得高(SENTENCE: The airplane flies high. ...)
│   └── L7: 我喜欢(SENTENCE: I like the airplane. ...)
└── Unit 3: 水上交通(3 课时)
    ├── L8: 认识船(WORD: boat, ship, submarine, canoe)
    ├── L9: 在水上(SENTENCE: The boat is on the water. ...)
    └── L10: 复习大挑战(SENTENCE: 综合复习前 9 课内容)
```

### 8.2 种子数据迁移
- 新建 `V3__transport_theme.sql`
- 保留原"水果乐园"数据(作为第二个主题)
- 新增:1 主题 + 3 单元 + 10 课时

---

## 九、错误处理与降级

| 场景 | 处理方式 |
|------|----------|
| 后端未启动 | 前端全局拦截器 → 友好提示"网络连接失败,请检查后端服务" |
| TTS 调用失败 | AudioButton 显示"发音加载失败,点击重试" |
| 录音权限未授予 | RecordButton 引导:"请允许麦克风权限才能跟读" |
| 百度评测 API 超时 | 后端返回 503,前端提示"评分服务忙碌,再试一次" |
| 百度评测返回低分 | 不显示"失败",改为鼓励:"差一点点,再试一次!" |
| 浏览器不支持 Web Audio | 首屏检测,提示"请使用 Chrome/Edge 浏览器" |
| 进度保存失败 | 本地 localStorage 兜底,下次进入提示"同步进度" |

---

## 十、测试策略

### 10.1 后端测试(JUnit 5 + Mockito)
- `ProgressServiceTest`:完成课时 → 解锁下一课、星星计算、边界(最后一课)
- `BaiduVoiceServiceTest`:mock 百度 API 响应,验证解析逻辑
- `ProgressControllerTest`:API 层参数校验、响应格式

### 10.2 前端测试(第一版轻量)
- 手动测试清单(10 课时完整走通)
- 关键 composable 单测:`useRecorder` 录音格式验证
- 预留 Vitest 配置,核心工具函数加单测

### 10.3 集成测试
- 后端 `@SpringBootTest`:启动 → 调 API → 验证数据库写入
- 端到端:手动走完"首页 → 学完一课 → 进度更新 → 解锁"全链路

---

## 十一、安全与配置

### 11.1 密钥管理
- `backend/.env` 存放百度密钥(已被 `.gitignore` 忽略)
- `backend/.env.example` 提供模板(可提交)
- `backend/run.sh` 启动时自动加载 `.env`

### 11.2 已配置密钥
- `BAIDU_APP_ID=7921478`
- `BAIDU_API_KEY` 和 `BAIDU_SECRET_KEY` 已填入(不在此明文展示)

### 11.3 浏览器权限
- 麦克风:首次使用引导授权
- 必须通过 `https://` 或 `localhost` 访问(浏览器安全限制)

---

## 十二、成功标准

### 12.1 第一版验收标准
- [ ] 孩子能在浏览器访问 `http://localhost:8080` 看到首页
- [ ] 能进入"交通工具乐园"主题,看到 3 个单元
- [ ] 能完成"认识汽车"课时:听发音 + 录音跟读 + 看到评分 + 看到星星
- [ ] 完成课时后,下一课时自动解锁
- [ ] 进度在刷新页面后依然保留(后端持久化)
- [ ] 孩子能独立走完 1 个完整课时,无需大人讲解

### 12.2 验证假设
- 孩子是否愿意持续使用?(观察 15-20 分钟专注度)
- 发音评分是否真的有激励作用?(观察孩子重复挑战行为)
- 内容难度是否合适?(观察孩子挫败/无聊情绪)
