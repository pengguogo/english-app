# AI 幼小衔接英语启蒙 APP

面向 5-6 岁儿童的英语启蒙学习应用,以游戏化学习地图为核心,结合语音识别与发音评分,让孩子在闯关中自然习得英语。

> **当前阶段**:Phase 2 网页版核心学习闭环已完成,优先以电脑网页版让孩子快速使用并收集反馈。Android 版暂停,待网页版验证后再定。

---

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 后端 | Java + Spring Boot | 17 / 3.2.0 |
| 数据库 | SQLite(开发)→ PostgreSQL(生产) | Flyway 管理迁移 |
| 前端 | Vue 3 + Vite + Pinia + Vue Router | 3.5 / 6.3 / 4.0 / 4.6 |
| HTTP 客户端 | Axios | 1.18 |
| 语音服务 | 百度 TTS + 发音评测(适配器模式抽象) | - |
| 测试 | JUnit 5 + Mockito + H2 | Spring Boot Test |
| 部署 | Vue 构建产物嵌入 Spring Boot `static/`,单 JAR 运行 | - |

> Android 版(Kotlin + Jetpack Compose + Hilt + Retrofit + Room)骨架已保留在 `android/` 目录,后续视网页版反馈决定是否继续迭代。

---

## 项目结构

```
english-app/
├── backend/                          # Spring Boot 后端
│   ├── src/main/java/com/englishapp/
│   │   ├── controller/               # REST API 控制器
│   │   │   ├── ThemeController.java        # 主题
│   │   │   ├── UnitController.java         # 单元
│   │   │   ├── LessonController.java       # 课时
│   │   │   ├── ProgressController.java     # 学习进度
│   │   │   └── VoiceController.java        # TTS / 发音评测
│   │   ├── service/                  # 业务逻辑层(接口 + Impl)
│   │   ├── repository/               # Spring Data JPA 接口
│   │   ├── domain/                   # JPA 实体 + 枚举(LessonType/ProgressStatus)
│   │   ├── dto/                      # 数据传输对象
│   │   ├── voice/                    # 语音服务抽象层(适配器模式)
│   │   ├── common/                   # Result / ResultCode 统一响应
│   │   ├── config/                   # VoiceProperties / WebConfig
│   │   └── exception/                # 全局异常处理
│   ├── src/main/resources/
│   │   ├── application.yml           # 主配置
│   │   ├── static/                   # Vue 构建产物(生产模式)
│   │   └── db/migration/             # Flyway 迁移脚本 V1~V4
│   ├── src/test/                     # 单元测试 + 集成测试
│   ├── .env.example                  # 百度密钥模板
│   ├── run.sh                        # 启动脚本(自动加载 .env)
│   └── pom.xml
├── frontend/                         # Vue 3 前端
│   ├── src/
│   │   ├── api/                      # Axios 实例 + 各资源 API
│   │   ├── components/               # AudioButton / RecordButton / StarBar
│   │   ├── composables/              # useRecorder / useTts
│   │   ├── views/                    # Home / Theme / Unit / Lesson
│   │   ├── stores/                   # Pinia: progress / audio
│   │   ├── router/                   # Vue Router(history 模式)
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

- JDK 17+
- Maven 3.8+(或使用 `./mvnw`)
- Node.js 18+ 与 npm
- (可选)百度智能云语音服务密钥

### 后端启动

```bash
cd backend
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
# 获取主题列表(含交通工具乐园)
curl http://localhost:8080/api/v1/themes

# 获取某主题下单元
curl http://localhost:8080/api/v1/units/theme/2

# 获取某单元下课时
curl http://localhost:8080/api/v1/lessons/unit/4

# 获取全部进度概览
curl http://localhost:8080/api/v1/progress
```

---

## API 文档

所有接口统一响应格式:

```json
{ "code": 200, "message": "success", "data": { ... } }
```

### 主题与单元

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/themes` | 获取所有主题列表 |
| GET | `/api/v1/units/theme/{themeId}` | 获取某主题下所有单元(带进度) |

