# 多学科重构 - 阶段1：架构骨架 实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 搭建 Subject → Theme → Unit → Lesson 四级层级架构骨架，新增学科表、学科API、前端学科选择页，英语现有内容无缝迁移到新架构。

**Architecture:** 后端新增 Subject 实体全栈（domain/repository/service/controller/dto），Theme 实体扩展 subjectId 外键，LessonType 枚举扩展3种新类型，WordImageResolver 支持多子目录。前端新增学科选择首页和 SubjectView 页面，路由调整为五级导航。

**Tech Stack:** Java 17 + Spring Boot 3.2 + JPA + Flyway + SQLite / Vue 3 + Vite + Pinia + Vue Router

---

## 文件结构

### 后端新增文件
- `backend/src/main/java/com/englishapp/domain/Subject.java` — 学科实体
- `backend/src/main/java/com/englishapp/repository/SubjectRepository.java` — 学科 Repository
- `backend/src/main/java/com/englishapp/dto/SubjectDto.java` — 学科 DTO
- `backend/src/main/java/com/englishapp/service/SubjectService.java` — 学科服务接口
- `backend/src/main/java/com/englishapp/service/SubjectServiceImpl.java` — 学科服务实现（暂无接口复杂逻辑，但遵循项目规范：接口+Impl）
- `backend/src/main/java/com/englishapp/controller/SubjectController.java` — 学科控制器
- `backend/src/main/resources/db/migration/V11__add_subject_table.sql` — 学科表+种子数据
- `backend/src/main/resources/db/migration/V12__add_theme_subject_id.sql` — theme表加subject_id
- `backend/src/test/java/com/englishapp/service/SubjectServiceImplTest.java` — 学科服务单测
- `backend/src/test/java/com/englishapp/controller/SubjectControllerTest.java` — 学科控制器单测
- `backend/src/test/java/com/englishapp/service/WordImageResolverMultiDirTest.java` — 多子目录解析单测

### 后端修改文件
- `backend/src/main/java/com/englishapp/domain/Theme.java` — 加 subjectId 字段
- `backend/src/main/java/com/englishapp/dto/ThemeDto.java` — 加 subjectId 字段
- `backend/src/main/java/com/englishapp/repository/ThemeRepository.java` — 加按学科查询方法
- `backend/src/main/java/com/englishapp/service/ThemeService.java` — 加 getThemesBySubjectId 方法
- `backend/src/main/java/com/englishapp/controller/ThemeController.java` — 加按学科查询端点
- `backend/src/main/java/com/englishapp/domain/enums/LessonType.java` — 加 READING/QUIZ/CALCULATE
- `backend/src/main/java/com/englishapp/service/WordImageResolver.java` — resolveUrl 支持多子目录

### 前端新增文件
- `frontend/src/api/subject.js` — 学科 API 调用
- `frontend/src/views/SubjectView.vue` — 学科主题列表页

### 前端修改文件
- `frontend/src/styles/tokens.css` — 加4个学科主题色
- `frontend/src/router/index.js` — 新增 subject 路由
- `frontend/src/views/HomeView.vue` — 重写为学科选择页

---

## Task 1: Flyway 迁移 - subject 表 + theme 表扩展

**Files:**
- Create: `backend/src/main/resources/db/migration/V11__add_subject_table.sql`
- Create: `backend/src/main/resources/db/migration/V12__add_theme_subject_id.sql`

- [ ] **Step 1: 创建 V11 迁移文件 - subject 表 + 种子数据**

创建 `backend/src/main/resources/db/migration/V11__add_subject_table.sql`：

```sql
-- V11: 新建学科表，支持多学科学习平台
-- 学科是最高层级，Theme 隶属于 Subject

CREATE TABLE subject (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(20) NOT NULL,
    icon_url VARCHAR(255),
    color VARCHAR(20),
    sort_order INTEGER DEFAULT 0,
    is_locked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 插入4个学科种子数据
INSERT INTO subject (id, name, code, icon_url, color, sort_order, is_locked) VALUES
(1, '英语', 'ENGLISH', NULL, '#6B7CFF', 1, FALSE),
(2, '语文', 'CHINESE', NULL, '#FF7043', 2, FALSE),
(3, '数学', 'MATH', NULL, '#26A69A', 3, FALSE),
(4, '课外', 'EXTRACURRICULAR', NULL, '#7E57C2', 4, FALSE);
```

- [ ] **Step 2: 创建 V12 迁移文件 - theme 表加 subject_id**

创建 `backend/src/main/resources/db/migration/V12__add_theme_subject_id.sql`：

```sql
-- V12: theme 表新增 subject_id 外键列
-- 现有4个英语主题全部回填 subject_id = 1（英语）

ALTER TABLE theme ADD COLUMN subject_id INTEGER;

-- 回填现有主题的 subject_id 为 1（英语）
UPDATE theme SET subject_id = 1 WHERE subject_id IS NULL;
```

- [ ] **Step 3: 启动后端验证迁移成功**

Run:
```bash
cd backend && export JAVA_HOME=/Users/dawn/Library/Java/JavaVirtualMachines/corretto-17.0.13/Contents/Home && export PATH=$JAVA_HOME/bin:$PATH && ./run.sh
```
Expected: 应用启动无报错，Flyway 输出 `Successfully applied 2 migrations`。Ctrl+C 停止。

- [ ] **Step 4: 验证数据库表结构**

Run:
```bash
cd backend && sqlite3 english.db "SELECT * FROM subject;"
```
Expected: 输出4行学科数据。

Run:
```bash
cd backend && sqlite3 english.db "SELECT id, name, subject_id FROM theme;"
```
Expected: 所有主题的 subject_id 均为 1。

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/resources/db/migration/V11__add_subject_table.sql backend/src/main/resources/db/migration/V12__add_theme_subject_id.sql
git commit -m "feat(db): 新增subject表和theme表subject_id外键(V11-V12)

