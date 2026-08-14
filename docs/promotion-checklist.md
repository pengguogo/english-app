# 仓库推广执行清单

本文件汇总需要在 GitHub 网页端或 `gh` CLI 执行的操作。代码层面的改动
（README、LICENSE、Dockerfile 等）已随本次提交完成，以下为需要在仓库后台
操作的项。

> 登录 GitHub 后，`gh auth login` 完成 CLI 认证，即可用下方脚本批量执行。

---

## 1. 仓库 About 描述

在仓库主页右上角 About（齿轮图标）填入：

```
Open-source AI-powered learning platform for kids aged 4–6, featuring
pronunciation assessment, phonics, reading and math. Vue 3 + Spring Boot.
```

中文版（可选，作为简短描述）：

```
面向 4-6 岁儿童的开源启蒙学习平台：发音评分、自然拼读、语文阅读、数学练习。
```

---

## 2. Topics

在 About 面板「Topics」添加以下标签（逐个输入回车）：

```
kids-learning
educational-app
english-learning
phonics
speech-recognition
text-to-speech
vue3
spring-boot
java
game-based-learning
self-hosted
open-source
```

---

## 3. Social Preview 图

GitHub 推荐社交预览图尺寸 1280×640。制作一张包含：

- 产品截图（首页或学习页）
- 吉祥物 Mimi（灰老鼠 + 黄围巾）
- 一句话卖点：`Mimi 启蒙乐园 · 开源儿童启蒙学习平台`

上传路径：仓库 Settings → Social preview → Edit → Upload image。

---

## 4. 创建 good first issue

建议一次性创建以下 8 个 Issue，均打 `good first issue` 或 `help wanted`
标签。可用下方脚本一键创建。

### Issue 清单

1. **[good first issue] 增加动物主题单词课程**
   - 添加一组原创动物主题单词课（如 farm animals / zoo animals），含配图
   - 涉及：新增 Flyway 迁移 + `static/images/words/` 配图

2. **[good first issue] 添加英文版 README**
   - 将 README.md 翻译为 README.en.md，并在中文 README 顶部加语言切换链接
   - 便于国际开发者了解项目

3. **[good first issue] 补充首页截图与 GIF 到 docs/assets/**
   - 按 docs/recording-guide.md 录制演示 GIF 与 3 张关键页面截图
   - 替换 README 中的占位链接

4. **[good first issue] 修复 JAR 版本号不一致**
   - pom.xml version 为 3.2.0，但 README 曾写 `english-app-backend-1.0.0.jar`
   - 统一为 pom.xml 实际版本，或在 pom.xml 配置 finalName

5. **[help wanted] 增加腾讯云语音适配器**
   - 在 `backend/src/main/java/com/englishapp/voice/` 新增 TencentVoiceService
   - 实现 VoiceService 接口，通过 @ConditionalOnProperty 条件装配
   - 参考 BaiduVoiceService 实现方式

6. **[help wanted] 编写 Docker Compose 生产配置**
   - 当前 docker-compose.yml 为基础版，需补充带 HTTPS 反向代理的生产配置
   - 可选 nginx + Let's Encrypt 示例

7. **[design] 设计原创课程主题替换第三方品牌**
   - 将「汪汪队」等第三方品牌化主题替换为原创角色与主题
   - 需配套设计原创角色形象与课程配图

8. **[content] 校对自然拼读课程内容**
   - 检查 V18 新增 PHONICS 课程的字母发音规则是否准确
   - 补充缺失的字母组合发音示例

### 批量创建脚本

```bash
gh auth login -h github.com   # 先登录

# 逐条创建（labels 需先存在，gh issue create 会自动创建不存在的 label）
gh issue create \
  --title "[good first issue] 增加动物主题单词课程" \
  --body "添加一组原创动物主题单词课（如 farm animals / zoo animals），含配图。涉及：新增 Flyway 迁移 + static/images/words/ 配图。" \
  --label "good first issue,enhancement"

gh issue create \
  --title "[good first issue] 添加英文版 README" \
  --body "将 README.md 翻译为 README.en.md，并在中文 README 顶部加语言切换链接，便于国际开发者了解项目。" \
  --label "good first issue,docs"

gh issue create \
  --title "[good first issue] 补充首页截图与 GIF" \
  --body "按 docs/recording-guide.md 录制演示 GIF 与 3 张关键页面截图，替换 README 中的占位链接。" \
  --label "good first issue,docs"

gh issue create \
  --title "[good first issue] 修复 JAR 版本号不一致" \
  --body "pom.xml version 为 3.2.0，但 README 曾写 english-app-backend-1.0.0.jar。统一版本号或在 pom.xml 配置 finalName。" \
  --label "good first issue,bug"

gh issue create \
  --title "[help wanted] 增加腾讯云语音适配器" \
  --body "在 backend/src/main/java/com/englishapp/voice/ 新增 TencentVoiceService，实现 VoiceService 接口，通过 @ConditionalOnProperty 条件装配。参考 BaiduVoiceService 实现方式。" \
  --label "help wanted,enhancement"

gh issue create \
  --title "[help wanted] 编写 Docker Compose 生产配置" \
  --body "当前 docker-compose.yml 为基础版，需补充带 HTTPS 反向代理的生产配置，可选 nginx + Let's Encrypt 示例。" \
  --label "help wanted"

gh issue create \
  --title "[design] 设计原创课程主题替换第三方品牌" \
  --body "将「汪汪队」等第三方品牌化主题替换为原创角色与主题，需配套设计原创角色形象与课程配图。" \
  --label "enhancement"

gh issue create \
  --title "[content] 校对自然拼读课程内容" \
  --body "检查 V18 新增 PHONICS 课程的字母发音规则是否准确，补充缺失的字母组合发音示例。" \
  --label "content"
```

---

## 5. 发布 v0.1.0 Release

```bash
git tag -a v0.1.0 -m "v0.1.0: 开源首发 - 60 节英语课程 + 完整学习闭环"
git push origin v0.1.0

# 创建 Release 并附构建产物
gh release create v0.1.0 \
  --title "v0.1.0 - 开源首发" \
  --notes "首个公开版本。详见 CHANGELOG。" \
  backend/target/english-app-backend-*.jar
```

---

## 6. Demo 部署建议

推荐方案（任选其一）：

- **Railway / Render / Fly.io**：直接连接 GitHub 仓库，用 Dockerfile 构建，
  免费 tier 可体验。注意 SQLite 在容器重启会丢失，需挂载持久卷或换 PostgreSQL。
- **自有服务器**：`docker compose up`，配 nginx 反向代理 + Let's Encrypt。
- **GitHub Pages**（仅静态预览）：构建前端后部署，但语音/进度 API 无法用，
  仅作 UI 预览。

部署后在 README「在线体验」处填入真实地址。
