# AI 幼小衔接英语启蒙 APP

面向 5-6 岁儿童的英语启蒙学习应用，以游戏化学习地图为核心，结合语音识别与发音评分，让孩子在闯关中自然习得英语。

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 后端 | Java + Spring Boot | 17 / 3.2.0 |
| 数据库 | SQLite（开发）→ PostgreSQL（生产） | Flyway 管理迁移 |
| 前端 | Kotlin + Jetpack Compose | 1.9.20 / BOM 2023.10 |
| 依赖注入 | Hilt（前端）/ 构造器注入（后端） | 2.48 |
| 网络层 | Retrofit + Gson | 2.9.0 |
| 本地缓存 | Room | 2.6.1 |
| 语音服务 | 百度 TTS / ASR（适配器模式抽象） | - |

## 项目结构

```
english-app/
├── backend/                          # Spring Boot 后端
│   ├── src/main/java/com/englishapp/
│   │   ├── controller/               # REST API 控制器
│   │   ├── service/                  # 业务逻辑层
│   │   ├── repository/               # Spring Data JPA 接口
│   │   ├── domain/                   # JPA 实体 + 枚举
│   │   ├── dto/                      # 数据传输对象
│   │   ├── config/                   # 配置类
│   │   ├── exception/                # 全局异常处理
│   │   └── voice/                    # 语音服务抽象层（适配器模式）
│   ├── src/main/resources/
│   │   ├── application.yml           # 主配置
│   │   └── db/migration/             # Flyway 迁移脚本
│   └── src/test/                     # 单元测试 + 集成测试
├── android/                          # Android 前端
│   └── app/src/main/java/com/englishapp/
│       ├── data/
│       │   ├── model/                # Room Entity
│       │   ├── remote/               # Retrofit API
│       │   └── local/                # Room DAO
│       ├── di/                       # Hilt 依赖注入模块
│       ├── repository/               # 离线优先策略
│       └── ui/
│           ├── home/                 # 首页（主题列表）
│           ├── map/                  # 学习地图（课程列表）
│           └── theme/                # Material 主题配置
└── docs/                             # 设计文档与实施计划
```

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- Android Studio（Iguana 或更高）
- Android SDK 34（minSdk 26）

### 后端启动

```bash
cd backend
mvn spring-boot:run
```

后端启动后访问 `http://localhost:8080`，API 文档见下方。

如果需要接入百度语音服务，设置环境变量：

```bash
export BAIDU_APP_ID=你的AppID
export BAIDU_API_KEY=你的APIKey
export BAIDU_SECRET_KEY=你的SecretKey
```

未配置时后端正常启动，语音接口返回空数据占位。

### Android 启动

1. 用 Android Studio 打开 `android/` 目录
2. 等待 Gradle 同步完成
3. 启动模拟器（API 26+）或连接真机
4. 点击 Run

模拟器通过 `10.0.2.2` 访问宿主机后端，无需额外配置。

### 验证

后端启动后，执行以下命令验证：

```bash
# 获取主题列表
curl http://localhost:8080/api/v1/themes
# 预期: [{"id":1,"name":"水果乐园","iconUrl":null,"sortOrder":1,"isLocked":false}]

# 获取单元课程
curl http://localhost:8080/api/v1/lessons/unit/1
# 预期: [{"id":1,"unitId":1,"name":"认识水果","type":"WORD","content":"{\"words\":[\"apple\",\"banana\",\"orange\"]}","sortOrder":1,"starReward":3}]
```

## API 文档

### 主题

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/themes` | 获取所有主题列表 |

### 课程

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/lessons/unit/{unitId}` | 获取指定单元的课程列表 |
| GET | `/api/v1/lessons/{id}` | 获取单个课程详情 |

### 语音

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/v1/voice/tts` | 文本转语音（body 为纯文本） |

## 数据库设计

5 张核心表，通过 Flyway 管理迁移：

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
  ├── name, type(WORD/SENTENCE/GAME/SPEAK), content(JSON)
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
  ├── status(LOCKED/IN_PROGRESS/COMPLETED)
  ├── stars, score, completed_at
  └── UNIQUE(user_id, lesson_id)
```

### 种子数据

预置数据供开箱即用验证：
- 1 个用户（"小朋友"）
- 1 个主题（"水果乐园"）
- 3 个单元（Unit 1 解锁，Unit 2/3 锁定）
- 1 节课程（"认识水果"，含 apple/banana/orange）

## 架构设计

### 后端分层

严格遵循 Controller → Service → Repository 三层架构，Controller 不含业务逻辑，Service 抛出业务异常，全局异常处理器统一捕获。

### 语音服务抽象层

采用适配器模式，`VoiceService` 接口定义 `textToSpeech` 和 `speechToText` 两个方法。`BaiduVoiceService` 通过 `@ConditionalOnProperty` 条件装配，后续可平滑切换至阿里云、腾讯云等其他供应商。

### Android 离线优先

`ThemeRepository` 先请求远程 API，成功后写入 Room 本地缓存；网络异常时降级读取本地数据，保证离线可用。

### 云迁移准备

- 配置外部化：敏感信息通过环境变量注入
- 数据库迁移：Flyway 管理，切换到 PostgreSQL 只需改数据源配置
- 无状态服务：后端不持有会话状态，可水平扩展

## 测试

```bash
cd backend
mvn test
```

测试分层：
- **单元测试**（Mockito）：Service 层逻辑
- **集成测试**（H2 内存数据库）：Repository 层 + Controller 层端到端

## 开发计划

### Phase 1：骨架搭建（已完成）

- [x] Spring Boot 项目初始化
- [x] 数据库设计与 Flyway 迁移
- [x] Domain 实体 + Repository + Service + Controller
- [x] 语音服务抽象层（百度 TTS 占位）
- [x] Android 项目初始化（Compose + Hilt + Retrofit + Room）
- [x] 首页 UI（主题列表）
- [x] 学习地图页面（课程列表 + 导航）
- [x] 种子数据 + 端到端验证

### Phase 2：核心学习闭环（规划中）

- [ ] 接入真实百度语音 API（TTS 播放单词/句子）
- [ ] 单词学习页面（图文 + 发音 + 跟读）
- [ ] 发音评分（ASR + 评分算法）
- [ ] 学习进度记录与星星奖励
- [ ] 更多课程内容填充

### Phase 3：游戏化与扩展

- [ ] 学习地图可视化路径
- [ ] 趣味游戏课程（配对、连线等）
- [ ] 成就系统与勋章
- [ ] 家长端数据看板

## 分支策略

| 分支 | 用途 |
|------|------|
| `main` | 稳定发布分支 |
| `feature/phase1-skeleton` | Phase 1 开发分支 |

提交信息格式：`<type>(<scope>): <subject>`
- type: feat / fix / docs / refactor / chore
- 示例：`feat(lesson): add word learning page with TTS playback`
