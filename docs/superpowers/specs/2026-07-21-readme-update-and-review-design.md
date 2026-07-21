# README 更新与代码复审设计文档

> 日期: 2026-07-21
> 状态: 已实施
> 主题: 根据近期代码改动全量重写 README，并对文档与代码进行复审，产出改进建议

## 1. 背景与目标

项目 README 停留在 2026-07-20 的 Phase 2 状态(交通工具 10 课时、5 张表、迁移 V1~V4、5 组 API)，但 7-20 之后有 30+ 次提交未反映：

- 多学科重构(Subject 实体 + 英语/语文/数学/课外四学科)
- 英语课时扩展 36→60 课，新增 PHONICS/DIALOGUE 课型
- 7 种课时类型与 7 个前端模板组件
- 错题本(wrong_answer 表 + 5 个 API)
- 已学课程回顾与统计
- 单词配图后端托管(WordImageResolver)
- 迁移推进到 V18

**目标**：

1. 全量重写 `README.md`，采用"经典结构 + 功能矩阵"，准确反映当前代码状态
2. 替换 `frontend/README.md`(原为 Vite 默认模板)为前端开发说明
3. 对文档准确性/完整性与代码分层/坏味道/测试覆盖/迁移规范性进行复审，产出分级改进建议清单

## 2. README 重写设计

### 结构骨架

```
# AI 幼小衔接英语启蒙 APP
> 一句话定位 + 当前阶段(Phase 2.5 多学科网页版闭环)
## 功能特性(新增)
  - 多学科学习平台矩阵
  - 七种课时类型表
  - 学习闭环(错题本/已学课程/语音评测/图片托管/吉祥物)
## 技术栈(更新迁移 V1~V18、ASR 评测)
## 项目结构(7 controller / 7 view / lesson-templates)
## 快速开始(显式 JAVA_HOME)
## API 文档(按 8 组资源分组)
## 数据库设计(7 表 + 迁移版本概览)
## 架构设计(分层/语音适配器/课时模板路由/图片托管/令牌系统/单JAR/云迁移)
## 测试(11 个测试类)
## 开发计划(Phase 1/2/2.5/3)
## 分支策略 + 安全说明
```

### 关键变更点

| 维度 | 旧 README | 新 README |
|------|-----------|-----------|
| 定位 | 英语启蒙 | 多学科启蒙(英语/语文/数学/课外) |
| 课时数 | 10 课时 | 英语 60 课 + 其他学科 |
| 课型 | WORD/SENTENCE | 7 种(WORD/SENTENCE/READING/QUIZ/CALCULATE/PHONICS/DIALOGUE) |
| API 组 | 5 组 | 8 组(新增 subjects/wrong-answers/learned) |
| 数据库表 | 5 张 | 7 张(新增 subject/wrong_answer) |
| 迁移版本 | V1~V4 | V1~V18 |
| 功能章节 | 无 | 新增"功能特性"含学科矩阵与课型表 |
| 架构章节 | 3 小节 | 7 小节(新增课时模板路由/图片托管/令牌系统) |

## 3. 复审维度框架

### 文档复审(核对 README 与代码一致性)

- 准确性：API 路径/方法/参数与 `@RequestMapping` 一致；表/字段与 entity + migration 一致；项目结构与实际目录一致
- 完整性：新功能(subject/wrong-answer/learned/7 课时模板/图片托管)是否全覆盖
- 示例可运行性：curl 示例路径与参数是否有效
- 结构合理性：章节有无重复或遗漏

### 代码复审(针对 7-20 后改动)

- 分层一致性：controller 是否混入业务逻辑；service 是否抛业务异常；DTO 是否与实体分离
- 坏味道：过长方法/类(>200 行)；硬编码色值/路径；重复代码
- 测试覆盖：subject/wrong-answer/learned/课时模板是否有测试
- 迁移规范性：V1~V18 命名与不可改规则；REPLACE 与 json_set 使用
- 边界清晰度：错题本与已学课程是否合理复用 user_progress；WordImageResolver 职责是否单一

## 4. 复审发现

### 文档复审发现

| 编号 | 优先级 | 状态 | 问题 | 证据 |
|------|--------|------|------|------|
| D1 | P0 | 已修复 | README 停留 Phase 2，未反映多学科/错题本/已学课程/6 课时模板/V18 | 30+ 提交未反映 |
| D2 | P0 | 已修复 | API 文档缺少 subjects/wrong-answers/learned 端点 | SubjectController/WrongAnswerController/ProgressController 新增端点 |
| D3 | P0 | 已修复 | 数据库表从 5 张变 7 张未反映 | V11 subject、V17 wrong_answer |
| D4 | P1 | 已修复 | 迁移版本 V1~V4 未更新到 V18 | db/migration 目录有 V1~V18 |
| D5 | P2 | 已修复 | frontend/README.md 仍是 Vite 默认模板 | 原 3 行内容 |

### 代码复审发现

#### C1 [P1] LessonService 抛 RuntimeException 违反业务异常规范

**问题**：`LessonService.getLessonById` 在课时不存在时抛 `new RuntimeException("Lesson not found: " + id)`。

