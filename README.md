# AI 幼小衔接英语启蒙 APP

面向 5-6 岁儿童的多学科启蒙学习应用，以游戏化学习地图为核心，结合语音识别与发音评分，让孩子在闯关中自然习得英语。已扩展为英语、语文、数学、课外四大学科平台。

> **当前阶段**:Phase 2 多学科网页版核心学习闭环已完成。英语学科已扩展至 60 课时并新增自然拼读、情景对话课型；新增错题本与已学课程复习功能。Android 版骨架保留，待网页版验证后再定。

---

## 功能特性

### 多学科学习平台

| 学科 | 代码 | 主题示例 | 主要课型 |
|------|------|----------|----------|
| 英语 | ENGLISH | 水果乐园 / 交通工具 / 汪汪队 / 小砾工程 | WORD / SENTENCE / PHONICS / DIALOGUE / READING / QUIZ |
| 语文 | CHINESE | 汉字识字 / 古诗朗诵 / 故事阅读 | WORD / SENTENCE / READING |
| 数学 | MATH | 数字认知 / 加减运算 / 图形应用 | WORD / CALCULATE / QUIZ |
| 课外 | EXTRACURRICULAR | 火车科普 | READING / QUIZ |

### 七种课时类型

| 类型 | 说明 | 交互模式 |
|------|------|----------|
| WORD | 看图认字 | 听发音 → 跟读评分 |
| SENTENCE | 句型朗诵 | 听音 → 跟读评分 |
| READING | 图文阅读 | 翻页阅读 |
| QUIZ | 选择题 | 判对错，答错自动入错题本 |
| CALCULATE | 计算题 | 数字输入判对错 |
| PHONICS | 自然拼读 | 字母发音规则学习 |
| DIALOGUE | 情景对话 | 角色扮演式对话练习 |

### 学习闭环

- **错题本**:答错自动记录，支持掌握度统计、标记已掌握、按类型筛选复习
- **已学课程**:已完成课时回顾，含按学科/课型的分类统计与累计星星
- **语音评测**:百度 TTS 合成 + ASR 相似度评分（适配器模式，可切换供应商）
- **单词配图后端托管**:数据库只存图片 key，后端下发完整 URL，迁移云存储零改动
- **吉祥物 Mimi**:灰老鼠 + 黄围巾，仅在欢迎/学习陪伴/完成庆祝三场景出现

---

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 后端 | Java + Spring Boot | 17 / 3.2.0 |
| 数据库 | SQLite(开发)→ PostgreSQL(生产) | Flyway 管理迁移 V1~V18 |
| 前端 | Vue 3 + Vite + Pinia + Vue Router | 3.5 / 6.3 / 4.0 / 4.6 |
| HTTP 客户端 | Axios | 1.18 |
| 语音服务 | 百度 TTS + ASR 发音评测(适配器模式抽象) | - |
| 测试 | JUnit 5 + Mockito + H2 | Spring Boot Test |
| 部署 | Vue 构建产物嵌入 Spring Boot `static/`,单 JAR 运行 | - |

> Android 版(Kotlin + Jetpack Compose + Hilt + Retrofit + Room)骨架已保留在 `android/` 目录，后续视网页版反馈决定是否继续迭代。

---

## 项目结构