Co-Authored-By: TRAE Agent <noreply@trae.ai>"
```

---

## Task 2: Subject 实体 + Repository

**Files:**
- Create: `backend/src/main/java/com/englishapp/domain/Subject.java`
- Create: `backend/src/main/java/com/englishapp/repository/SubjectRepository.java`

- [ ] **Step 1: 创建 Subject 实体类**

创建 `backend/src/main/java/com/englishapp/domain/Subject.java`：

```java
package com.englishapp.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * 学科实体
 * <p>
 * 多学科学习平台的最高层级，Theme 隶属于 Subject。
 * 目前包含英语、语文、数学、课外四个学科。
 * </p>
 *
 * @author TRAE Agent
 * @since 2026-07-21
 */
@Entity
@Table(name = "subject")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** 学科名称：英语/语文/数学/课外 */
    private String name;

    /** 学科代码：ENGLISH/CHINESE/MATH/EXTRACURRICULAR */
    private String code;

    /** 学科图标 URL */
    private String iconUrl;

    /** 学科主题色（前端卡片配色，如 #6B7CFF） */
    private String color;

    /** 排序序号 */
    private Integer sortOrder;

    /** 是否锁定 */
    private Boolean isLocked;

    /** 创建时间 */
    private LocalDateTime createdAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Boolean getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(Boolean isLocked) {
        this.isLocked = isLocked;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
```

- [ ] **Step 2: 创建 SubjectRepository 接口**

创建 `backend/src/main/java/com/englishapp/repository/SubjectRepository.java`：

```java
package com.englishapp.repository;

import com.englishapp.domain.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 学科数据访问接口
 *
 * @author TRAE Agent
 * @since 2026-07-21
 */
@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> {

    /**
     * 按排序序号升序获取所有学科
     * @return 排序后的学科列表
     */
    List<Subject> findAllByOrderBySortOrderAsc();
}
```

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/java/com/englishapp/domain/Subject.java backend/src/main/java/com/englishapp/repository/SubjectRepository.java
git commit -m "feat(domain): 新增Subject实体和Repository

Co-Authored-By: TRAE Agent <noreply@trae.ai>"
```

---

## Task 3: SubjectDto + SubjectService + SubjectController

**Files:**
- Create: `backend/src/main/java/com/englishapp/dto/SubjectDto.java`
- Create: `backend/src/main/java/com/englishapp/service/SubjectService.java`
- Create: `backend/src/main/java/com/englishapp/service/SubjectServiceImpl.java`
- Create: `backend/src/main/java/com/englishapp/controller/SubjectController.java`

- [ ] **Step 1: 创建 SubjectDto**

创建 `backend/src/main/java/com/englishapp/dto/SubjectDto.java`：

```java
package com.englishapp.dto;

/**
 * 学科数据传输对象
 *
 * @param id 学科 ID
 * @param name 学科名称
 * @param code 学科代码
 * @param iconUrl 学科图标 URL
 * @param color 学科主题色
 * @param sortOrder 排序序号
 * @param isLocked 是否锁定
 * @author TRAE Agent
 * @since 2026-07-21
 */
public record SubjectDto(
    Integer id,
    String name,
    String code,
    String iconUrl,
    String color,
    Integer sortOrder,
    Boolean isLocked
) {}
```

- [ ] **Step 2: 创建 SubjectService 接口**

创建 `backend/src/main/java/com/englishapp/service/SubjectService.java`：

```java
package com.englishapp.service;

import com.englishapp.dto.SubjectDto;

import java.util.List;

/**
 * 学科服务接口
 *
 * @author TRAE Agent
 * @since 2026-07-21
 */
public interface SubjectService {

    /**
     * 获取所有学科（按 sortOrder 升序）
     * @return 学科 DTO 列表
     */
    List<SubjectDto> getAllSubjects();
}
```

- [ ] **Step 3: 创建 SubjectServiceImpl 实现**

创建 `backend/src/main/java/com/englishapp/service/SubjectServiceImpl.java`：

```java
package com.englishapp.service;

import com.englishapp.domain.Subject;
import com.englishapp.dto.SubjectDto;
import com.englishapp.repository.SubjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 学科服务实现
 *
 * @author TRAE Agent
 * @since 2026-07-21
 */
@Service
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;

    /**
     * 构造学科服务
     * @param subjectRepository 学科数据访问接口
     */
    public SubjectServiceImpl(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    @Override
    public List<SubjectDto> getAllSubjects() {
        return subjectRepository.findAllByOrderBySortOrderAsc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * 实体转 DTO
     * @param subject 学科实体
     * @return 学科 DTO
     */
    private SubjectDto toDto(Subject subject) {
        return new SubjectDto(
                subject.getId(),
                subject.getName(),
                subject.getCode(),
                subject.getIconUrl(),
                subject.getColor(),
                subject.getSortOrder(),
                subject.getIsLocked()
        );
    }
}
```

- [ ] **Step 4: 创建 SubjectController**

创建 `backend/src/main/java/com/englishapp/controller/SubjectController.java`：

```java
package com.englishapp.controller;

import com.englishapp.common.Result;
import com.englishapp.dto.SubjectDto;
import com.englishapp.service.SubjectService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 学科 REST 控制器
 * <p>
 * 提供学科列表查询接口，是多学科学习平台的入口。
 * </p>
 *
 * @author TRAE Agent
 * @since 2026-07-21
 */
@RestController
@RequestMapping("/api/v1/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    /**
     * 构造学科控制器
     * @param subjectService 学科业务接口
     */
    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    /**
     * 获取所有学科列表
     * @return 统一响应，包含学科 DTO 列表
     */
    @GetMapping
    public Result<List<SubjectDto>> getAllSubjects() {
        return Result.success(subjectService.getAllSubjects());
    }
}
```

- [ ] **Step 5: 启动后端验证 API**

Run:
```bash
cd backend && export JAVA_HOME=/Users/dawn/Library/Java/JavaVirtualMachines/corretto-17.0.13/Contents/Home && export PATH=$JAVA_HOME/bin:$PATH && ./run.sh
```

在另一个终端验证：
```bash
curl -s http://localhost:8080/api/v1/subjects | python3 -m json.tool
```
Expected: 返回包含4个学科的 JSON 数组。Ctrl+C 停止后端。

- [ ] **Step 6: Commit**

```bash
git add backend/src/main/java/com/englishapp/dto/SubjectDto.java backend/src/main/java/com/englishapp/service/SubjectService.java backend/src/main/java/com/englishapp/service/SubjectServiceImpl.java backend/src/main/java/com/englishapp/controller/SubjectController.java
git commit -m "feat(subject): 新增Subject全栈(DTO+Service+Controller)

Co-Authored-By: TRAE Agent <noreply@trae.ai>"
```

---

## Task 4: Theme 扩展 subjectId + 按学科查询

**Files:**
- Modify: `backend/src/main/java/com/englishapp/domain/Theme.java`
- Modify: `backend/src/main/java/com/englishapp/dto/ThemeDto.java`
- Modify: `backend/src/main/java/com/englishapp/repository/ThemeRepository.java`
- Modify: `backend/src/main/java/com/englishapp/service/ThemeService.java`
- Modify: `backend/src/main/java/com/englishapp/controller/ThemeController.java`

- [ ] **Step 1: Theme 实体加 subjectId 字段**

在 `Theme.java` 的 `isLocked` 字段后新增：

```java
    /** 所属学科 ID（外键 → subject.id） */
    private Integer subjectId;
```

在 `setIsLocked` 方法后新增 getter/setter：

```java
    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }
```

- [ ] **Step 2: ThemeDto 加 subjectId 字段**

修改 `ThemeDto.java` record 定义：

```java
public record ThemeDto(
    Integer id,
    String name,
    String iconUrl,
    Integer sortOrder,
    Boolean isLocked,
    Integer subjectId
) {}
```

- [ ] **Step 3: ThemeRepository 加按学科查询方法**

在 `ThemeRepository.java` 接口中新增方法：

```java
    /**
     * 按学科 ID 查询主题列表（按排序序号升序）
     * @param subjectId 学科 ID
     * @return 该学科下的主题列表
     */
    List<Theme> findBySubjectIdOrderBySortOrderAsc(Integer subjectId);
```

- [ ] **Step 4: ThemeService 扩展 getThemesBySubjectId 方法**

在 `ThemeService.java` 中新增构造器注入 ThemeRepository（已有）并新增方法。完整修改后的 `ThemeService.java`：

```java
package com.englishapp.service;

import com.englishapp.domain.Theme;
import com.englishapp.dto.ThemeDto;
import com.englishapp.repository.ThemeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 主题服务
 * <p>
 * 提供主题查询能力，支持按学科筛选或获取全部主题。
 * </p>
 *
 * @author englishapp
 * @since 1.0.0
 */
@Service
public class ThemeService {

    private final ThemeRepository themeRepository;

    /**
     * 构造主题服务
     * @param themeRepository 主题数据访问接口
     */
    public ThemeService(ThemeRepository themeRepository) {
        this.themeRepository = themeRepository;
    }

    /**
     * 获取所有主题列表（兼容旧接口，已标记废弃）
     * @return 主题 DTO 列表
     */
    @Deprecated
    public List<ThemeDto> getAllThemes() {
        return themeRepository.findAllByOrderBySortOrderAsc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * 按学科 ID 获取主题列表
     * @param subjectId 学科 ID
     * @return 该学科下的主题 DTO 列表
     */
    public List<ThemeDto> getThemesBySubjectId(Integer subjectId) {
        return themeRepository.findBySubjectIdOrderBySortOrderAsc(subjectId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * 实体转 DTO
     * @param theme 主题实体
     * @return 主题 DTO
     */
    private ThemeDto toDto(Theme theme) {
        return new ThemeDto(
                theme.getId(),
                theme.getName(),
                theme.getIconUrl(),
                theme.getSortOrder(),
                theme.getIsLocked(),
                theme.getSubjectId()
        );
    }
}
```

- [ ] **Step 5: ThemeController 新增按学科查询端点**

修改 `ThemeController.java`，在现有 `getAllThemes` 方法后新增按学科查询端点。完整修改后的 `ThemeController.java`：

```java
package com.englishapp.controller;

import com.englishapp.common.Result;
import com.englishapp.dto.ThemeDto;
import com.englishapp.service.ThemeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 主题 REST 控制器
 * <p>
 * 提供学习主题列表查询接口，支持按学科筛选。
 * 所有响应统一使用 {@link Result} 包装。
 * </p>
 *
 * @author englishapp
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/themes")
public class ThemeController {

    private final ThemeService themeService;

    /**
     * 构造主题控制器
     * @param themeService 主题业务接口
     */
    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    /**
     * 获取所有学习主题列表（兼容旧接口）
     * @return 统一响应，包含主题 DTO 列表
     */
    @Deprecated
    @GetMapping
    public Result<List<ThemeDto>> getAllThemes() {
        return Result.success(themeService.getAllThemes());
    }

    /**
     * 按学科 ID 获取主题列表
     * @param subjectId 学科 ID
     * @return 统一响应，包含该学科下的主题 DTO 列表
     */
    @GetMapping("/subject/{subjectId}")
    public Result<List<ThemeDto>> getThemesBySubject(@PathVariable Integer subjectId) {
        return Result.success(themeService.getThemesBySubjectId(subjectId));
    }
}
```

- [ ] **Step 6: 启动后端验证 API**

Run:
```bash
cd backend && export JAVA_HOME=/Users/dawn/Library/Java/JavaVirtualMachines/corretto-17.0.13/Contents/Home && export PATH=$JAVA_HOME/bin:$PATH && ./run.sh
```

在另一个终端验证：
```bash
curl -s http://localhost:8080/api/v1/themes/subject/1 | python3 -m json.tool
```
Expected: 返回4个英语主题，每个包含 `subjectId: 1`。Ctrl+C 停止。

- [ ] **Step 7: Commit**

```bash
git add backend/src/main/java/com/englishapp/domain/Theme.java backend/src/main/java/com/englishapp/dto/ThemeDto.java backend/src/main/java/com/englishapp/repository/ThemeRepository.java backend/src/main/java/com/englishapp/service/ThemeService.java backend/src/main/java/com/englishapp/controller/ThemeController.java
git commit -m "feat(theme): Theme扩展subjectId字段和按学科查询API

Co-Authored-By: TRAE Agent <noreply@trae.ai>"
```

---

## Task 5: LessonType 枚举扩展 + WordImageResolver 多子目录

**Files:**
- Modify: `backend/src/main/java/com/englishapp/domain/enums/LessonType.java`
- Modify: `backend/src/main/java/com/englishapp/service/WordImageResolver.java`

- [ ] **Step 1: LessonType 枚举新增3种类型**

修改 `LessonType.java`：

```java
package com.englishapp.domain.enums;

/**
 * 课时类型枚举
 * <p>
 * 定义不同的学习交互模式，前端根据类型分发到对应模板组件。
 * </p>
 *
 * @author englishapp
 * @since 1.0.0
 */
public enum LessonType {
    /** 看图认字→听音→跟读评分（英语单词/语文汉字/数学数字） */
    WORD,
    /** 听音→跟读朗诵评分（英语句型/语文古诗） */
    SENTENCE,
    /** 图文翻页阅读（语文故事/课外科普） */
    READING,
    /** 选择题判对错（数学图形/应用题/课外问答） */
    QUIZ,
    /** 计算题判对错（数学加减运算） */
    CALCULATE,
    /** 保留，暂未使用 */
    GAME,
    /** 保留，暂未使用 */
    SPEAK
}
```

- [ ] **Step 2: WordImageResolver 的 resolveUrl 方法支持多子目录**

修改 `WordImageResolver.java` 中的 `resolveUrl` 方法。当 key 包含斜杠（如 `train/steam-locomotive`）时，使用 `/images` 作为前缀，key 直接作为子路径；不含斜杠时保持原有行为。

修改 `resolveUrl` 方法为：

```java
    /**
     * 拼接单个图片的完整 URL。
     * <p>
     * 支持两种 key 格式：
     * <ul>
     *   <li>不带斜杠的 key（如 "car"）：使用默认 pathPrefix，如 /images/words/car.jpg</li>
     *   <li>带子目录前缀的 key（如 "train/steam-locomotive"）：使用 /images 作为前缀，如 /images/train/steam-locomotive.jpg</li>
     * </ul>
     * </p>
     *
     * @param key 图片 key
     * @return 完整 URL
     */
    public String resolveUrl(String key) {
        if (key.contains("/")) {
            // 带子目录前缀的 key，如 "train/steam-locomotive"、"chinese/pinyin-a"
            return properties.getBaseUrl()
                    + "/images"
                    + "/" + key
                    + properties.getExtension();
        }
        // 不带前缀的 key，保持原有行为（默认 words 目录）
        return properties.getBaseUrl()
                + properties.getPathPrefix()
                + "/" + key
                + properties.getExtension();
    }
```

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/java/com/englishapp/domain/enums/LessonType.java backend/src/main/java/com/englishapp/service/WordImageResolver.java
git commit -m "feat(lesson): LessonType扩展READING/QUIZ/CALCULATE + WordImageResolver多子目录

Co-Authored-By: TRAE Agent <noreply@trae.ai>"
```

---

## Task 6: 后端单元测试

**Files:**
- Create: `backend/src/test/java/com/englishapp/service/SubjectServiceImplTest.java`
- Create: `backend/src/test/java/com/englishapp/controller/SubjectControllerTest.java`
- Create: `backend/src/test/java/com/englishapp/service/WordImageResolverMultiDirTest.java`
- Modify: `backend/src/test/java/com/englishapp/service/ThemeServiceTest.java` (如有需要更新)

- [ ] **Step 1: 编写 SubjectServiceImpl 单测**

创建 `backend/src/test/java/com/englishapp/service/SubjectServiceImplTest.java`：

```java
package com.englishapp.service;

import com.englishapp.domain.Subject;
import com.englishapp.dto.SubjectDto;
import com.englishapp.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * 学科服务实现单元测试
 *
 * @author TRAE Agent
 * @since 2026-07-21
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("学科服务测试")
class SubjectServiceImplTest {

    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private SubjectServiceImpl subjectService;

    private Subject englishSubject;
    private Subject chineseSubject;

    @BeforeEach
    void setUp() {
        englishSubject = new Subject();
        englishSubject.setId(1);
        englishSubject.setName("英语");
        englishSubject.setCode("ENGLISH");
        englishSubject.setColor("#6B7CFF");
        englishSubject.setSortOrder(1);
        englishSubject.setIsLocked(false);

        chineseSubject = new Subject();
        chineseSubject.setId(2);
        chineseSubject.setName("语文");
        chineseSubject.setCode("CHINESE");
        chineseSubject.setColor("#FF7043");
        chineseSubject.setSortOrder(2);
        chineseSubject.setIsLocked(false);
    }

    @Test
    @DisplayName("should_returnAllSubjects_when_调用getAllSubjects")
    void should_returnAllSubjects_when_调用getAllSubjects() {
        // Arrange
        when(subjectRepository.findAllByOrderBySortOrderAsc())
                .thenReturn(List.of(englishSubject, chineseSubject));

        // Act
        List<SubjectDto> result = subjectService.getAllSubjects();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("英语");
        assertThat(result.get(0).code()).isEqualTo("ENGLISH");
        assertThat(result.get(0).color()).isEqualTo("#6B7CFF");
        assertThat(result.get(1).name()).isEqualTo("语文");
    }

    @Test
    @DisplayName("should_returnEmptyList_when_无学科数据")
    void should_returnEmptyList_when_无学科数据() {
        // Arrange
        when(subjectRepository.findAllByOrderBySortOrderAsc())
                .thenReturn(List.of());

        // Act
        List<SubjectDto> result = subjectService.getAllSubjects();

        // Assert
        assertThat(result).isEmpty();
    }
}
```

- [ ] **Step 2: 编写 SubjectController 单测**

创建 `backend/src/test/java/com/englishapp/controller/SubjectControllerTest.java`：

```java
package com.englishapp.controller;

import com.englishapp.common.Result;
import com.englishapp.dto.SubjectDto;
import com.englishapp.service.SubjectService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * 学科控制器单元测试
 *
 * @author TRAE Agent
 * @since 2026-07-21
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("学科控制器测试")
class SubjectControllerTest {

    @Mock
    private SubjectService subjectService;

    @InjectMocks
    private SubjectController subjectController;

    @Test
    @DisplayName("should_returnSubjects_when_调用getAllSubjects")
    void should_returnSubjects_when_调用getAllSubjects() {
        // Arrange
        SubjectDto dto = new SubjectDto(1, "英语", "ENGLISH", null, "#6B7CFF", 1, false);
        when(subjectService.getAllSubjects()).thenReturn(List.of(dto));

        // Act
        Result<List<SubjectDto>> result = subjectController.getAllSubjects();

        // Assert
        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getData()).hasSize(1);
        assertThat(result.getData().get(0).name()).isEqualTo("英语");
    }
}
```

- [ ] **Step 3: 编写 WordImageResolver 多子目录单测**

创建 `backend/src/test/java/com/englishapp/service/WordImageResolverMultiDirTest.java`：

```java
package com.englishapp.service;

