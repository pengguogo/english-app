# 多学科重构设计文档

> 日期：2026-07-21
> 状态：待审阅
> 范围：将英语单学科APP重构为多学科学习平台（英语+语文+数学+课外火车）

## 一、背景与目标

### 1.1 当前状态

项目为英语学习APP，采用 Theme → Unit → Lesson 三级结构：
- 4个主题 / 11个单元 / 36个课时
- 后端：Java 17 + Spring Boot 3.2 + JPA + Flyway + SQLite
- 前端：Vue 3 + Vite + Pinia + Vue Router (history)
- 课时类型：WORD（单词课）、SENTENCE（句型课），其他枚举值未使用
- 学习模式：听音 → 跟读 → 评分（百度TTS + ASR降级评分）
- 固定用户 user_id=1，无登录系统

### 1.2 重构目标

将单学科英语APP扩展为多学科学习平台：
- **英语**：保留现有内容，迁移到新架构
- **语文**：拼音学习、汉字识读、古诗词朗诵、寓言故事阅读
- **数学**：数字认知、加减运算、图形认知、趣味应用题
- **课外**：火车模块（组件认知、动力原理、火车类型、历史发展）

### 1.3 设计原则

1. **最小改动复用现有架构**：语音评分、图片解析等通用能力直接复用
2. **五种课型覆盖所有学科**：前端按type分发，新增学科零前端改动
3. **分阶段实施**：每阶段可独立验证和回滚，英语进度无缝衔接
4. **AI配图统一风格**：所有配图由AI生成，扁平2.5D儿童友好风格

## 二、数据库架构设计

### 2.1 新增 Subject 表

```sql
CREATE TABLE subject (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(50) NOT NULL,        -- 学科名称：英语/语文/数学/课外
    code VARCHAR(20) NOT NULL,        -- 学科代码：ENGLISH/CHINESE/MATH/EXTRACURRICULAR
    icon_url VARCHAR(255),            -- 学科图标
    color VARCHAR(20),                -- 学科主题色（前端卡片）
    sort_order INTEGER DEFAULT 0,
    is_locked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

学科种子数据：

| id | name | code | color | sort_order |
|----|------|------|-------|-----------|
| 1 | 英语 | ENGLISH | #6B7CFF | 1 |
| 2 | 语文 | CHINESE | #FF7043 | 2 |
| 3 | 数学 | MATH | #26A69A | 3 |
| 4 | 课外 | EXTRACURRICULAR | #7E57C2 | 4 |

### 2.2 Theme 表扩展

`theme` 表新增 `subject_id` 外键列，指向 `subject.id`。现有4个英语主题回填 `subject_id = 1`。

```sql
ALTER TABLE theme ADD COLUMN subject_id INTEGER;
UPDATE theme SET subject_id = 1 WHERE subject_id IS NULL;
```

### 2.3 课时类型设计

LessonType 枚举扩展为5种活跃类型 + 2种保留类型：

| 类型 | 交互模式 | 适用场景 | content结构 |
|------|---------|---------|------------|
| `WORD` | 看图认字→听音→跟读评分 | 英语单词、语文汉字/拼音、数学数字 | `{items:[{word,phonetic,translation,image}]}` |
| `SENTENCE` | 听音→跟读朗诵评分 | 英语句型、语文古诗 | `{items:[{sentence,translation,image}]}` |
| `READING` | 图文展示→翻页阅读 | 语文故事、课外火车科普 | `{items:[{title,content,image}]}` |
| `QUIZ` | 出题→选答案→判对错 | 数学图形/应用题、课外互动问答 | `{items:[{question,options[],optionsImages[],answer,image}]}` |
| `CALCULATE` | 出题→输入答案→判对错 | 数学加减运算 | `{items:[{question,answer,image}]}` |
| `GAME` | 保留，暂未使用 | - | - |
| `SPEAK` | 保留，暂未使用 | - | - |

### 2.4 各学科课时类型映射

| 学科 | 可用课时类型 | 说明 |
|------|------------|------|
| 英语 | WORD + SENTENCE | 现有模式不变，直接迁移 |
| 语文 | WORD + SENTENCE + READING | 汉字/拼音用WORD，古诗用SENTENCE，故事用READING |
| 数学 | WORD + CALCULATE + QUIZ | 数字用WORD，运算用CALCULATE，图形/应用题用QUIZ |
| 课外 | READING + QUIZ | 图文科普用READING，互动问答用QUIZ |

## 三、后端架构改造

### 3.1 实体层

新增 Subject 实体：

```java
/**
 * 学科实体
 * @author TRAE Agent
 * @since 2026-07-21
 */
