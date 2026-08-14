# 贡献指南

感谢你对 Mimi 启蒙乐园的关注！无论提交 Issue、修复 Bug、补充课程内容还是完善文档，
都欢迎参与。

## 快速贡献

1. Fork 本仓库
2. 在 `main` 分支基础上创建工作分支（`feature/xxx` / `fix/xxx`，小写连字符）
3. 完成改动并自检
4. 提交 PR，填写模板

## 本地开发

### 环境要求

- JDK 17+
- Maven 3.8+
- Node.js 18+ 与 npm
- （可选）百度智能云语音服务密钥

> 仓库 `AGENTS.md` 与 `docs/project-build-rules.md` 列出了完整约定，
> 贡献代码前请先阅读。

### 启动后端

```bash
cd backend
./run.sh          # 自动加载 .env 并启动
# 或: mvn spring-boot:run
```

如需语音功能，复制 `backend/.env.example` 为 `.env` 填入密钥；未配置时语音接口返回降级提示。

### 启动前端

```bash
cd frontend
npm install
npm run dev       # http://localhost:5173,代理 /api 到后端 8080
```

### 构建单 JAR

```bash
cd backend && mvn test
cd ../frontend && npm ci && npm run build
cd ../backend && mvn clean package
java -jar target/english-app-backend-1.0.0.jar
```

## 代码约定

- 始终用中文回复与注释
- 后端分层：`domain` / `repository` / `service`(+`Impl`) / `controller` / `dto`
- 构造器注入，禁用 `@Autowired` 字段注入
- Controller 不返回 JPA 实体，用 DTO
- 统一响应 `{code, message, data}`，业务异常继承 `RuntimeException`，异常码用枚举
- 前端色值统一用 `var(--xxx)`，定义在 `frontend/src/styles/tokens.css`
- 按钮统一用 `AppButton`，返回栏用 `BackBar`
- 动效只用 CSS `transition`/`keyframes`，不引入动画库

## 课程内容贡献

- 课程与故事内容优先从优秀图书和故事中汲取精华
- 新增或修改课程必须通过新 Flyway 迁移交付，禁止修改已提交迁移
- 每个可视学习项必须配图，`image` 不得为空，emoji 不能替代图片
- 图片文件、课程迁移和素材来源记录必须同批提交

## 测试要求

- Service 公共方法须有单测
- 类名 `XxxTest`，方法 `should_期望行为_当_条件`
- JUnit 5 + Mockito，AAA 模式，测试间独立
- 提交前执行 `cd backend && mvn test` 确保通过

## 提交格式

```
<type>(<scope>): <subject>
```

- type：`feat` / `fix` / `docs` / `style` / `refactor` / `test` / `chore`
- 示例：`feat(lesson): 扩充课时内容,单词课4→8个`

## 提交署名

AI 辅助生成的提交请包含：

```
Co-Authored-By: TRAE Agent <noreply@trae.ai>
```

## 寻找任务

带 `good first issue` 标签的 Issue 是适合新贡献者的入门任务，
`help wanted` 表示需要社区协助。欢迎在 Issue 下留言认领。