import com.englishapp.config.WordImageProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * WordImageResolver 多子目录解析单元测试
 *
 * @author TRAE Agent
 * @since 2026-07-21
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("图片URL多子目录解析测试")
class WordImageResolverMultiDirTest {

    @Mock
    private WordImageProperties properties;

    @InjectMocks
    private WordImageResolver resolver;

    @BeforeEach
    void setUp() {
        when(properties.getBaseUrl()).thenReturn("http://localhost:8080");
        when(properties.getPathPrefix()).thenReturn("/images/words");
        when(properties.getExtension()).thenReturn(".jpg");
    }

    @Test
    @DisplayName("should_resolveWordsUrl_when_key不带斜杠")
    void should_resolveWordsUrl_when_key不带斜杠() {
        // Act
        String url = resolver.resolveUrl("car");

        // Assert：保持原有行为，使用 /images/words/ 前缀
        assertThat(url).isEqualTo("http://localhost:8080/images/words/car.jpg");
    }

    @Test
    @DisplayName("should_resolveTrainUrl_when_key带train前缀")
    void should_resolveTrainUrl_when_key带train前缀() {
        // Act
        String url = resolver.resolveUrl("train/steam-locomotive");

        // Assert：带子目录前缀，使用 /images/ 作为基础前缀
        assertThat(url).isEqualTo("http://localhost:8080/images/train/steam-locomotive.jpg");
    }