### 课程

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/lessons/unit/{unitId}` | 获取指定单元的课时列表 |
| GET | `/api/v1/lessons/{id}` | 获取单个课时详情 |

### 学习进度(固定 user_id=1)

| 方法 | 路径 | 说明 | 请求体 |
|------|------|------|--------|
| GET | `/api/v1/progress` | 获取全部进度概览(主题树 + 进度) | - |
| GET | `/api/v1/progress/lesson/{lessonId}` | 获取某课时进度 | - |
| POST | `/api/v1/progress/lesson/{lessonId}/complete` | 标记完成并解锁下一课 | `{ "stars": 3, "score": 85 }` |

### 语音

| 方法 | 路径 | 说明 | 请求 |
|------|------|------|------|
| POST | `/api/v1/voice/tts` | 文本转语音 | body 纯文本 |
| POST | `/api/v1/voice/score` | 发音评测 | `multipart`: audio(wav 16k 16bit mono) + `text` |

---

## 数据库设计

5 张核心表,通过 Flyway 管理迁移(V1 初始化 / V2 水果种子 / V3 交通工具 / V4 课时扩容):

```
theme (主题)
  ├── id (PK)
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
  ├── name, type(WORD/SENTENCE), content(JSON)
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
```

### 课程内容(交通工具乐园)

```
🚗 交通工具乐园
├── Unit 1: 陆地交通(4 课时)
│   ├── L1: 认识汽车(WORD: car, bus, bike, truck)
│   ├── L2: 认识火车(WORD: train, subway, taxi, police car)
│   ├── L3: 我看见了(SENTENCE: I see a car. ...)
│   └── L4: 它是什么颜色(SENTENCE: The car is red. ...)
├── Unit 2: 空中交通(3 课时)
│   ├── L5: 认识飞机(WORD: airplane, helicopter, rocket, balloon)
│   ├── L6: 飞得高(SENTENCE)
│   └── L7: 我喜欢(SENTENCE)
└── Unit 3: 水上交通(3 课时)
    ├── L8: 认识船(WORD: boat, ship, submarine, canoe)
    ├── L9: 在水上(SENTENCE)
    └── L10: 复习大挑战(SENTENCE)
```

> 原"水果乐园"主题保留作为第二个主题。

### 解锁逻辑

- 首次进入:某 unit 下第一课时 `UNLOCKED`,其余 `LOCKED`
- 完成当前课时 → 下一课时 `UNLOCKED`
- 某 unit 所有课时完成 → 下一 unit 解锁
- 每课时可重复挑战,取历史最高分

---

## 架构设计

### 后端分层

严格遵循 Controller → Service → Repository 三层架构,Controller 不含业务逻辑,Service 抛出业务异常,`GlobalExceptionHandler` 统一捕获并返回 `{code, message, data}` 结构。

### 语音服务抽象层

采用适配器模式,`VoiceService` 接口定义 `textToSpeech` 与 `scorePronunciation` 两个方法。`BaiduVoiceService` 通过 `@ConditionalOnProperty` 条件装配,后续可平滑切换至阿里云、腾讯云等其他供应商。

### 前端状态管理

- **Pinia `progress` store**:缓存进度树,减少请求
- **Pinia `audio` store**:统一管理 TTS 播放与录音状态
- **`useRecorder` composable**:基于 Web Audio API 封装录音,强制输出 wav 16kHz 16bit mono
- **`useTts` composable**:TTS 请求 + Blob URL 播放封装

### 单 JAR 部署

Vue 构建产物通过 `vite.config.js` 直接输出到 `backend/src/main/resources/static/`,`WebConfig` 配置 Vue Router history 模式 fallback:非 `/api` 请求统一返回 `index.html`,生产环境无 CORS 问题。

### 云迁移准备

- 配置外部化:敏感信息通过 `.env` 环境变量注入
- 数据库迁移:Flyway 管理,切换到 PostgreSQL 只需改数据源配置
- 无状态服务:后端不持有会话状态,可水平扩展

---

## 测试

```bash
cd backend
mvn test
```

测试分层:
- **单元测试**(Mockito):Service 层业务逻辑(进度解锁、最高分、首课逻辑等)
- **集成测试**(H2 内存数据库):Repository 层 + Controller 层端到端

测试类:`ThemeServiceTest`、`ProgressServiceImplTest`、`ThemeControllerTest`、`ThemeRepositoryTest`。

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