**证据**：`backend/src/main/java/com/englishapp/service/LessonService.java` 第 47 行。

**规范冲突**：项目规范要求"Service 层抛出业务异常，不返回 null"、"业务异常继承 RuntimeException"、"异常码定义在枚举类中"，且有 `GlobalExceptionHandler` 统一捕获。

**改进建议**：定义 `BusinessException extends RuntimeException`，含 `ResultCode` 枚举(如 `LESSON_NOT_FOUND`)，由 `GlobalExceptionHandler` 捕获并返回对应 HTTP 状态码与业务码。`LessonService` 改为 `throw new BusinessException(ResultCode.LESSON_NOT_FOUND, id)`。

#### C2 [P1] WrongAnswerService 与 LearnedLesson 缺少单元测试

**问题**：错题本与已学课程是 7-21 新增的核心功能，但无对应测试类。

**证据**：`backend/src/test/` 下无 `WrongAnswerServiceImplTest`，`ProgressServiceImplTest` 未覆盖 `getLearnedLessons`/`getLearnedStats`。现有 11 个测试类主要集中在 Theme/Subject/Unit/LessonService/WordImageResolver。

**改进建议**：新增 `WrongAnswerServiceImplTest`，覆盖记录(新建/累加 wrong_count)、查询(全部/按 resolved 筛选)、统计、标记已掌握、删除；扩展 `ProgressServiceImplTest` 覆盖 learned 列表与统计的学科/课型分类逻辑。

#### C3 [P2] LessonType 枚举含未使用的 GAME/SPEAK

**问题**：`LessonType` 定义了 9 个值，但 `GAME`/`SPEAK` 标注"保留，暂未使用"，前端 `LessonView` 分发器也未处理这两种类型。

**证据**：`domain/enums/LessonType.java` 第 24-26 行；`LessonView.vue` 的 `lessonTemplate` computed 无 GAME/SPEAK 分支。

**改进建议**：遵循 YAGNI，移除 `GAME`/`SPEAK`。若后续确有需求，再通过新增迁移与枚举值引入。移除可避免前端遇到未支持类型时的占位提示。

#### C4 [P2] LessonView 残留 wordMetaMap 硬编码映射

**问题**：`LessonView.vue` 保留了 `wordMetaMap`(15 个单词的 emoji/phonetic/translation 硬编码)，但后端 `lesson.content` 已包含这些字段(V18 迁移的 items 均含 emoji/phonetic/translation/image)。

**证据**：`LessonView.vue` 第 151-167 行 `wordMetaMap`；`normalizeContent` 函数对 `raw.words`/`raw.sentences` 旧格式做兜底填充。

**改进建议**：确认数据库中所有课时 content 均为新格式(含 items 数组且字段完整)后，移除 `wordMetaMap` 与 `normalizeContent` 的旧格式兜底分支，直接使用 `raw.items`。这能消除前端硬编码与后端数据的重复，避免数据不一致。

#### C5 [P2] V18 迁移 Part 2 用 REPLACE 替换 content

**问题**：V18 迁移 Part 2(Lesson 35/36/37)用整体 `UPDATE ... SET content = '{...}'` 替换 JSON，而非 `json_set` 精确更新。

**证据**：`V18__expand_english_lessons.sql` 第 19-25 行。

**风险**：项目 lessons learned 记录"REPLACE 可能因 emoji Unicode 转义失败"，V10 已改用 `json_set` 修复 V9 的问题。V18 Part 2 的整体替换虽已成功落地，但与既定经验不一致，后续迁移若遇 emoji 密集内容可能复发。

**改进建议**：后续迁移一律使用 `json_set` 做字段级更新；若需整体替换，先在测试库验证 emoji 编码。V18 已落地不可改，仅作为经验记录。

#### C6 [P2] WrongAnswer.lessonName 冗余字段一致性

**问题**：`wrong_answer` 表冗余存储 `lesson_name`，便于错题列表直接展示，但若 `lesson.name` 后续变更，错题记录不会同步。

**证据**：`domain/WrongAnswer.java` 第 41-42 行 `lessonName` 字段注释"冗余字段,便于错题列表直接展示,避免额外关联查询"；`LessonView.vue` 上报时传 `lesson.value?.name`。

**改进建议**：当前设计为性能取舍，可接受。但应在错题列表查询时标注"名称为答错时快照"，或在 lesson 重命名时考虑同步更新错题记录。属可接受的技术债，记录即可。

## 5. 交付物清单

1. `README.md` — 全量重写，反映 Phase 2.5 多学科状态(已更新)
2. `frontend/README.md` — 替换 Vite 默认模板为前端开发说明(已更新)
3. `docs/superpowers/specs/2026-07-21-readme-update-and-review-design.md` — 本文档(含复审发现)

## 6. 验证标准

- [x] README 中所有 API 路径与 controller `@RequestMapping` 注解一致
- [x] README 中所有表/字段与 entity + migration 一致
- [x] curl 示例路径可实际运行
- [x] 复审发现每条都有明确改进建议和优先级
- [x] 项目结构描述与实际目录一致(7 controller / 7 view / lesson-templates)
- [x] 迁移版本 V1~V18 与 db/migration 目录一致