    @Test
    @DisplayName("should_resolveChineseUrl_when_key带chinese前缀")
    void should_resolveChineseUrl_when_key带chinese前缀() {
        // Act
        String url = resolver.resolveUrl("chinese/pinyin-a");

        // Assert
        assertThat(url).isEqualTo("http://localhost:8080/images/chinese/pinyin-a.jpg");
    }

    @Test
    @DisplayName("should_resolveMathUrl_when_key带math前缀")
    void should_resolveMathUrl_when_key带math前缀() {
        // Act
        String url = resolver.resolveUrl("math/triangle");

        // Assert
        assertThat(url).isEqualTo("http://localhost:8080/images/math/triangle.jpg");
    }
}
```

- [ ] **Step 4: 运行全部后端测试**

Run:
```bash
cd backend && export JAVA_HOME=/Users/dawn/Library/Java/JavaVirtualMachines/corretto-17.0.13/Contents/Home && export PATH=$JAVA_HOME/bin:$PATH && mvn test
```
Expected: 所有测试通过（含新增测试和现有测试）。如有 ThemeServiceTest 因 ThemeDto 新增字段而失败，需同步更新。

- [ ] **Step 5: 如有需要更新 ThemeServiceTest**

检查 `backend/src/test/java/com/englishapp/service/ThemeServiceTest.java`，如果它构造了 ThemeDto 对象，需要补上新增的 `subjectId` 参数。

- [ ] **Step 6: Commit**

```bash
git add backend/src/test/java/com/englishapp/service/SubjectServiceImplTest.java backend/src/test/java/com/englishapp/controller/SubjectControllerTest.java backend/src/test/java/com/englishapp/service/WordImageResolverMultiDirTest.java
git commit -m "test(subject): 新增Subject和WordImageResolver多子目录单元测试

