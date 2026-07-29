# Agent Instructions

## 交互
- 始终用中文回复，代码注释用中文
- 代码超 50 行考虑拆分；生成代码后简述修改点与设计思路

## 包管理器
- 后端: **Maven** (`backend/`)
- 前端: **npm** (`frontend/`)

## 常用命令
| 任务 | 命令 |
|------|------|
| 启动后端 | `cd backend && export JAVA_HOME=/Users/dawn/Library/Java/JavaVirtualMachines/corretto-17.0.13/Contents/Home && export PATH=$JAVA_HOME/bin:$PATH && ./run.sh` |
| 后端测试 | `cd backend && mvn test` |
| 单测类 | `cd backend && mvn test -Dtest=XxxTest` |
| 前端开发 | `cd frontend && npm run dev` |
| 前端构建 | `cd frontend && npm run build` |

> 必须显式指定 JAVA_HOME 为 Corretto 17，否则报 `无效的标记: --release`

## 构建与课程内容
- 项目构建、课程内容和图片交付必须遵守
  [`docs/project-build-rules.md`](docs/project-build-rules.md)
- 正式交付顺序：后端测试 → `npm ci` → 前端构建 → Maven 完整打包；完成前不得使用
  `-DskipTests` 代替验收
- 前端生成物仅写入 `backend/src/main/resources/static/app/`，禁止手工修改
- 新增或修改课程必须通过新 Flyway 迁移交付，禁止修改已提交迁移或只改
  `backend/english.db`
- **新增或修改课程的每个可视学习项必须配图**；`image` 不得为空，emoji 不能替代图片
- 图片文件、课程迁移和素材来源记录必须同批提交；合并前必须验证图片 URL 和课程页面

## 提交署名
AI 提交 MUST 包含：
```
Co-Authored-By: TRAE Agent <noreply@trae.ai>
```

## 项目结构
- `backend/` — Java 17 + Spring Boot 3.2 + JPA + Flyway + SQLite
- `frontend/` — Vue 3 + Vite + Pinia + Vue Router (history)
- `android/` — Kotlin + Jetpack Compose (MVP)
- `docs/` — 设计文档与 specs
- 数据库 `backend/english.db`；迁移 `backend/src/main/resources/db/migration/`

## 后端约定
- 分层: `domain` / `repository` / `service`(+`Impl`) / `controller` / `dto` / `exception` / `config`
- 构造器注入，禁用 `@Autowired` 字段注入
- Controller 不返回 JPA 实体，用 `XxxRequestDTO` / `XxxResponseDTO`
- 统一响应: `{code, message, data}`
- 全局异常: `@RestControllerAdvice`，业务异常继承 `RuntimeException`，异常码用枚举
- 日志: SLF4J，`log.error("描述", e)`，禁用 `printStackTrace`
- 包名 `com.englishapp.xxx`；接口无 `I` 前缀，实现加 `Impl`
- AI 功能放 `service/ai/`，必须设超时 + 降级 + 结果校验

## 数据库
- Flyway 迁移: `V{版本号}__{描述}.sql`；已提交不可改，只能新增
- 表名/字段名 snake_case 单数；主键 `id`；索引 `idx_表名_字段名`
- 固定用户 `user_id=1`，无登录系统

## 前端约定
- 设计令牌统一定义在 `frontend/src/styles/tokens.css`，**禁止硬编码色值**，一律 `var(--xxx)`
- 通用组件在 `frontend/src/components/`，PascalCase 命名（如 `AppButton.vue`）
- 按钮统一用 `AppButton`（variant: primary/success/warning/ghost），返回栏用 `BackBar`
- 动效只用 CSS `transition`/`keyframes`，不引入动画库；装饰动画包裹 `@media (prefers-reduced-motion: no-preference)`
- 路由路径小写连字符（`/user-progress`），name 小写，视图 `XxxView.vue`
- 构建产物嵌入 Spring Boot 静态资源，单 JAR 部署
- 吉祥物: 灰老鼠 Mimi + 黄围巾，仅出现在 welcome/companion/celebrate 三个场景，资产在 `frontend/src/assets/mascot/`

## 测试
- Service 公共方法须有单测；类名 `XxxTest`，方法 `should_期望行为_当_条件`
- JUnit 5 + Mockito，AAA 模式，测试间独立

## Git
- 提交格式: `<type>(<scope>): <subject>`，type: feat/fix/docs/style/refactor/test/chore
- 分支: `main` / `develop` / `feature/xxx` / `fix/xxx`，小写连字符
