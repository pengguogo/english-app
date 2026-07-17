# AI幼小衔接英语APP - 技术设计文档

## 1. 概述

### 1.1 项目背景
基于现有PRD V1.0和20张高保真UI设计图，开发一款面向5-6岁幼儿园大班儿童的英语启蒙Android应用。核心目标为**快速出MVP让孩子先用起来**，同时兼顾面试展示价值。

### 1.2 核心原则
- **速度优先**：3周内出可运行的MVP
- **云就绪**：架构设计为后续迁移上云预留扩展空间
- **面试友好**：技术选型国内主流，架构清晰可讲

### 1.3 技术栈

| 层级 | 技术选型 | 说明 |
|------|----------|------|
| 移动端 | Android (Kotlin + Jetpack Compose) | 原生开发，性能最佳 |
| 后端 | Java + Spring Boot 3.x | 用户舒适区，面试认可度高 |
| 数据库 | SQLite (MVP) → MySQL/PostgreSQL (生产) | 先本地，后迁移 |
| 语音服务 | 百度智能云语音API | 免费额度充足，Java SDK完善 |
| 缓存 | Caffeine (本地) → Redis (生产) | 预留缓存抽象层 |
| 构建工具 | Gradle (Android) / Maven (后端) | 标准工具链 |

---

## 2. 系统架构

### 2.1 整体架构图

```
┌─────────────────────────────────────────────────────────────┐
│                        Android App                           │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │ 首页模块  │  │ 学习地图  │  │ 课程学习  │  │ 个人中心  │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
│  ┌──────────────────────────────────────────────────────┐  │
│  │              ViewModel + Repository层                 │  │
│  └──────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────┐  │
│  │              Retrofit + Room (本地缓存)               │  │
│  └──────────────────────────────────────────────────────┘  │
└───────────────────────────┬─────────────────────────────────┘
                            │ HTTPS/REST
┌───────────────────────────┴─────────────────────────────────┐
│                    Spring Boot Backend                       │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │Controller│  │  Service │  │Repository│  │   DTO    │   │
│  │   层     │  │   层     │  │   层     │  │          │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
│  ┌──────────────────────────────────────────────────────┐  │
│  │              语音服务抽象层 (VoiceService)            │  │
│  │         ┌──────────────┐    ┌──────────────┐        │  │
│  │         │ 百度TTS适配器 │    │ 百度ASR适配器 │        │  │
│  │         └──────────────┘    └──────────────┘        │  │
│  └──────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────┐  │
│  │                   SQLite (MVP阶段)                   │  │
│  │              ↓ 未来可切换为 MySQL/PostgreSQL          │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 分层设计原则

**严格遵循分层架构，每层只依赖下一层：**

- **Controller层**：接收请求、参数校验、返回DTO
- **Service层**：业务逻辑编排、事务管理
- **Repository层**：数据访问、SQL/JPQL
- **Domain层**：实体定义、业务规则

**云迁移友好设计：**
- 所有外部依赖（数据库、缓存、语音服务）通过接口抽象
- 配置全部 externalization（`application.yml` + 环境变量）
- 无状态服务设计，便于容器化部署

---

## 3. 后端设计

### 3.1 模块划分

```
english-app-backend/
├── src/main/java/com/englishapp/
│   ├── controller/          # REST API入口
│   ├── service/             # 业务逻辑
│   ├── repository/          # 数据访问
│   ├── domain/              # 实体/枚举
│   ├── dto/                 # 请求/响应对象
│   ├── config/              # 配置类
│   ├── exception/           # 全局异常处理
│   └── voice/               # 语音服务抽象层
│       ├── VoiceService.java
│       ├── BaiduTtsAdapter.java
│       └── BaiduAsrAdapter.java
└── src/main/resources/
    ├── application.yml      # 主配置（环境无关）
    ├── application-dev.yml  # 开发环境
    ├── application-prod.yml # 生产环境（云部署）
    └── db/migration/        # Flyway迁移脚本