Co-Authored-By: TRAE Agent <noreply@trae.ai>"
```

---

## Task 7: 前端基础设施 - tokens.css + api/subject.js + 路由

**Files:**
- Modify: `frontend/src/styles/tokens.css`
- Create: `frontend/src/api/subject.js`
- Modify: `frontend/src/router/index.js`

- [ ] **Step 1: tokens.css 新增学科主题色**

在 `tokens.css` 的 `--gradient-success` 行之后，`}` 之前新增：

```css

  /* ===== 学科主题色 ===== */
  /* 英语: 蓝紫（复用主色） */
  --subject-english: #6B7CFF;
  /* 语文: 朱砂红 */
  --subject-chinese: #FF7043;
  /* 数学: 青绿 */
  --subject-math: #26A69A;
  /* 课外: 紫罗兰 */
  --subject-extracurricular: #7E57C2;
```

- [ ] **Step 2: 创建 api/subject.js**

创建 `frontend/src/api/subject.js`：

```javascript
/**
 * @file 学科相关 API
 * @description 对应后端 GET /api/v1/subjects，获取所有学科。
 * @author english-app
 * @since 2026-07-21
 */
import http from './http'

/**
 * 获取所有学科列表。
 * 拦截器已剥离 Result 外层，直接返回 data（学科数组）。
 *
 * @return {Promise<Array>} 学科列表
 */