@Entity
@Table(name = "subject")
public class Subject {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String code;
    private String iconUrl;
    private String color;
    private Integer sortOrder;
    private Boolean isLocked;
    private LocalDateTime createdAt;
}
```

Theme 实体新增字段：

```java
@Entity
@Table(name = "theme")
public class Theme {
    // 现有字段保留
    private Integer subjectId;  // 新增外键，指向 subject.id
}
```

### 3.2 Service 层

新增 SubjectService 接口及实现：

```java
public interface SubjectService {
    /** 获取所有学科（按 sortOrder 排序） */
    List<SubjectDto> getAllSubjects();
}
```

ThemeService 扩展：新增 `getThemesBySubjectId(subjectId)` 方法，原 `getAllThemes()` 保留兼容。

### 3.3 Controller 层

新增 SubjectController：

| 方法 | 路径 | 功能 |
|------|------|------|
| GET | `/api/v1/subjects` | 获取所有学科 |
| GET | `/api/v1/subjects/{subjectId}/themes` | 获取某学科下所有主题 |

现有 `/api/v1/themes` 标记 `@Deprecated`，保持向后兼容。

### 3.4 WordImageResolver 扩展

支持多子目录前缀。content 中 image 字段支持带前缀的key：
- `car` → `/images/words/car.jpg`（英语，默认words目录）
- `train/steam-locomotive` → `/images/train/steam-locomotive.jpg`
- `chinese/pinyin-a` → `/images/chinese/pinyin-a.jpg`

### 3.5 现有代码改动清单

| 模块 | 改动 | 影响 |
|------|------|------|
| Subject | 新增实体+Repository+Service+Controller | 全新模块 |
| Theme | 实体加 subjectId 字段 | 需迁移现有4个主题 |
| ThemeService | 新增按学科查询 | 现有方法保留 |
| LessonType | 枚举加 READING/QUIZ/CALCULATE | 无破坏性 |
| VoiceService | 无改动 | 已支持中文TTS+ASR评分 |
| WordImageResolver | 扩展多子目录支持 | 兼容现有单级key |

## 四、前端架构改造

### 4.1 路由结构

| 路径 | name | 组件 | 变化 |
|------|------|------|------|
| `/` | `home` | `HomeView.vue` | 改为学科选择页 |
| `/subject/:subjectId` | `subject` | `SubjectView.vue` | 新增：学科主题列表 |
| `/theme/:themeId` | `theme` | `ThemeView.vue` | 不变 |
| `/unit/:unitId` | `unit` | `UnitView.vue` | 不变 |
| `/lesson/:lessonId` | `lesson` | `LessonView.vue` | 改为按type分发 |

### 4.2 页面组件

**HomeView.vue（学科选择页）**：四大学科卡片网格，每卡显示学科图标、名称、已获得星星总数，点击跳转 `/subject/:subjectId`。

**SubjectView.vue（新增）**：展示某学科下所有主题，逻辑同原 HomeView，显示主题卡片和进度。

**LessonView.vue（重构为分发器）**：根据 `lesson.type` 分发到对应模板组件：

```vue
<template>
  <div class="lesson-container">
    <component
      :is="lessonTemplate"
      :lesson="lesson"
      @complete="handleComplete"
    />
  </div>