```
english-app/
├── backend/                          # Spring Boot 后端
│   ├── src/main/java/com/englishapp/
│   │   ├── controller/               # REST API 控制器(7 个)
│   │   │   ├── SubjectController.java       # 学科
│   │   │   ├── ThemeController.java         # 主题(支持按学科筛选)
│   │   │   ├── UnitController.java          # 单元
│   │   │   ├── LessonController.java        # 课时
│   │   │   ├── ProgressController.java      # 学习进度 + 已学课程
│   │   │   ├── WrongAnswerController.java   # 错题本
│   │   │   └── VoiceController.java         # TTS / 发音评测
│   │   ├── service/                  # 业务逻辑层(接口 + Impl)
│   │   │   └── WordImageResolver.java       # 单词配图 URL 解析器
│   │   ├── repository/               # Spring Data JPA 接口
│   │   ├── domain/                   # JPA 实体 + 枚举(LessonType/ProgressStatus)
│   │   ├── dto/                      # 数据传输对象
│   │   ├── voice/                    # 语音服务抽象层(适配器模式)
│   │   ├── common/                   # Result / ResultCode 统一响应
│   │   ├── config/                   # VoiceProperties / WebConfig / WordImageProperties
│   │   └── exception/                # 全局异常处理
│   ├── src/main/resources/
│   │   ├── application.yml           # 主配置
│   │   ├── static/images/words/      # 单词配图静态资源(后端托管)
│   │   └── db/migration/             # Flyway 迁移脚本 V1~V18
│   ├── src/test/                     # 单元测试 + 集成测试(11 个测试类)
│   ├── .env.example                  # 百度密钥模板
│   ├── run.sh                        # 启动脚本(自动加载 .env)
│   └── pom.xml
├── frontend/                         # Vue 3 前端
│   ├── src/
│   │   ├── api/                      # Axios 实例 + 各资源 API
│   │   ├── components/
│   │   │   ├── AppButton / BackBar / StarBar / AudioButton / RecordButton
│   │   │   └── lesson-templates/     # 7 种课时模板组件
│   │   │       ├── WordLesson / SentenceLesson / ReadingLesson
│   │   │       ├── QuizLesson / CalculateLesson
│   │   │       └── PhonicsLesson / DialogueLesson / LessonComplete
│   │   ├── composables/              # useRecorder / useTts
│   │   ├── views/                    # Home / Subject / Theme / Unit / Lesson / WrongAnswers / Learned
│   │   ├── stores/                   # Pinia: progress / audio
│   │   ├── router/                   # Vue Router(history 模式)
│   │   ├── styles/tokens.css         # 设计令牌系统(禁止硬编码色值)
│   │   ├── App.vue
│   │   └── main.js
│   ├── vite.config.js                # 构建输出到后端 static/,开发期代理 /api
│   └── package.json
├── android/                          # Android 版骨架(暂停迭代)
├── docs/                             # 设计文档与实施计划
└── README.md
```

---

## 快速开始

### 环境要求

- JDK 17+(必须显式指定 JAVA_HOME，系统默认可能是 1.8)
- Maven 3.8+(或使用 `./mvnw`)
- Node.js 18+ 与 npm
- (可选)百度智能云语音服务密钥

### 后端启动

```bash
cd backend
# 必须显式指定 Java 17,否则报"无效的标记: --release"
export JAVA_HOME=/Users/dawn/Library/Java/JavaVirtualMachines/corretto-17.0.13/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH
./run.sh          # 自动加载 .env 并启动
# 或: mvn spring-boot:run
```

后端启动后访问 `http://localhost:8080`,API 文档见下方。

如需接入百度语音服务,复制 `.env.example` 为 `.env` 并填入密钥:

```bash
cp .env.example .env
# 编辑 .env 填入以下字段
# BAIDU_APP_ID=你的AppID
# BAIDU_API_KEY=你的APIKey
# BAIDU_SECRET_KEY=你的SecretKey
```

未配置密钥时后端正常启动,语音接口返回降级提示。

### 前端启动(开发模式)

```bash
cd frontend
npm install
npm run dev
```

Vite 开发服务器运行在 `http://localhost:5173`,自动将 `/api` 请求代理到后端 8080,支持热更新。

### 生产构建(单 JAR 部署)

```bash
# 1. 构建前端,产物自动写入 backend/src/main/resources/static/
cd frontend && npm run build

# 2. 打包后端
cd ../backend && ./mvnw clean package -DskipTests

# 3. 一个 JAR 启动,浏览器访问 http://localhost:8080
java -jar target/english-app-backend-1.0.0.jar
```

### 验证