export const getSubjects = () => http.get('/subjects')

/**
 * 获取某学科下的所有主题列表。
 *
 * @param {number} subjectId 学科 ID
 * @return {Promise<Array>} 主题列表
 */
export const getThemesBySubject = (subjectId) => http.get(`/themes/subject/${subjectId}`)
```

- [ ] **Step 3: 路由新增 subject 路由**

修改 `frontend/src/router/index.js` 的路由表，在 home 和 theme 之间插入 subject 路由：

```javascript
/**
 * @file Vue Router 路由配置
 * @description 定义前端应用的五级学习流路由：学科→主题→单元→课时。
 *              所有页面均采用路由懒加载，按需打包，减小首屏体积。
 * @author english-app
 * @since 2026-07-20
 */
import { createRouter, createWebHistory } from 'vue-router'

// 路由表：home / subject / theme / unit / lesson 五级学习流
const routes = [
  { path: '/', name: 'home', component: () => import('../views/HomeView.vue') },
  { path: '/subject/:subjectId', name: 'subject', component: () => import('../views/SubjectView.vue') },
  { path: '/theme/:themeId', name: 'theme', component: () => import('../views/ThemeView.vue') },
  { path: '/unit/:unitId', name: 'unit', component: () => import('../views/UnitView.vue') },
  { path: '/lesson/:lessonId', name: 'lesson', component: () => import('../views/LessonView.vue') }
]

// 创建 history 模式路由
const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
```

- [ ] **Step 4: Commit**

```bash
git add frontend/src/styles/tokens.css frontend/src/api/subject.js frontend/src/router/index.js
git commit -m "feat(frontend): 新增学科主题色令牌、学科API和subject路由

Co-Authored-By: TRAE Agent <noreply@trae.ai>"
```

---

## Task 8: 前端 SubjectView.vue - 学科主题列表页

**Files:**
- Create: `frontend/src/views/SubjectView.vue`

- [ ] **Step 1: 创建 SubjectView.vue**

创建 `frontend/src/views/SubjectView.vue`。此页面展示某学科下的所有主题，逻辑从原 HomeView 迁移而来：

```vue
<!--
  SubjectView.vue - 学科主题列表页
  用途: 展示某学科下所有学习主题，点击进入主题详情。
  作者: english-app
  创建日期: 2026-07-21
-->
<script setup>
/**
 * @description 学科主题列表页，根据路由参数 subjectId 加载该学科下的主题列表。
  复用原有的主题卡片样式和 themeConfig 配置。
 */
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getThemesBySubject } from '../api/subject'
import { getThemeConfig } from '../config/themeConfig'
import BackBar from '../components/BackBar.vue'

const router = useRouter()
const route = useRoute()
const themes = ref([])
const isLoading = ref(true)
const errorMsg = ref('')

// 学科主题色映射（用于页面顶部 banner 配色）
const subjectColors = {
  1: 'var(--subject-english)',
  2: 'var(--subject-chinese)',
  3: 'var(--subject-math)',
  4: 'var(--subject-extracurricular)'
}

// 学科名称映射
const subjectNames = {
  1: '英语',
  2: '语文',
  3: '数学',
  4: '课外'
}

const subjectId = Number(route.params.subjectId)
const subjectColor = subjectColors[subjectId] || 'var(--color-primary)'
const subjectName = subjectNames[subjectId] || '学习'

onMounted(async () => {
  try {
    themes.value = await getThemesBySubject(subjectId)
  } catch (e) {
    errorMsg.value = '加载失败，请刷新重试'
    console.error('加载主题失败:', e)
  } finally {
    isLoading.value = false
  }
})

/**
 * 根据主题 ID 返回对应的 emoji 图标。
 * @param {number} id 主题 ID
 * @return {string} emoji 字符
 */
function getThemeIcon(id) {
  return getThemeConfig(id).emoji
}
</script>

<template>
  <div class="subject-page">
    <BackBar :title="subjectName + '学习'" />

    <!-- 主题选择区 -->
    <section class="theme-section">
      <!-- 加载中 -->
      <div v-if="isLoading" class="state-tip">
        <div class="loading-dot"></div>
        <p>加载中...</p>
      </div>

      <!-- 加载失败 -->
      <div v-else-if="errorMsg" class="state-tip error">
        <p>{{ errorMsg }}</p>
      </div>

      <!-- 主题卡片网格 -->
      <div v-else class="theme-grid">
        <div
          v-for="theme in themes"
          :key="theme.id"
          class="theme-card"
          :class="{ locked: theme.isLocked }"
          :style="{ '--card-accent': subjectColor }"
          @click="!theme.isLocked && router.push(`/theme/${theme.id}`)"
        >
          <div class="card-icon">{{ getThemeIcon(theme.id) }}</div>
          <h3 class="card-title">{{ theme.name }}</h3>
          <p class="card-desc" v-if="theme.isLocked">待解锁</p>
          <p class="card-desc" v-else>点击进入</p>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.subject-page {
  min-height: 100vh;
  padding-bottom: var(--space-8);
}

/* 主题选择区 */
.theme-section {
  padding: var(--space-4);
}

/* 主题卡片网格 */
.theme-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: var(--space-4);
}

/* 主题卡片 */
.theme-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: var(--space-6);
  text-align: center;
  cursor: pointer;
  box-shadow: var(--shadow-card);
  border-top: 4px solid var(--card-accent, var(--color-primary));
  transition: transform var(--duration-fast) var(--ease-bounce),
              box-shadow var(--duration-fast) var(--ease-smooth);
}

.theme-card:not(.locked):hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-hover);
}

.theme-card.locked {
  opacity: 0.6;
  cursor: not-allowed;
}