```

### 3.2 核心API设计

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 获取学习地图 | GET | `/api/v1/themes` | 返回所有主题和单元 |
| 获取课程详情 | GET | `/api/v1/lessons/{id}` | 返回课程内容和步骤 |
| 提交学习记录 | POST | `/api/v1/progress` | 记录学习进度 |
| 语音合成 | POST | `/api/v1/voice/tts` | 文字转语音（带缓存） |
| 语音识别 | POST | `/api/v1/voice/asr` | 语音转文字 |
| 获取学习报告 | GET | `/api/v1/reports/{userId}` | 返回学习统计数据 |

### 3.3 语音服务抽象层

**核心设计：适配器模式**

```java
public interface VoiceService {
    byte[] textToSpeech(String text, VoiceType type);
    String speechToText(byte[] audioData);
}

@Component
public class BaiduVoiceService implements VoiceService {
    // 封装百度SDK调用
    // 内置本地缓存：已合成的语音文件缓存在本地磁盘
}
```

**云迁移准备：**
- 接口定义与具体供应商解耦
- 未来切换到阿里云/科大讯飞只需新增Adapter
- 语音文件本地缓存路径可配置为对象存储（OSS/S3）

---

## 4. Android端设计

### 4.1 架构模式：MVVM + Repository

```
UI层 (Compose Screens)
    ↓
ViewModel (状态管理 + 业务逻辑)
    ↓
Repository (数据统一入口)
    ↓
┌─────────────┬─────────────┐
│  Remote API │  Local DB   │
│  (Retrofit) │  (Room)     │
└─────────────┴─────────────┘
```

### 4.2 模块划分

```
app/
├── ui/
│   ├── home/          # 首页
│   ├── map/           # 学习地图
│   ├── lesson/        # 课程学习（单词、句型、游戏）
│   ├── speak/         # AI跟读
│   ├── reward/        # 奖励动画
│   └── profile/       # 个人中心
├── viewmodel/         # ViewModel层
├── repository/        # Repository层
├── data/
│   ├── remote/        # Retrofit API定义
│   ├── local/         # Room数据库
│   └── model/         # 数据模型
├── service/           # 语音播放服务
└── di/                # Hilt依赖注入
```

### 4.3 离线优先策略

- 课程数据首次加载后缓存到Room数据库
- 语音文件下载后本地缓存（复用，减少API调用）
- 学习记录本地先存，网络可用时同步到后端

---

## 5. 数据库设计

### 5.1 实体关系图

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   Theme     │────<│    Unit     │────<│   Lesson    │
│  (主题)      │     │   (单元)     │     │   (课程)     │
└─────────────┘     └─────────────┘     └─────────────┘
                                               │
                                               │
┌─────────────┐     ┌─────────────┐     ┌────┴────────┐
│   User      │────<│UserProgress │────>│ LessonStep  │
│  (用户)      │     │  (学习进度)  │     │  (课程步骤)  │
└─────────────┘     └─────────────┘     └─────────────┘
```

### 5.2 核心表结构（SQLite，与MySQL兼容）

**theme** - 主题表
```sql
CREATE TABLE theme (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(50) NOT NULL,
    icon_url VARCHAR(255),
    sort_order INTEGER DEFAULT 0,
    is_locked BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**lesson** - 课程表
```sql
CREATE TABLE lesson (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    unit_id INTEGER NOT NULL,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL,  -- WORD, SENTENCE, GAME, SPEAK
    content JSON NOT NULL,      -- 课程内容JSON（未来可迁移到MySQL的JSON类型）
    sort_order INTEGER DEFAULT 0,
    star_reward INTEGER DEFAULT 3
);
```

**user_progress** - 学习进度表
```sql
CREATE TABLE user_progress (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    lesson_id INTEGER NOT NULL,
    status VARCHAR(20) DEFAULT 'LOCKED',  -- LOCKED, IN_PROGRESS, COMPLETED
    stars INTEGER DEFAULT 0,
    score INTEGER DEFAULT 0,
    completed_at TIMESTAMP,
    UNIQUE(user_id, lesson_id)
);
```

### 5.3 云迁移准备

- **Flyway迁移脚本**：所有DDL放入`db/migration/`，切换数据库时只需改驱动配置
- **JPA/Hibernate**：MVP用SQLite，生产切MySQL/PostgreSQL无需改代码
- **主键策略**：使用`GenerationType.IDENTITY`，兼容主流数据库

---

## 6. 云迁移准备策略

### 6.1 配置外部化

所有环境相关配置抽离到`application.yml`：

```yaml
spring:
  profiles:
    active: ${ENV:dev}  # 环境变量控制
  datasource:
    url: ${DB_URL:jdbc:sqlite:english.db}
    driver-class-name: ${DB_DRIVER:org.sqlite.JDBC}