```bash
# 获取学科列表
curl http://localhost:8080/api/v1/subjects

# 获取英语学科下的主题
curl http://localhost:8080/api/v1/themes/subject/1

# 获取某主题下单元
curl http://localhost:8080/api/v1/units/theme/2

# 获取某单元下课时
curl http://localhost:8080/api/v1/lessons/unit/4

# 获取课时详情(含 image 完整 URL)
curl http://localhost:8080/api/v1/lessons/22

# 获取已学课程统计
curl http://localhost:8080/api/v1/progress/learned/stats

# 获取错题统计
curl http://localhost:8080/api/v1/wrong-answers/stats
```

---

## API 文档

所有接口统一响应格式:

```json
{ "code": 200, "message": "success", "data": { ... } }
```

> 除 TTS 返回音频二进制流外,所有接口均用 `Result<T>` 包装。

### 学科

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/subjects` | 获取所有学科列表 |

### 主题与单元

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/themes/subject/{subjectId}` | 按学科获取主题列表(推荐) |
| GET | `/api/v1/themes` | 获取所有主题(已废弃,兼容旧接口) |
| GET | `/api/v1/units/theme/{themeId}` | 获取某主题下所有单元(带进度) |

### 课程

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/lessons/unit/{unitId}` | 获取指定单元的课时列表 |
| GET | `/api/v1/lessons/{id}` | 获取单个课时详情(含学习内容 JSON,image 已转为完整 URL) |

### 学习进度(固定 user_id=1)

| 方法 | 路径 | 说明 | 请求体 |
|------|------|------|--------|
| GET | `/api/v1/progress/lesson/{lessonId}` | 获取某课时进度 | - |
| GET | `/api/v1/progress/unit/{unitId}` | 批量获取某单元所有课时进度 | - |
| POST | `/api/v1/progress/lesson/{lessonId}/complete` | 标记完成并解锁下一课 | `{ "stars": 3, "score": 85 }` |
| GET | `/api/v1/progress/learned` | 获取已学课时列表(按完成时间降序) | - |
| GET | `/api/v1/progress/learned/stats` | 获取已学课时统计(总数/星星/均分/分类) | - |

### 错题本(固定 user_id=1)

| 方法 | 路径 | 说明 | 请求 |
|------|------|------|------|
| POST | `/api/v1/wrong-answers/record` | 记录错题(同一题累加错误次数) | `{ lessonId, lessonName, questionIndex, questionType, questionSnapshot, userAnswer, correctAnswer }` |
| GET | `/api/v1/wrong-answers` | 查询错题列表(可选 `resolved` 筛选) | - |
| GET | `/api/v1/wrong-answers/stats` | 获取错题统计(总数/未掌握/已掌握/按类型) | - |
| PATCH | `/api/v1/wrong-answers/{id}/resolve` | 标记某错题为已掌握 | - |
| DELETE | `/api/v1/wrong-answers/{id}` | 删除某错题 | - |

### 语音

| 方法 | 路径 | 说明 | 请求 |
|------|------|------|------|
| POST | `/api/v1/voice/tts` | 文本转语音 | `{ "text": "hello", "lan": "en" }`(`lan` 可选,默认 en) |
| POST | `/api/v1/voice/score` | 发音评测 | `multipart`: audio(wav 16k 16bit mono) + `text` |

---

## 数据库设计

7 张核心表,通过 Flyway 管理迁移(V1 初始化 ~ V18 英语课时扩展):

```
subject (学科)            ← V11 新增
  ├── id (PK)
  ├── name, code, icon_url, color, sort_order, is_locked
  └── created_at

theme (主题)
  ├── id (PK)
  ├── subject_id (FK → subject)   ← V12 新增
  ├── name, icon_url, sort_order, is_locked
  └── created_at

unit (单元)
  ├── id (PK)
  ├── theme_id (FK → theme)
  ├── name, sort_order, is_locked
  └── ...

lesson (课程)
  ├── id (PK)
  ├── unit_id (FK → unit)
  ├── name, type(WORD/SENTENCE/READING/QUIZ/CALCULATE/PHONICS/DIALOGUE), content(JSON)
  ├── sort_order, star_reward
  └── ...

app_user (用户)
  ├── id (PK)
  ├── nickname, avatar_url
  └── created_at