.card-icon {
  font-size: 3.5rem;
  margin-bottom: var(--space-3);
}

.card-title {
  font-size: var(--text-base);
  font-weight: var(--font-medium);
  color: var(--text-primary);
  margin-bottom: var(--space-2);
}

.card-desc {
  font-size: var(--text-sm);
  color: var(--text-tertiary);
}

/* 状态提示 */
.state-tip {
  text-align: center;
  padding: var(--space-8);
  color: var(--text-tertiary);
}

.state-tip.error {
  color: var(--color-warning);
}

.loading-dot {
  width: 32px;
  height: 32px;
  border: 3px solid var(--border-light);
  border-top-color: var(--color-primary);
  border-radius: var(--radius-pill);
  margin: 0 auto var(--space-3);
}

@media (prefers-reduced-motion: no-preference) {
  .loading-dot {
    animation: spin 0.8s linear infinite;
  }
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
```

- [ ] **Step 2: Commit**

```bash
git add frontend/src/views/SubjectView.vue
git commit -m "feat(frontend): 新增SubjectView学科主题列表页

Co-Authored-By: TRAE Agent <noreply@trae.ai>"
```

---

## Task 9: 前端 HomeView.vue 重写为学科选择页

**Files:**
- Modify: `frontend/src/views/HomeView.vue`

- [ ] **Step 1: 重写 HomeView.vue 为学科选择页**

用以下内容完整替换 `frontend/src/views/HomeView.vue`：

```vue
<!--
  HomeView.vue - 首页：学科选择
  用途: 吉祥物欢迎 header + 四大学科卡片网格，儿童友好的视觉风格。
  作者: english-app
  创建日期: 2026-07-21
-->
<script setup>
/**
 * @description 学科选择首页，展示英语/语文/数学/课外四个学科入口卡片。
  点击学科卡片跳转到 /subject/:subjectId 展示该学科下的主题列表。
 */
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getSubjects } from '../api/subject'
import mascotWelcome from '../assets/mascot/mascot-welcome.jpg'

const router = useRouter()
const subjects = ref([])
const isLoading = ref(true)
const errorMsg = ref('')

// 学科卡片配色映射（CSS 变量名）
const subjectColorVars = {
  'ENGLISH': 'var(--subject-english)',
  'CHINESE': 'var(--subject-chinese)',
  'MATH': 'var(--subject-math)',
  'EXTRACURRICULAR': 'var(--subject-extracurricular)'
}

// 学科 emoji 映射（后续替换为 AI 生成图标）
const subjectEmojis = {
  'ENGLISH': 'ABC',
  'CHINESE': '语',
  'MATH': '123',
  'EXTRACURRICULAR': '🚂'
}

onMounted(async () => {
  try {
    subjects.value = await getSubjects()
  } catch (e) {
    errorMsg.value = '加载失败，请刷新重试'
    console.error('加载学科失败:', e)
  } finally {
    isLoading.value = false
  }
})

/**
 * 获取学科卡片的主题色
 * @param {string} code 学科代码
 * @return {string} CSS 变量
 */
function getSubjectColor(code) {
  return subjectColorVars[code] || 'var(--color-primary)'
}

/**
 * 获取学科卡片的图标文字
 * @param {string} code 学科代码
 * @return {string} 图标文字
 */
function getSubjectIcon(code) {
  return subjectEmojis[code] || '📚'
}
</script>

<template>
  <div class="home">
    <!-- 顶部欢迎 header -->
    <header class="welcome-header">
      <span class="decor cloud c1">☁️</span>
      <span class="decor star c2">⭐</span>
      <span class="decor star c3">✨</span>
      <div class="header-content">
        <div class="greeting">
          <h1>嗨，小朋友！</h1>
          <p>今天想学什么呢？🎈</p>
        </div>
        <img :src="mascotWelcome" alt="小老鼠 Mimi" class="mascot" />
      </div>
    </header>

    <!-- 学科选择区 -->
    <section class="subject-section">
      <h2 class="section-title">选择学科</h2>

      <!-- 加载中 -->
      <div v-if="isLoading" class="state-tip">
        <div class="loading-dot"></div>
        <p>加载中...</p>
      </div>

      <!-- 加载失败 -->
      <div v-else-if="errorMsg" class="state-tip error">
        <p>{{ errorMsg }}</p>
      </div>

      <!-- 学科卡片网格 -->
      <div v-else class="subject-grid">
        <div
          v-for="subject in subjects"
          :key="subject.id"
          class="subject-card"
          :class="{ locked: subject.isLocked }"
          :style="{ '--card-accent': getSubjectColor(subject.code) }"
          @click="!subject.isLocked && router.push(`/subject/${subject.id}`)"
        >
          <div class="card-icon" :style="{ background: getSubjectColor(subject.code) }">
            {{ getSubjectIcon(subject.code) }}
          </div>
          <h3 class="card-title">{{ subject.name }}</h3>
          <p class="card-desc" v-if="subject.isLocked">待解锁</p>
          <p class="card-desc" v-else>点击进入 →</p>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.home {
  padding: var(--space-4);
  min-height: 100vh;
}

/* 欢迎 header */
.welcome-header {
  position: relative;
  background: var(--gradient-primary);
  border-radius: var(--radius-lg);
  padding: var(--space-6);
  margin-bottom: var(--space-6);
  overflow: hidden;
  box-shadow: var(--shadow-card);
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: relative;
  z-index: 1;
}

.greeting h1 {
  color: white;
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  margin-bottom: var(--space-2);
}

.greeting p {
  color: rgba(255, 255, 255, 0.9);
  font-size: var(--text-base);
}

.mascot {
  width: 120px;
  height: 120px;
  object-fit: contain;
  border-radius: var(--radius-md);
  flex-shrink: 0;
}