</template>
```

### 4.3 课时模板组件

放在 `src/components/lesson-templates/` 下：

| 组件 | 类型 | 交互流程 | 复用现有组件 |
|------|------|---------|------------|
| `WordLesson.vue` | WORD | 看图→听音→跟读→评分 | AudioButton + RecordButton + StarBar |
| `SentenceLesson.vue` | SENTENCE | 听音→跟读朗诵→评分 | AudioButton + RecordButton + StarBar |
| `ReadingLesson.vue` | READING | 图文翻页阅读→读完后结算 | AudioButton（朗读全文） |
| `QuizLesson.vue` | QUIZ | 出题→选选项→即时判对错→结算 | StarBar |
| `CalculateLesson.vue` | CALCULATE | 出题→输入答案→判对错→结算 | StarBar |

### 4.4 设计令牌扩展

`tokens.css` 新增学科主题色：

```css
--subject-english: var(--color-primary);    /* #6B7CFF 蓝紫 */
--subject-chinese: #FF7043;                 /* 朱砂红 */
--subject-math: #26A69A;                    /* 青绿 */
--subject-extracurricular: #7E57C2;         /* 紫罗兰 */
```

### 4.5 前端改动清单

| 模块 | 动作 | 说明 |
|------|------|------|
| HomeView.vue | 重写 | 主题列表→学科选择页 |
| SubjectView.vue | 新增 | 学科下的主题列表 |
| LessonView.vue | 重构 | 硬编码流程→type分发器 |
| WordLesson.vue | 新增 | 提取自现有LessonView逻辑 |
| SentenceLesson.vue | 新增 | 提取自现有LessonView逻辑 |
| ReadingLesson.vue | 新增 | 图文翻页阅读模板 |
| QuizLesson.vue | 新增 | 选择题模板 |
| CalculateLesson.vue | 新增 | 计算题模板 |
| themeConfig.js | 扩展 | 按学科分组配置 |
| tokens.css | 扩展 | 4个学科主题色 |
| api/subject.js | 新增 | 学科API调用 |
| router/index.js | 修改 | 新增2个路由 |

## 五、内容规划

### 5.1 英语（迁移现有）

| 主题 | 单元 | 课时数 | 类型 |
|------|------|--------|------|
| 水果乐园 | 3单元 | 10课时 | WORD+SENTENCE |
| 交通工具乐园 | 3单元 | 10课时 | WORD+SENTENCE |
| 汪汪队立大功 | 3单元 | 10课时 | WORD+SENTENCE |
| 小砾工程队 | 2单元 | 6课时 | WORD+SENTENCE |

### 5.2 语文（新增）

| 主题 | 单元 | 课时数 | 类型 | 内容方向 |
|------|------|--------|------|---------|
| 拼音启蒙 | 3单元 | 9课时 | WORD | 声母/韵母/整体认读音节 |
| 汉字识读 | 3单元 | 9课时 | WORD | 常用汉字（看图识字） |
| 古诗朗诵 | 2单元 | 6课时 | SENTENCE | 《静夜思》《春晓》《咏鹅》等 |
| 寓言故事 | 2单元 | 6课时 | READING | 《龟兔赛跑》《拔苗助长》等 |

### 5.3 数学（新增）

| 主题 | 单元 | 课时数 | 类型 | 内容方向 |
|------|------|--------|------|---------|
| 数字认知 | 2单元 | 6课时 | WORD | 0-20数字、数数、比大小 |
| 加减运算 | 3单元 | 9课时 | CALCULATE | 10以内/20以内加减法 |
| 图形认知 | 2单元 | 6课时 | QUIZ | 平面图形、立体图形分类 |
| 趣味应用题 | 2单元 | 6课时 | QUIZ | 图文应用题 |

### 5.4 课外火车（新增）

| 主题 | 单元 | 课时数 | 类型 | 内容方向 |
|------|------|--------|------|---------|
| 火车大家族 | 2单元 | 6课时 | READING | 客车/货车/高铁/地铁/磁悬浮 |
| 火车的零件 | 2单元 | 6课时 | READING+QUIZ | 车头/车厢/车轮/车钩/排障器 |
| 火车怎么跑 | 2单元 | 6课时 | READING+QUIZ | 蒸汽/柴油/电力三种动力原理 |
| 火车的时间旅行 | 2单元 | 6课时 | READING | 从蒸汽到高铁的演变历史 |

## 六、图片方案

### 6.1 核心原则

content JSON 中的 `emoji` 字段统一替换为 `image` 字段，所有配图由AI生成。

### 6.2 图片存放结构

```
backend/src/main/resources/static/images/
├── words/          # 英语单词配图（现有，48张）
├── chinese/        # 语文：汉字/拼音/古诗场景
├── math/           # 数学：数字/图形/应用题场景
├── train/          # 课外火车：组件/动力/类型/历史
└── story/          # 故事阅读：场景插图
```

### 6.3 图片风格规范

- **风格**：扁平2.5D、圆润、暖色调、儿童友好
- **背景**：浅色纯色背景（白/浅黄），无复杂场景干扰
- **尺寸**：正方形 1024x1024，前端CSS自适应裁切
- **命名**：英文kebab-case，如 `steam-locomotive`、`pinyin-a`

### 6.4 图片生成批次

| 批次 | 范围 | 预估数量 | 优先级 |
|------|------|---------|--------|
| 第1批 | 英语补全缺失配图 | ~20张 | 高 |
| 第2批 | 课外火车全部配图 | ~30张 | 高 |
| 第3批 | 语文配图（拼音/汉字/古诗/故事） | ~40张 | 中 |
| 第4批 | 数学配图（数字/图形/应用题） | ~25张 | 中 |

总计约115张AI生成图片，用 GenerateImage 工具分批生成。

## 七、实施路径

### 7.1 阶段划分

| 阶段 | 内容 | 依赖 |
|------|------|------|
| 阶段1 | 架构骨架：Subject表+后端API+前端路由 | 无 |
| 阶段2 | 英语迁移：现有内容迁入新架构 | 阶段1 |
| 阶段3 | 课外火车模块（含图片生成） | 阶段1 |
| 阶段4 | 语文模块（含图片生成） | 阶段1 |
| 阶段5 | 数学模块（含图片生成） | 阶段1 |

### 7.2 Flyway 迁移文件规划

```
V11__add_subject_table.sql       — 新建 subject 表 + 4 条学科数据
V12__add_theme_subject_id.sql    — theme 表加 subject_id 列 + 回填现有主题
V13__seed_chinese_content.sql    — 语文课程内容
V14__seed_math_content.sql       — 数学课程内容
V15__seed_train_content.sql      — 课外火车内容
```

### 7.3 迁移安全策略

**数据库安全**：
- 所有迁移通过 Flyway 新增 V11-V15，不修改已提交文件
- V12 回填 subject_id 时先 SELECT 确认现有主题数，再 UPDATE
- 每个阶段迁移后立即跑后端单测验证

**向后兼容**：
- 现有 `/api/v1/themes` 接口保留，前端切换到新接口后再移除
- 现有 `user_progress` 数据零改动，`lesson_id` 不变
- 英语课程的 `lesson.id` 保持不变，用户学习进度无缝衔接

**回滚方案**：
- 每个阶段独立提交 git，出问题可 `git revert` 单阶段
- Flyway 迁移文件按阶段拆分，可精确回滚
- 前端改动在独立分支 `feature/multi-subject`

### 7.4 测试策略

| 层级 | 测试内容 | 工具 |
|------|---------|------|
| 后端单元测试 | SubjectService、扩展后的ThemeService | JUnit5 + Mockito |
| 后端集成测试 | 新增API端点、Flyway迁移正确性 | @SpringBootTest |
| 前端手动测试 | 每阶段验证点逐项确认 | 浏览器 |
| 数据完整性 | 迁移后数据量校验、图片URL可达性 | SQL查询 + HTTP检查 |

### 7.5 最终交付物

重构完成后将拥有：
- **4个学科**：英语/语文/数学/课外，学科首页可切换
- **117+课时**：英语36 + 语文30 + 数学27 + 课外24
- **5种课型**：WORD/SENTENCE/READING/QUIZ/CALCULATE
- **115+张AI配图**：统一扁平2.5D风格
- **完整学习闭环**：每学科都有从认知到练习到反馈的流程

## 八、设计理念总结

1. **四级层级** Subject → Theme → Unit → Lesson，学科独立互不干扰
2. **五种课型**覆盖所有学科需求，前端按type分发，新增学科零前端改动
3. **AI配图**统一替换emoji，扁平2.5D儿童友好风格
4. **分5阶段实施**，每阶段可独立验证和回滚，英语进度无缝衔接