user_progress (学习进度)
  ├── id (PK)
  ├── user_id (FK → app_user)
  ├── lesson_id (FK → lesson)
  ├── status(LOCKED/UNLOCKED/COMPLETED)
  ├── stars, score, completed_at
  └── UNIQUE(user_id, lesson_id)

wrong_answer (错题)        ← V17 新增
  ├── id (PK)
  ├── user_id (FK → app_user)
  ├── lesson_id (FK → lesson)
  ├── lesson_name, question_index, question_type
  ├── question_snapshot(JSON), user_answer, correct_answer
  ├── wrong_count, is_resolved, last_wrong_at
  └── UNIQUE(user_id, lesson_id, question_index)
```

### 迁移版本概览

| 版本 | 内容 |
|------|------|
| V1~V4 | 初始化 + 水果 + 交通工具 + 课时扩容 |
| V5~V8 | 水果主题 + 汪汪队 + 小砾工程队主题 |
| V9~V10 | 单词配图字段(V10 用 `json_set` 修复 V9 的 emoji Unicode 问题) |
| V11~V13 | 学科表 + theme.subject_id 外键 + 索引 |
| V14~V16 | 课外火车 / 语文 / 数学 内容播种 |
| V17 | 错题表 |
| V18 | 英语课时扩展 36→60 课(新增 PHONICS/DIALOGUE 内容) |

### 解锁逻辑

- 首次进入:某 unit 下第一课时 `UNLOCKED`,其余 `LOCKED`
- 完成当前课时 → 下一课时 `UNLOCKED`
- 某 unit 所有课时完成 → 下一 unit 解锁
- 每课时可重复挑战,取历史最高分

---

## 架构设计

### 后端分层

严格遵循 Controller → Service → Repository 三层架构,Controller 不含业务逻辑,Service 抛出业务异常,`GlobalExceptionHandler` 统一捕获并返回 `{code, message, data}` 结构。所有 Controller 通过构造器注入,不返回 JPA 实体,统一用 DTO。

### 语音服务抽象层

采用适配器模式,`VoiceService` 接口定义 `textToSpeech` 与 `scorePronunciation` 两个方法。`BaiduVoiceService` 通过 `@ConditionalOnProperty` 条件装配,后续可平滑切换至阿里云、腾讯云等其他供应商。发音评测采用百度 ASR 识别 + 文本相似度评分。

### 课时模板路由

前端 `LessonView` 作为分发器,根据 `lesson.type` 动态渲染对应的模板组件(`WordLesson`/`SentenceLesson`/`ReadingLesson`/`QuizLesson`/`CalculateLesson`/`PhonicsLesson`/`DialogueLesson`)。公共逻辑(加载、评分、完成提交、错题上报)由 `LessonView` 统一管理,模板只负责交互展示。

### 单词配图后端托管

`WordImageResolver` 负责将 `lesson.content` 中 item 的 `image` 字段(纯 key,如 `chase` 或 `paw-patrol/chase`)改写为完整 URL。数据库只存纯 key,迁移云存储时仅需修改 `WordImageProperties` 配置,无需改动数据。仅在课时**详情**接口解析,列表接口不解析以减少开销。

### 前端状态管理

- **Pinia `progress` store**:缓存进度树,减少请求
- **Pinia `audio` store**:统一管理 TTS 播放与录音状态
- **`useRecorder` composable**:基于 Web Audio API 封装录音,强制输出 wav 16kHz 16bit mono
- **`useTts` composable**:TTS 请求 + Blob URL 播放封装

### 设计令牌系统

所有色值、间距、圆角、阴影、动效统一定义在 `frontend/src/styles/tokens.css`,组件通过 `var(--xxx)` 引用,禁止硬编码。吉祥物 Mimi(灰老鼠 + 黄围巾)资产存放在 `frontend/src/assets/mascot/`,仅在欢迎/学习陪伴/完成庆祝三场景出现。动效只用 CSS `transition`/`keyframes`,不引入动画库。

### 单 JAR 部署

Vue 构建产物通过 `vite.config.js` 直接输出到 `backend/src/main/resources/static/`,`WebConfig` 配置 Vue Router history 模式 fallback:非 `/api` 请求统一返回 `index.html`,生产环境无 CORS 问题。

### 云迁移准备

- 配置外部化:敏感信息通过 `.env` 环境变量注入
- 数据库迁移:Flyway 管理,切换到 PostgreSQL 只需改数据源配置
- 无状态服务:后端不持有会话状态,可水平扩展
- 图片存储:`WordImageResolver` 抽象使切换 OSS/COS 只需改配置

---

## 测试

```bash
cd backend
mvn test
```

测试分层:
- **单元测试**(Mockito):Service 层业务逻辑(进度解锁、最高分、首课逻辑、学科查询、图片解析等)
- **集成测试**(H2 内存数据库):Repository 层 + Controller 层端到端

测试类(11 个):
- `SubjectServiceImplTest` / `SubjectControllerTest`
- `ThemeServiceTest` / `ThemeControllerTest` / `ThemeRepositoryTest`
- `UnitServiceImplTest`
- `LessonServiceTest`
- `ProgressServiceImplTest`
- `WordImageResolverTest` / `WordImageResolverMultiDirTest`(多子目录兼容性)
- `LessonTypeTest`(枚举完整性)

---

## 开发计划

### Phase 1:骨架搭建(已完成)

- [x] Spring Boot 项目初始化
- [x] 数据库设计与 Flyway 迁移
- [x] Domain 实体 + Repository + Service + Controller
- [x] 语音服务抽象层(百度 TTS 占位)
- [x] Android 项目初始化(Compose + Hilt + Retrofit + Room)
- [x] 种子数据 + 端到端验证

### Phase 2:网页版核心学习闭环(已完成)

- [x] Vue 3 + Vite 项目初始化,构建产物嵌入后端 static/
- [x] 首页 / 主题 / 单元 / 课时列表页面
- [x] 核心学习页 `LessonView`(听发音 + 跟读 + 评分 + 完成)
- [x] 录音组件 `RecordButton`(wav 16k 16bit mono)
- [x] `StarBar` 与 `AudioButton` 组件
- [x] 进度 API(查询 / 完成 / 解锁下一课)
- [x] 交通工具主题(1 主题 + 3 单元 + 10 课时)
- [x] 进度业务单元测试
- [x] 接入百度语音 TTS + 发音评测

### Phase 2.5:多学科与课型扩展(已完成)

- [x] 多学科重构(Subject 实体 + 学科 API + 学科主题色令牌)
- [x] 语文 / 数学 / 课外学科内容播种
- [x] 英语课时扩展 36→60 课
- [x] 新增 PHONICS / DIALOGUE 课型及模板组件
- [x] 新增 READING / QUIZ / CALCULATE 课型模板
- [x] `LessonView` 重构为按 type 分发的路由器
- [x] 单词配图改为后端托管,后端下发完整 URL
- [x] AI 生成课程插图替换占位图
- [x] 错题本记录与复习功能
- [x] 已学课程回顾与统计

### Phase 3:游戏化与扩展(规划中)

- [ ] 学习地图可视化路径
- [ ] 趣味游戏课程(配对、连线等)
- [ ] 成就系统与勋章
- [ ] 家长端数据看板
- [ ] 内容难度自适应

---

## 分支策略

| 分支 | 用途 |
|------|------|
| `main` | 稳定发布分支 |
| `feature/phase1-skeleton` | Phase 1 骨架开发分支(已合并) |
| `feature/web-version-transport` | Phase 2 网页版 + 交通工具主题(已合并) |

提交信息格式:`<type>(<scope>): <subject>`
- type: `feat` / `fix` / `docs` / `refactor` / `test` / `chore`
- 示例:`feat(lesson): 扩充课时内容,单词课4→8个,句子课3→5个`

---

## 安全说明

- 百度密钥存于 `backend/.env`(已被 `.gitignore` 忽略),`.env.example` 提供模板
- 录音功能需通过 `https://` 或 `localhost` 访问(浏览器安全限制)
- 后续版本将引入 JWT 认证、API 限流与 CORS 白名单