/* 飘浮装饰 */
.decor {
  position: absolute;
  z-index: 0;
}
.decor.cloud { font-size: 28px; }
.decor.star { font-size: 20px; }
.decor.c1 { top: var(--space-3); right: 40%; }
.decor.c2 { bottom: var(--space-4); left: var(--space-4); }
.decor.c3 { top: var(--space-6); right: var(--space-6); }

@media (prefers-reduced-motion: no-preference) {
  .decor.cloud { animation: float 6s ease-in-out infinite; }
  .decor.star { animation: float 4s ease-in-out infinite; }
  .decor.c3 { animation-delay: 1s; }
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}

/* 学科选择区 */
.section-title {
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin-bottom: var(--space-4);
}

/* 学科卡片网格 */
.subject-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: var(--space-4);
}

/* 学科卡片 */
.subject-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: var(--space-6);
  text-align: center;
  cursor: pointer;
  box-shadow: var(--shadow-card);
  border-top: 4px solid var(--card-accent, var(--color-primary));
  transition: transform var(--duration-fast) var(--ease-bounce),
              box-shadow var(--duration-fast) var(--ease-smooth);
}

.subject-card:not(.locked):hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-hover);
}

.subject-card.locked {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 学科图标：彩色圆形背景 */
.card-icon {
  width: 72px;
  height: 72px;
  border-radius: var(--radius-pill);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto var(--space-3);
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  color: white;
}

.card-title {
  font-size: var(--text-base);
  font-weight: var(--font-medium);
  color: var(--text-primary);
  margin-bottom: var(--space-2);
}

.card-desc {
  font-size: var(--text-sm);
  color: var(--text-tertiary);
}

/* 状态提示 */
.state-tip {
  text-align: center;
  padding: var(--space-8);
  color: var(--text-tertiary);
}

.state-tip.error {
  color: var(--color-warning);
}

.loading-dot {
  width: 32px;
  height: 32px;
  border: 3px solid var(--border-light);
  border-top-color: var(--color-primary);
  border-radius: var(--radius-pill);
  margin: 0 auto var(--space-3);
}

@media (prefers-reduced-motion: no-preference) {
  .loading-dot {
    animation: spin 0.8s linear infinite;
  }
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 响应式 */
@media (max-width: 480px) {
  .mascot { width: 96px; height: 96px; }
  .greeting h1 { font-size: var(--text-lg); }
}
</style>
```

- [ ] **Step 2: Commit**

```bash
git add frontend/src/views/HomeView.vue
git commit -m "feat(frontend): HomeView重写为学科选择页

Co-Authored-By: TRAE Agent <noreply@trae.ai>"
```

---

## Task 10: 集成验证

**Files:** 无代码变更，纯验证

- [ ] **Step 1: 启动后端**

Run:
```bash
cd backend && export JAVA_HOME=/Users/dawn/Library/Java/JavaVirtualMachines/corretto-17.0.13/Contents/Home && export PATH=$JAVA_HOME/bin:$PATH && ./run.sh
```
Expected: 应用启动无报错。

- [ ] **Step 2: 启动前端开发服务器**

Run（新终端）:
```bash
cd frontend && npm run dev
```
Expected: Vite 开发服务器启动在 https://localhost:5173。

- [ ] **Step 3: 浏览器验证首页**

打开 `https://localhost:5173`，验证：
- 顶部吉祥物欢迎 header 正常显示
- 下方显示4个学科卡片（英语/语文/数学/课外），各有不同主题色
- 点击"英语"卡片跳转到 `/subject/1`

- [ ] **Step 4: 验证英语学科主题列表**

在 `/subject/1` 页面验证：
- 返回栏标题为"英语学习"
- 显示4个英语主题（水果乐园/交通工具乐园/汪汪队立大功/小砾工程队）
- 点击任一主题能正常进入 `/theme/:id`，后续学习流程不受影响

- [ ] **Step 5: 验证现有英语学习流程无回归**

进入任一英语课时（如 `/lesson/2`），验证：
- 课时内容正常加载
- 听音、录音、评分功能正常
- 完成课时后跳转回单元列表

- [ ] **Step 6: 验证其他学科入口**

在首页点击"语文"/"数学"/"课外"卡片：
- 能跳转到 `/subject/2`、`/subject/3`、`/subject/4`
- 页面显示"暂无主题"或空列表（这些学科内容在后续阶段填充）

- [ ] **Step 7: 运行后端全量测试**

Run:
```bash
cd backend && export JAVA_HOME=/Users/dawn/Library/Java/JavaVirtualMachines/corretto-17.0.13/Contents/Home && export PATH=$JAVA_HOME/bin:$PATH && mvn test
```
Expected: 所有测试通过。

- [ ] **Step 8: 停止服务并提交最终状态**

```bash
git add -A
git commit -m "chore: 阶段1架构骨架完成，集成验证通过

Co-Authored-By: TRAE Agent <noreply@trae.ai>"
```

---

## 验收标准

阶段1完成后需满足：
1. 数据库新增 subject 表，含4条学科数据
2. theme 表新增 subject_id 字段，现有4个英语主题 subject_id=1
3. 后端 API `GET /api/v1/subjects` 返回4个学科
4. 后端 API `GET /api/v1/themes/subject/{id}` 返回该学科下的主题
5. LessonType 枚举包含 READING/QUIZ/CALCULATE
6. WordImageResolver 支持带子目录前缀的 key
7. 前端首页显示4个学科卡片
8. 点击英语学科能看到现有4个主题，学习流程无回归
9. 后端全部单元测试通过
10. SubjectView 页面正常工作，返回栏可用

## 后续阶段预告

阶段1完成后，后续4个阶段将分别制定计划：
- **阶段2**：英语迁移（补全缺失配图 + LessonView重构为分发器 + 提取Word/Sentence模板）
- **阶段3**：课外火车模块（Flyway V15 + Reading/Quiz模板组件 + AI图片生成）
- **阶段4**：语文模块（Flyway V13 + 复用模板 + AI图片生成）
- **阶段5**：数学模块（Flyway V14 + Calculate模板 + AI图片生成）