voice:
  provider: ${VOICE_PROVIDER:baidu}
  baidu:
    app-id: ${BAIDU_APP_ID:}
    api-key: ${BAIDU_API_KEY:}
    secret-key: ${BAIDU_SECRET_KEY:}
```

### 6.2 无状态服务设计

- **不存储会话**：使用JWT Token，便于水平扩展
- **文件存储抽象**：语音文件先存本地，后续切到OSS/S3只需改实现
- **缓存抽象**：Caffeine本地缓存 → Redis分布式缓存，通过接口切换

### 6.3 容器化准备

- 提供`Dockerfile`（多阶段构建，减小镜像体积）
- 提供`docker-compose.yml`（本地一键启动）
- 云部署时直接推送到阿里云容器服务/腾讯云容器服务

### 6.4 迁移路径

```
阶段1 (MVP): SQLite + 本地文件 + 单机部署
    ↓
阶段2 (家庭共享): MySQL + 对象存储 + 云服务器
    ↓
阶段3 (规模化): MySQL + Redis + OSS + K8s集群
```

---

## 7. 实施阶段

### 7.1 第一阶段：骨架搭建（Week 1）

**目标**：App能打开，看到首页和学习地图

| 任务 | 产出 |
|------|------|
| Android项目搭建（Jetpack Compose + Hilt + Room + Retrofit） | 可编译的空项目 |
| Spring Boot项目搭建（Web + JPA + Flyway） | 可启动的后端 |
| 设计数据库Schema + Flyway迁移脚本 | V1__init.sql |
| 实现主题/单元/课程基础CRUD API | 4个REST接口 |
| 实现首页 + 学习地图页面 | 2个Compose页面 |
| 接入百度TTS SDK（基础播放） | 单词可发音 |

**砍掉的功能**：登录注册（本地用户ID写死）、AI跟读、游戏、奖励

### 7.2 第二阶段：核心闭环（Week 2-3）

**目标**：孩子能完成1节完整课程

| 任务 | 产出 |
|------|------|
| 填充1个主题数据（水果乐园，3个单元，6节课） | 可点击的课程 |
| 实现单词学习页（图片+单词+TTS） | 可交互的单词卡片 |
| 实现"选出水果"小游戏 | 1个选择题游戏 |
| 实现句型学习页 | 句型展示+播放 |
| 实现AI跟读页（百度ASR + 简单评分） | 可录音+反馈 |
| 实现奖励动画页 | 获得星星动画 |
| 实现学习记录本地存储+同步 | 进度可保存 |

### 7.3 第三阶段：扩展迭代（Week 4+）

根据孩子反馈决定优先级：
- 增加主题到5个
- 简单家长报告（学习时长、完成课程数）
- 成长树（能力达成可视化）
- 账号登录（多设备同步）
- 管理后台（课程配置）

---

## 8. 非功能需求

### 8.1 性能目标
- App启动时间 < 3秒
- 课程页面加载 < 1秒（本地缓存后）
- TTS响应 < 500ms（缓存命中）

### 8.2 安全
- API通信HTTPS
- 百度API密钥不硬编码，走环境变量
- 本地数据库加密存储（SQLCipher）

### 8.3 可测试性
- Service层单元测试（JUnit + Mockito）
- Repository层集成测试（@DataJpaTest）
- Android UI测试（Compose Test）

---

## 9. 风险与应对

| 风险 | 影响 | 应对 |
|------|------|------|
| 百度API调用失败 | 语音功能不可用 | 本地预置 fallback 音频文件 |
| 孩子学习兴趣不足 | 项目失去意义 | Week 2必须让孩子试玩，及时调整 |
| Android开发卡壳 | 进度延误 | 先保证功能可用，UI细节后续打磨 |
| 数据库Schema变更 | 迁移成本 | Flyway版本化管理 |

---

## 10. 附录

### 10.1 参考资料
- PRD V1.0：`AI幼小衔接英语APP_PRD_V1.0.docx`
- UI设计图：`儿童英语启蒙APP界面设计.png`（20张高保真设计）

### 10.2 命名规范
- 包名：`com.englishapp.{module}`
- API路径：`/api/v1/{resource}`
- 数据库表：下划线命名，单数形式
