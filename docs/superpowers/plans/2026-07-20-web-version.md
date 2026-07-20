# 网页版英语学习应用 - 实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 在现有 Spring Boot 后端基础上新增 Vue 3 网页前端,实现"听发音→跟读→评分→进度持久化"完整学习闭环,让孩子在电脑浏览器中使用。

**Architecture:** Vue 3 + Vite 构建产物嵌入 Spring Boot `static/` 目录的单体部署;后端新增单元/进度/语音评测模块;复用现有 SQLite 数据库与 user_progress 表;百度语音 API 提供 TTS 与发音评测。

**Tech Stack:** 后端 Java 17 + Spring Boot 3.2 + JPA + Flyway + SQLite;前端 Vue 3 + Vite + Pinia + Vue Router + axios;百度语音 REST API(TTS + 发音评测)。

**关联 Spec:** `docs/superpowers/specs/2026-07-20-web-version-design.md`

---

## 文件结构总览

### 后端新增/修改
```
backend/
├── pom.xml                                    # 新增 validation 依赖
├── src/main/java/com/englishapp/
│   ├── config/WebConfig.java                  # 静态资源映射 + CORS
│   ├── common/Result.java                     # 统一响应格式
│   ├── common/ResultCode.java                 # 状态码枚举
│   ├── controller/UnitController.java          # 单元列表
│   ├── controller/ProgressController.java     # 进度查询 + 完成
│   ├── controller/VoiceController.java        # 扩展 /score
│   ├── dto/UnitDto.java                       # 单元 DTO(带进度)
│   ├── dto/ProgressDto.java                   # 课时进度 DTO
│   ├── dto/CompleteRequest.java               # 完成请求体
│   ├── dto/ScoreResponse.java                 # 评测响应
│   ├── service/UnitService.java + Impl        # 单元业务
│   ├── service/ProgressService.java + Impl    # 进度业务 + 解锁逻辑
│   ├── voice/VoiceService.java                # 扩展 scorePronunciation
│   └── voice/BaiduVoiceService.java           # 实现评测
└── src/main/resources/db/migration/V3__transport_theme.sql
```

### 前端新建
```
frontend/
├── package.json
├── vite.config.ts
├── index.html
└── src/
    ├── main.js
    ├── App.vue
    ├── router/index.js
    ├── api/{http,theme,unit,lesson,progress,voice}.js
    ├── stores/{progress,audio}.js
    ├── composables/{useRecorder,useTts}.js
    ├── components/{StarBar,AudioButton,RecordButton,LessonCard,UnitCard}.vue
    └── views/{HomeView,ThemeView,UnitView,LessonView}.vue
```

---

## Phase A: 后端基础设施

### Task 1: 新增统一响应格式 Result

**Files:**
- Create: `backend/src/main/java/com/englishapp/common/Result.java`
- Create: `backend/src/main/java/com/englishapp/common/ResultCode.java`

- [ ] **Step 1: 创建 ResultCode 枚举**

```java
package com.englishapp.common;

/**
 * 统一响应状态码枚举
 * @author englishapp
 * @since 1.0.0
 */
public enum ResultCode {
    SUCCESS(200, "success"),
    BAD_REQUEST(400, "请求参数错误"),
    NOT_FOUND(404, "资源不存在"),
    VOICE_SERVICE_ERROR(503, "语音服务暂时不可用"),
    INTERNAL_ERROR(500, "服务器内部错误");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() { return code; }
    public String getMessage() { return message; }
}
```

- [ ] **Step 2: 创建 Result 统一响应类**

```java
package com.englishapp.common;

/**
 * 统一 API 响应格式
 * @author englishapp
 * @since 1.0.0
 */
public class Result<T> {
    private final int code;
    private final String message;
    private final T data;

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    public static <T> Result<T> error(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    public int getCode() { return code; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}
```

- [ ] **Step 3: 提交**

```bash
cd backend && git add src/main/java/com/englishapp/common/ && git commit -m "feat(common): 新增统一响应格式 Result 和 ResultCode"
```

---

### Task 2: 新增 WebConfig(静态资源 + CORS)

**Files:**
- Create: `backend/src/main/java/com/englishapp/config/WebConfig.java`

- [ ] **Step 1: 创建 WebConfig**

```java
package com.englishapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置:静态资源映射(Vue history 模式) + 开发期 CORS
 * @author englishapp
 * @since 1.0.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 配置 CORS:开发期放行 Vite dev server
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    /**
     * 让 Vue Router history 模式生效:非 /api 请求转发到 index.html
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.html");
    }

    /**
     * 静态资源处理:让 Vue 路由的任意路径都能返回 index.html
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }
}
```

- [ ] **Step 2: 新增 SPA fallback 异常处理(处理 404 转发到 index.html)**

```java
package com.englishapp.config;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.PathResourceResolver;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * SPA 资源解析器:非 API 请求且非静态资源时返回 index.html
 * (作为 WebConfig 的补充,通过内部类实现)
 */
```

将 `WebConfig` 的 `addResourceHandlers` 替换为使用自定义 resolver:

```java
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/**")
            .addResourceLocations("classpath:/static/")
            .resourceChain(true)
            .addResolver(new PathResourceResolver() {
                @Override
                protected Resource getResource(String resourcePath, Resource location) throws IOException {
                    Resource requested = location.createRelative(resourcePath);
                    // API 请求或存在的资源直接返回;否则返回 index.html 给 Vue Router 处理
                    if (resourcePath.startsWith("api/") || requested.isReadable()) {
                        return requested;
                    }
                    return new ClassPathResource("/static/index.html");
                }
            });
}
```

- [ ] **Step 3: 提交**

```bash
git add backend/src/main/java/com/englishapp/config/WebConfig.java
git commit -m "feat(config): 新增 WebConfig 支持 Vue SPA 部署与开发期 CORS"
```

---

### Task 3: 新增 validation 依赖

**Files:**
- Modify: `backend/pom.xml`

- [ ] **Step 1: 在 pom.xml 的 `<dependencies>` 中新增 validation**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

- [ ] **Step 2: 验证依赖加载**

```bash
cd backend && ./mvnw dependency:resolve -q
```
Expected: 无错误

- [ ] **Step 3: 提交**

```bash
git add backend/pom.xml && git commit -m "chore: 新增 spring-boot-starter-validation 依赖"
```

---

## Phase B: 数据迁移

### Task 4: V3 迁移 - 交通工具主题种子数据

**Files:**
- Create: `backend/src/main/resources/db/migration/V3__transport_theme.sql`

- [ ] **Step 1: 编写迁移 SQL**

```sql
-- 新增主题:交通工具乐园
INSERT INTO theme (id, name, sort_order, is_locked) VALUES (2, '交通工具乐园', 2, 0);

-- 新增单元:陆地交通(4 课时)、空中交通(3 课时)、水上交通(3 课时)
INSERT INTO unit (id, theme_id, name, sort_order, is_locked) VALUES
(4, 2, '陆地交通', 1, 0),
(5, 2, '空中交通', 2, 1),
(6, 2, '水上交通', 3, 1);

-- Unit 4: 陆地交通(4 课时)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(2, 4, '认识汽车', 'WORD', '{"type":"WORD","items":[{"word":"car","phonetic":"/kɑːr/","translation":"小汽车","emoji":"🚗"},{"word":"bus","phonetic":"/bʌs/","translation":"公交车","emoji":"🚌"},{"word":"bike","phonetic":"/baɪk/","translation":"自行车","emoji":"🚲"},{"word":"truck","phonetic":"/trʌk/","translation":"卡车","emoji":"🚚"}]}', 1, 3),
(3, 4, '认识火车', 'WORD', '{"type":"WORD","items":[{"word":"train","phonetic":"/treɪn/","translation":"火车","emoji":"🚂"},{"word":"subway","phonetic":"/ˈsʌbweɪ/","translation":"地铁","emoji":"🚇"},{"word":"taxi","phonetic":"/ˈtæksi/","translation":"出租车","emoji":"🚕"},{"word":"police car","phonetic":"/pəˈliːs kɑːr/","translation":"警车","emoji":"🚓"}]}', 2, 3),
(4, 4, '我看见了', 'SENTENCE', '{"type":"SENTENCE","items":[{"sentence":"I see a car.","translation":"我看见一辆小汽车。","emoji":"🚗"},{"sentence":"I see a bus.","translation":"我看见一辆公交车。","emoji":"🚌"},{"sentence":"I see a train.","translation":"我看见一列火车。","emoji":"🚂"}]}', 3, 3),
(5, 4, '它是什么颜色', 'SENTENCE', '{"type":"SENTENCE","items":[{"sentence":"The car is red.","translation":"小汽车是红色的。","emoji":"🚗"},{"sentence":"The bus is yellow.","translation":"公交车是黄色的。","emoji":"🚌"},{"sentence":"The train is blue.","translation":"火车是蓝色的。","emoji":"🚂"}]}', 4, 3);

-- Unit 5: 空中交通(3 课时)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(6, 5, '认识飞机', 'WORD', '{"type":"WORD","items":[{"word":"airplane","phonetic":"/ˈerpleɪn/","translation":"飞机","emoji":"✈️"},{"word":"helicopter","phonetic":"/ˈhelɪkɑːptər/","translation":"直升机","emoji":"🚁"},{"word":"rocket","phonetic":"/ˈrɑːkɪt/","translation":"火箭","emoji":"🚀"},{"word":"balloon","phonetic":"/bəˈluːn/","translation":"气球","emoji":"🎈"}]}', 1, 3),
(7, 5, '飞得高', 'SENTENCE', '{"type":"SENTENCE","items":[{"sentence":"The airplane flies high.","translation":"飞机飞得高。","emoji":"✈️"},{"sentence":"The rocket goes up.","translation":"火箭向上飞。","emoji":"🚀"},{"sentence":"The balloon floats.","translation":"气球飘浮。","emoji":"🎈"}]}', 2, 3),
(8, 5, '我喜欢', 'SENTENCE', '{"type":"SENTENCE","items":[{"sentence":"I like the airplane.","translation":"我喜欢飞机。","emoji":"✈️"},{"sentence":"I like the helicopter.","translation":"我喜欢直升机。","emoji":"🚁"},{"sentence":"I like the rocket.","translation":"我喜欢火箭。","emoji":"🚀"}]}', 3, 3);

-- Unit 6: 水上交通(3 课时)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(9, 6, '认识船', 'WORD', '{"type":"WORD","items":[{"word":"boat","phonetic":"/boʊt/","translation":"小船","emoji":"⛵"},{"word":"ship","phonetic":"/ʃɪp/","translation":"大船","emoji":"🚢"},{"word":"submarine","phonetic":"/ˌsʌbməˈriːn/","translation":"潜水艇","emoji":"🤿"},{"word":"canoe","phonetic":"/kəˈnuː/","translation":"独木舟","emoji":"🛶"}]}', 1, 3),
(10, 6, '在水上', 'SENTENCE', '{"type":"SENTENCE","items":[{"sentence":"The boat is on the water.","translation":"小船在水面上。","emoji":"⛵"},{"sentence":"The ship is big.","translation":"大船很大。","emoji":"🚢"},{"sentence":"The submarine is under water.","translation":"潜水艇在水下。","emoji":"🤿"}]}', 2, 3),
(11, 6, '复习大挑战', 'SENTENCE', '{"type":"SENTENCE","items":[{"sentence":"I see a car and a bus.","translation":"我看见一辆小汽车和一辆公交车。","emoji":"🚗"},{"sentence":"The airplane flies in the sky.","translation":"飞机在天上飞。","emoji":"✈️"},{"sentence":"The boat is on the water.","translation":"小船在水面上。","emoji":"⛵"}]}', 3, 3);
```

- [ ] **Step 2: 删除旧 english.db 让迁移重新执行(开发期)**

```bash
rm backend/english.db
cd backend && ./mvnw spring-boot:run
```
Expected: 控制台显示 Flyway 执行 V1/V2/V3,启动成功后 `Ctrl+C` 停止

- [ ] **Step 3: 验证数据**

```bash
cd backend && sqlite3 english.db "SELECT count(*) FROM lesson WHERE unit_id IN (4,5,6);"
```
Expected: `10`

- [ ] **Step 4: 提交**

```bash
git add backend/src/main/resources/db/migration/V3__transport_theme.sql
git commit -m "feat(db): V3 迁移新增交通工具主题(3 单元 10 课时)"
```

---

## Phase C: 后端单元模块

### Task 5: UnitDto 与 UnitController + UnitService

**Files:**
- Create: `backend/src/main/java/com/englishapp/dto/UnitDto.java`
- Create: `backend/src/main/java/com/englishapp/service/UnitService.java`
- Create: `backend/src/main/java/com/englishapp/service/UnitServiceImpl.java`
- Create: `backend/src/main/java/com/englishapp/controller/UnitController.java`

- [ ] **Step 1: 创建 UnitDto**

```java
package com.englishapp.dto;

/**
 * 单元 DTO(带进度信息)
 * @author englishapp
 * @since 1.0.0
 */
public class UnitDto {
    private Integer id;
    private String name;
    private Integer sortOrder;
    private Boolean isLocked;
    private Integer totalLessons;   // 单元总课时数
    private Integer completedLessons; // 已完成课时数

    public UnitDto(Integer id, String name, Integer sortOrder, Boolean isLocked,
                   Integer totalLessons, Integer completedLessons) {
        this.id = id;
        this.name = name;
        this.sortOrder = sortOrder;
        this.isLocked = isLocked;
        this.totalLessons = totalLessons;
        this.completedLessons = completedLessons;
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public Integer getSortOrder() { return sortOrder; }
    public Boolean getIsLocked() { return isLocked; }
    public Integer getTotalLessons() { return totalLessons; }
    public Integer getCompletedLessons() { return completedLessons; }
}
```

- [ ] **Step 2: 创建 UnitService 接口**

```java
package com.englishapp.service;

import com.englishapp.dto.UnitDto;
import java.util.List;

/**
 * 单元业务接口
 * @author englishapp
 * @since 1.0.0
 */
public interface UnitService {
    /**
     * 获取某主题下所有单元(带进度)
     * @param themeId 主题 ID
     * @param userId 用户 ID
     * @return 单元列表
     */
    List<UnitDto> getUnitsByTheme(Integer themeId, Integer userId);
}
```

- [ ] **Step 3: 创建 UnitServiceImpl**

```java
package com.englishapp.service;

import com.englishapp.domain.Unit;
import com.englishapp.domain.enums.ProgressStatus;
import com.englishapp.dto.UnitDto;
import com.englishapp.repository.LessonRepository;
import com.englishapp.repository.UnitRepository;
import com.englishapp.repository.UserProgressRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 单元业务实现
 * @author englishapp
 * @since 1.0.0
 */
@Service
public class UnitServiceImpl implements UnitService {

    private static final Integer DEFAULT_USER_ID = 1;

    private final UnitRepository unitRepository;
    private final LessonRepository lessonRepository;
    private final UserProgressRepository userProgressRepository;

    public UnitServiceImpl(UnitRepository unitRepository,
                           LessonRepository lessonRepository,
                           UserProgressRepository userProgressRepository) {
        this.unitRepository = unitRepository;
        this.lessonRepository = lessonRepository;
        this.userProgressRepository = userProgressRepository;
    }

    @Override
    public List<UnitDto> getUnitsByTheme(Integer themeId, Integer userId) {
        Integer uid = userId != null ? userId : DEFAULT_USER_ID;
        List<Unit> units = unitRepository.findByThemeIdOrderBySortOrderAsc(themeId);
        return units.stream().map(unit -> toDto(unit, uid)).toList();
    }

    private UnitDto toDto(Unit unit, Integer userId) {
        Integer total = lessonRepository.countByUnitId(unit.getId());
        Integer completed = userProgressRepository
                .countByUserIdAndLessonUnitIdAndStatus(userId, unit.getId(), ProgressStatus.COMPLETED);
        return new UnitDto(unit.getId(), unit.getName(), unit.getSortOrder(),
                unit.getIsLocked(), total, completed);
    }
}
```

- [ ] **Step 4: 在 UnitRepository 补充查询方法**

修改 `backend/src/main/java/com/englishapp/repository/UnitRepository.java`,确保存在:

```java
package com.englishapp.repository;

import com.englishapp.domain.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UnitRepository extends JpaRepository<Unit, Integer> {
    List<Unit> findByThemeIdOrderBySortOrderAsc(Integer themeId);
}
```

- [ ] **Step 5: 在 LessonRepository 补充 count 方法**

修改 `backend/src/main/java/com/englishapp/repository/LessonRepository.java`:

```java
package com.englishapp.repository;

import com.englishapp.domain.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Integer> {
    List<Lesson> findByUnitIdOrderBySortOrderAsc(Integer unitId);

    @Query("SELECT COUNT(l) FROM Lesson l WHERE l.unitId = :unitId")
    Integer countByUnitId(Integer unitId);
}
```

- [ ] **Step 6: 在 UserProgressRepository 补充 count 方法**

修改 `backend/src/main/java/com/englishapp/repository/UserProgressRepository.java`:

```java
package com.englishapp.repository;

import com.englishapp.domain.UserProgress;
import com.englishapp.domain.enums.ProgressStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface UserProgressRepository extends JpaRepository<UserProgress, Integer> {

    @Query("SELECT up FROM UserProgress up WHERE up.userId = :userId AND up.lessonId = :lessonId")
    Optional<UserProgress> findByUserIdAndLessonId(Integer userId, Integer lessonId);

    @Query("SELECT COUNT(up) FROM UserProgress up " +
           "WHERE up.userId = :userId AND up.lesson.unitId = :unitId AND up.status = :status")
    Integer countByUserIdAndLessonUnitIdAndStatus(Integer userId, Integer unitId, ProgressStatus status);

    List<UserProgress> findByUserId(Integer userId);
}
```

- [ ] **Step 7: 创建 UnitController**

```java
package com.englishapp.controller;

import com.englishapp.common.Result;
import com.englishapp.dto.UnitDto;
import com.englishapp.service.UnitService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 单元 REST 控制器
 * @author englishapp
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/units")
public class UnitController {

    private final UnitService unitService;

    public UnitController(UnitService unitService) {
        this.unitService = unitService;
    }

    /**
     * 获取某主题下所有单元(带进度)
     * @param themeId 主题 ID
     * @param userId 用户 ID(可选,默认 1)
     */
    @GetMapping("/theme/{themeId}")
    public Result<List<UnitDto>> getUnitsByTheme(
            @PathVariable Integer themeId,
            @RequestParam(required = false, defaultValue = "1") Integer userId) {
        return Result.success(unitService.getUnitsByTheme(themeId, userId));
    }
}
```

- [ ] **Step 8: 启动后端验证**

```bash
cd backend && ./mvnw spring-boot:run
```
访问 `http://localhost:8080/api/v1/units/theme/2`
Expected: 返回 3 个单元 JSON

- [ ] **Step 9: 提交**

```bash
git add backend/src/main/java/com/englishapp/{dto/UnitDto.java,service/UnitService.java,service/UnitServiceImpl.java,controller/UnitController.java,repository/UnitRepository.java,repository/LessonRepository.java,repository/UserProgressRepository.java}
git commit -m "feat(unit): 新增单元列表 API(带进度统计)"
```

---

## Phase D: 后端进度模块

### Task 6: ProgressDto 与 CompleteRequest

**Files:**
- Create: `backend/src/main/java/com/englishapp/dto/ProgressDto.java`
- Create: `backend/src/main/java/com/englishapp/dto/CompleteRequest.java`
- Create: `backend/src/main/java/com/englishapp/dto/CompleteResponse.java`

- [ ] **Step 1: 创建 ProgressDto**

```java
package com.englishapp.dto;

/**
 * 课时进度 DTO
 * @author englishapp
 * @since 1.0.0
 */
public class ProgressDto {
    private Integer lessonId;
    private String status;  // LOCKED / UNLOCKED / COMPLETED
    private Integer stars;
    private Integer score;

    public ProgressDto(Integer lessonId, String status, Integer stars, Integer score) {
        this.lessonId = lessonId;
        this.status = status;
        this.stars = stars;
        this.score = score;
    }

    public Integer getLessonId() { return lessonId; }
    public String getStatus() { return status; }
    public Integer getStars() { return stars; }
    public Integer getScore() { return score; }
}
```

- [ ] **Step 2: 创建 CompleteRequest**

```java
package com.englishapp.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 完成课时请求体
 * @author englishapp
 * @since 1.0.0
 */
public class CompleteRequest {
    @NotNull
    @Min(0) @Max(3)
    private Integer stars;

    @NotNull
    @Min(0) @Max(100)
    private Integer score;

    public Integer getStars() { return stars; }
    public void setStars(Integer stars) { this.stars = stars; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
}
```

- [ ] **Step 3: 创建 CompleteResponse**

```java
package com.englishapp.dto;

/**
 * 完成课时响应(含下一课解锁信息)
 * @author englishapp
 * @since 1.0.0
 */
public class CompleteResponse {
    private Integer nextLessonId;
    private Boolean unlocked;

    public CompleteResponse(Integer nextLessonId, Boolean unlocked) {
        this.nextLessonId = nextLessonId;
        this.unlocked = unlocked;
    }

    public Integer getNextLessonId() { return nextLessonId; }
    public Boolean getUnlocked() { return unlocked; }
}
```

- [ ] **Step 4: 提交**

```bash
git add backend/src/main/java/com/englishapp/dto/{ProgressDto,CompleteRequest,CompleteResponse}.java
git commit -m "feat(dto): 新增进度相关 DTO"
```

---

### Task 7: ProgressService 接口与实现

**Files:**
- Create: `backend/src/main/java/com/englishapp/service/ProgressService.java`
- Create: `backend/src/main/java/com/englishapp/service/ProgressServiceImpl.java`

- [ ] **Step 1: 创建 ProgressService 接口**

```java
package com.englishapp.service;

import com.englishapp.dto.CompleteRequest;
import com.englishapp.dto.CompleteResponse;
import com.englishapp.dto.ProgressDto;

/**
 * 进度业务接口
 * @author englishapp
 * @since 1.0.0
 */
public interface ProgressService {

    /**
     * 获取某课时进度(不存在则返回默认 LOCKED/UNLOCKED)
     */
    ProgressDto getLessonProgress(Integer lessonId, Integer userId);

    /**
     * 完成课时:更新进度 + 解锁下一课时
     */
    CompleteResponse completeLesson(Integer lessonId, CompleteRequest request, Integer userId);
}
```

- [ ] **Step 2: 创建 ProgressServiceImpl**

```java
package com.englishapp.service;

import com.englishapp.domain.Lesson;
import com.englishapp.domain.UserProgress;
import com.englishapp.domain.enums.ProgressStatus;
import com.englishapp.dto.CompleteRequest;
import com.englishapp.dto.CompleteResponse;
import com.englishapp.dto.ProgressDto;
import com.englishapp.repository.LessonRepository;
import com.englishapp.repository.UserProgressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 进度业务实现
 * @author englishapp
 * @since 1.0.0
 */
@Service
public class ProgressServiceImpl implements ProgressService {

    private static final Integer DEFAULT_USER_ID = 1;

    private final UserProgressRepository userProgressRepository;
    private final LessonRepository lessonRepository;

    public ProgressServiceImpl(UserProgressRepository userProgressRepository,
                                LessonRepository lessonRepository) {
        this.userProgressRepository = userProgressRepository;
        this.lessonRepository = lessonRepository;
    }

    @Override
    public ProgressDto getLessonProgress(Integer lessonId, Integer userId) {
        Integer uid = userId != null ? userId : DEFAULT_USER_ID;
        Optional<UserProgress> progress = userProgressRepository
                .findByUserIdAndLessonId(uid, lessonId);
        if (progress.isPresent()) {
            UserProgress up = progress.get();
            return new ProgressDto(lessonId, up.getStatus().name(),
                    up.getStars(), up.getScore());
        }
        // 无记录时:第一课时默认 UNLOCKED,其余 LOCKED
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("课时不存在: " + lessonId));
        List<Lesson> unitLessons = lessonRepository
                .findByUnitIdOrderBySortOrderAsc(lesson.getUnitId());
        boolean isFirst = unitLessons.stream()
                .mapToInt(Lesson::getSortOrder).min().orElse(0) == lesson.getSortOrder();
        String status = isFirst ? ProgressStatus.UNLOCKED.name() : ProgressStatus.LOCKED.name();
        return new ProgressDto(lessonId, status, 0, 0);
    }

    @Override
    @Transactional
    public CompleteResponse completeLesson(Integer lessonId, CompleteRequest request, Integer userId) {
        Integer uid = userId != null ? userId : DEFAULT_USER_ID;
        UserProgress progress = userProgressRepository
                .findByUserIdAndLessonId(uid, lessonId)
                .orElseGet(() -> {
                    UserProgress newUp = new UserProgress();
                    newUp.setUserId(uid);
                    newUp.setLessonId(lessonId);
                    newUp.setStatus(ProgressStatus.UNLOCKED);
                    newUp.setStars(0);
                    newUp.setScore(0);
                    return newUp;
                });

        // 取最高分(允许重复挑战)
        Integer bestScore = Math.max(progress.getScore(), request.getScore());
        Integer bestStars = Math.max(progress.getStars(), request.getStars());
        progress.setStatus(ProgressStatus.COMPLETED);
        progress.setStars(bestStars);
        progress.setScore(bestScore);
        userProgressRepository.save(progress);

        // 解锁下一课时
        Lesson current = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("课时不存在"));
        List<Lesson> unitLessons = lessonRepository
                .findByUnitIdOrderBySortOrderAsc(current.getUnitId());
        Lesson nextLesson = unitLessons.stream()
                .filter(l -> l.getSortOrder() > current.getSortOrder())
                .findFirst()
                .orElse(null);

        if (nextLesson != null) {
            unlockLesson(nextLesson.getId(), uid);
            return new CompleteResponse(nextLesson.getId(), true);
        }
        return new CompleteResponse(null, false);
    }

    /**
     * 解锁指定课时(若记录不存在则创建)
     */
    private void unlockLesson(Integer lessonId, Integer userId) {
        Optional<UserProgress> existing = userProgressRepository
                .findByUserIdAndLessonId(userId, lessonId);
        if (existing.isEmpty()) {
            UserProgress up = new UserProgress();
            up.setUserId(userId);
            up.setLessonId(lessonId);
            up.setStatus(ProgressStatus.UNLOCKED);
            up.setStars(0);
            up.setScore(0);
            userProgressRepository.save(up);
        } else {
            UserProgress up = existing.get();
            if (up.getStatus() == ProgressStatus.LOCKED) {
                up.setStatus(ProgressStatus.UNLOCKED);
                userProgressRepository.save(up);
            }
        }
    }
}
```

- [ ] **Step 3: 确保 UserProgress 实体有 setter**

检查 `backend/src/main/java/com/englishapp/domain/UserProgress.java`,确保有 setter 方法或使用 Lombok `@Setter`。若无,补充:

```java
public void setUserId(Integer userId) { this.userId = userId; }
public void setLessonId(Integer lessonId) { this.lessonId = lessonId; }
public void setStatus(ProgressStatus status) { this.status = status; }
public void setStars(Integer stars) { this.stars = stars; }
public void setScore(Integer score) { this.score = score; }
```

- [ ] **Step 4: 提交**

```bash
git add backend/src/main/java/com/englishapp/service/{ProgressService,ProgressServiceImpl}.java backend/src/main/java/com/englishapp/domain/UserProgress.java
git commit -m "feat(progress): 新增进度业务(完成课时 + 解锁下一课 + 取最高分)"
```

---

### Task 8: ProgressController

**Files:**
- Create: `backend/src/main/java/com/englishapp/controller/ProgressController.java`

- [ ] **Step 1: 创建 ProgressController**

```java
package com.englishapp.controller;

import com.englishapp.common.Result;
import com.englishapp.dto.CompleteRequest;
import com.englishapp.dto.CompleteResponse;
import com.englishapp.dto.ProgressDto;
import com.englishapp.service.ProgressService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 进度 REST 控制器
 * @author englishapp
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/progress")
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    /**
     * 获取某课时进度
     */
    @GetMapping("/lesson/{lessonId}")
    public Result<ProgressDto> getLessonProgress(
            @PathVariable Integer lessonId,
            @RequestParam(required = false, defaultValue = "1") Integer userId) {
        return Result.success(progressService.getLessonProgress(lessonId, userId));
    }

    /**
     * 完成课时并解锁下一课
     */
    @PostMapping("/lesson/{lessonId}/complete")
    public Result<CompleteResponse> completeLesson(
            @PathVariable Integer lessonId,
            @Valid @RequestBody CompleteRequest request,
            @RequestParam(required = false, defaultValue = "1") Integer userId) {
        return Result.success(progressService.completeLesson(lessonId, request, userId));
    }
}
```

- [ ] **Step 2: 启动验证**

```bash
cd backend && ./mvnw spring-boot:run
```
访问 `http://localhost:8080/api/v1/progress/lesson/2`
Expected: `{"code":200,"message":"success","data":{"lessonId":2,"status":"UNLOCKED","stars":0,"score":0}}`

- [ ] **Step 3: 提交**

```bash
git add backend/src/main/java/com/englishapp/controller/ProgressController.java
git commit -m "feat(progress): 新增进度查询与完成 API"
```

---

## Phase E: 后端语音评测

### Task 9: 扩展 VoiceService 接口与 ScoreResponse

**Files:**
- Modify: `backend/src/main/java/com/englishapp/voice/VoiceService.java`
- Create: `backend/src/main/java/com/englishapp/voice/dto/ScoreResponse.java`

- [ ] **Step 1: 扩展 VoiceService 接口**

```java
package com.englishapp.voice;

import com.englishapp.voice.dto.ScoreResponse;

public interface VoiceService {
    byte[] textToSpeech(String text);
    String speechToText(byte[] audioData);

    /**
     * 发音评测
     * @param audioData 音频数据(wav 16k 16bit mono)
     * @param text 参考文本
     * @return 评测结果
     */
    ScoreResponse scorePronunciation(byte[] audioData, String text);
}
```

- [ ] **Step 2: 创建 ScoreResponse**

```java
package com.englishapp.voice.dto;

/**
 * 发音评测结果
 * @author englishapp
 * @since 1.0.0
 */
public class ScoreResponse {
    private Integer score;       // 0-100
    private String feedback;     // 反馈文案

    public ScoreResponse(Integer score, String feedback) {
        this.score = score;
        this.feedback = feedback;
    }

    public Integer getScore() { return score; }
    public String getFeedback() { return feedback; }
}
```

- [ ] **Step 3: 提交**

```bash
git add backend/src/main/java/com/englishapp/voice/VoiceService.java backend/src/main/java/com/englishapp/voice/dto/ScoreResponse.java
git commit -m "feat(voice): 扩展 VoiceService 接口新增发音评测方法"
```

---

### Task 10: BaiduVoiceService 实现评测 + TTS

**Files:**
- Modify: `backend/src/main/java/com/englishapp/voice/BaiduVoiceService.java`

- [ ] **Step 1: 实现 TTS(调百度语音合成 API)**

修改 `BaiduVoiceService.java`,完整内容:

```java
package com.englishapp.voice;

import com.englishapp.config.VoiceProperties;
import com.englishapp.voice.dto.ScoreResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 百度语音服务实现(TTS + 发音评测)
 * @author englishapp
 * @since 1.0.0
 */
@Service
@ConditionalOnProperty(name = "voice.provider", havingValue = "baidu")
public class BaiduVoiceService implements VoiceService {

    private static final Logger log = LoggerFactory.getLogger(BaiduVoiceService.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String TOKEN_URL =
            "https://aip.baidubce.com/oauth/2.0/token";
    private static final String TTS_URL =
            "https://tsn.baidu.com/text2audio";
    private static final String SCORE_URL =
            "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/brain_assessment/score";

    private final VoiceProperties voiceProperties;
    private final RestTemplate restTemplate = new RestTemplate();
    private String cachedToken;
    private long tokenExpireAt;

    public BaiduVoiceService(VoiceProperties voiceProperties) {
        this.voiceProperties = voiceProperties;
    }

    /**
     * 获取百度 access_token(带缓存)
     */
    private String getToken() {
        if (cachedToken != null && System.currentTimeMillis() < tokenExpireAt) {
            return cachedToken;
        }
        String url = TOKEN_URL + "?grant_type=client_credentials"
                + "&client_id=" + voiceProperties.getBaidu().getApiKey()
                + "&client_secret=" + voiceProperties.getBaidu().getSecretKey();
        try {
            ResponseEntity<String> resp = restTemplate.getForEntity(url, String.class);
            JsonNode node = MAPPER.readTree(resp.getBody());
            cachedToken = node.get("access_token").asText();
            long expiresIn = node.get("expires_in").asLong();
            tokenExpireAt = System.currentTimeMillis() + (expiresIn - 60) * 1000;
            return cachedToken;
        } catch (Exception e) {
            log.error("获取百度 token 失败", e);
            throw new RuntimeException("语音服务认证失败", e);
        }
    }

    @Override
    public byte[] textToSpeech(String text) {
        log.info("TTS request for text: {}", text);
        try {
            String token = getToken();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("tex", text);
            form.add("tok", token);
            form.add("cuid", "english-app-web");
            form.add("lan", "zh");
            form.add("ctp", "1");
            form.add("per", "4118");   // 度小薇(女声,适合儿童)

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);
            ResponseEntity<byte[]> resp = restTemplate.postForEntity(TTS_URL, entity, byte[].class);
            return resp.getBody();
        } catch (Exception e) {
            log.error("TTS 调用失败: {}", text, e);
            return new byte[0];
        }
    }

    @Override
    public String speechToText(byte[] audioData) {
        log.info("ASR request, audio length: {} bytes", audioData.length);
        return "";
    }

    @Override
    public ScoreResponse scorePronunciation(byte[] audioData, String text) {
        log.info("发音评测: text={}, audioBytes={}", text, audioData.length);
        try {
            String token = getToken();
            String audioBase64 = Base64.getEncoder().encodeToString(audioData);

            Map<String, Object> body = new HashMap<>();
            body.put("audio", audioBase64);
            body.put("text", text);
            body.put("type", "wav");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String url = SCORE_URL + "?access_token=" + token;
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> resp = restTemplate.postForEntity(url, entity, String.class);

            JsonNode node = MAPPER.readTree(resp.getBody());
            // 百度评测返回字段:score(总分)、pronunciation(发音分)、fluency(流利度)
            int score = node.has("score") ? node.get("score").asInt() : 0;
            String feedback = buildFeedback(score);
            return new ScoreResponse(score, feedback);
        } catch (Exception e) {
            log.error("发音评测调用失败: text={}", text, e);
            return new ScoreResponse(0, "评分服务暂时不可用,请重试");
        }
    }

    /**
     * 根据分数生成鼓励文案
     */
    private String buildFeedback(int score) {
        if (score >= 80) return "太棒了!发音很标准!";
        if (score >= 60) return "不错哦!再练练更好!";
        if (score >= 40) return "加油!差一点点!";
        return "再试一次,你可以的!";
    }
}
```

- [ ] **Step 2: 启动验证 TTS(需 .env 密钥)**

```bash
cd backend && ./run.sh
```
用 curl 测试:
```bash
curl -X POST http://localhost:8080/api/v1/voice/tts -H "Content-Type: application/json" -d '{"text":"car"}' -o /tmp/test.wav
file /tmp/test.wav
```
Expected: 输出音频文件信息(非空)

- [ ] **Step 3: 提交**

```bash
git add backend/src/main/java/com/englishapp/voice/BaiduVoiceService.java
git commit -m "feat(voice): 实现百度 TTS 与发音评测"
```

---

### Task 11: VoiceController 扩展 /score 端点

**Files:**
- Modify: `backend/src/main/java/com/englishapp/controller/VoiceController.java`

- [ ] **Step 1: 修改 VoiceController,新增 /score 并改 TTS 为 JSON 请求**

```java
package com.englishapp.controller;

import com.englishapp.common.Result;
import com.englishapp.voice.VoiceService;
import com.englishapp.voice.dto.ScoreResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * 语音 REST 控制器
 * @author englishapp
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/voice")
public class VoiceController {

    private final VoiceService voiceService;

    public VoiceController(VoiceService voiceService) {
        this.voiceService = voiceService;
    }

    /**
     * 文字转语音
     */
    @PostMapping("/tts")
    public ResponseEntity<byte[]> textToSpeech(@RequestBody Map<String, String> body) {
        String text = body.get("text");
        byte[] audio = voiceService.textToSpeech(text);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .body(audio);
    }

    /**
     * 发音评测
     * @param audio 录音文件(wav 16k 16bit mono)
     * @param text 参考文本
     */
    @PostMapping("/score")
    public Result<ScoreResponse> scorePronunciation(
            @RequestParam("audio") MultipartFile audio,
            @RequestParam("text") String text) throws IOException {
        byte[] audioData = audio.getBytes();
        ScoreResponse response = voiceService.scorePronunciation(audioData, text);
        return Result.success(response);
    }
}
```

- [ ] **Step 2: 提交**

```bash
git add backend/src/main/java/com/englishapp/controller/VoiceController.java
git commit -m "feat(voice): 新增 /score 发音评测端点,改 TTS 为 JSON 请求"
```

---

## Phase F: 前端脚手架

### Task 12: 创建 Vue 3 + Vite 项目

**Files:**
- Create: `frontend/package.json`
- Create: `frontend/vite.config.ts`
- Create: `frontend/index.html`
- Create: `frontend/src/main.js`

- [ ] **Step 1: 用 npm 创建 Vite 项目**

```bash
cd /Users/dawn/Desktop/english_app
npm create vite@latest frontend -- --template vue
```

- [ ] **Step 2: 安装依赖**

```bash
cd frontend && npm install
npm install vue-router@4 pinia axios
```

- [ ] **Step 3: 配置 vite.config.ts**

```ts
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  build: {
    outDir: '../backend/src/main/resources/static',
    emptyOutDir: true
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

- [ ] **Step 4: 验证前端可启动**

```bash
cd frontend && npm run dev
```
Expected: 访问 `http://localhost:5173` 看到默认页面

- [ ] **Step 5: 提交**

```bash
git add frontend/
git commit -m "feat(frontend): 初始化 Vue 3 + Vite 项目,配置 proxy 与构建输出"
```

---

### Task 13: 前端 API 层与 Pinia Store

**Files:**
- Create: `frontend/src/api/http.js`
- Create: `frontend/src/api/{theme,unit,lesson,progress,voice}.js`
- Create: `frontend/src/stores/progress.js`
- Create: `frontend/src/stores/audio.js`

- [ ] **Step 1: 创建 axios http.js**

```js
// src/api/http.js
import axios from 'axios'

const http = axios.create({
  baseURL: '/api/v1',
  timeout: 15000
})

// 响应拦截器:统一处理 Result 格式
http.interceptors.response.use(
  (response) => {
    const result = response.data
    if (result.code === 200) {
      return result.data
    }
    return Promise.reject(new Error(result.message || '请求失败'))
  },
  (error) => {
    console.error('请求失败:', error.message)
    return Promise.reject(error)
  }
)

export default http
```

- [ ] **Step 2: 创建 API 模块**

```js
// src/api/theme.js
import http from './http'
export const getThemes = () => http.get('/themes')
```

```js
// src/api/unit.js
import http from './http'
export const getUnitsByTheme = (themeId) => http.get(`/units/theme/${themeId}`)
```

```js
// src/api/lesson.js
import http from './http'
export const getLessonsByUnit = (unitId) => http.get(`/lessons/unit/${unitId}`)
export const getLessonById = (id) => http.get(`/lessons/${id}`)
```

```js
// src/api/progress.js
import http from './http'
export const getLessonProgress = (lessonId) =>
  http.get(`/progress/lesson/${lessonId}`)
export const completeLesson = (lessonId, stars, score) =>
  http.post(`/progress/lesson/${lessonId}/complete`, { stars, score })
```

```js
// src/api/voice.js
import http from './http'
export const textToSpeech = (text) =>
  http.post('/voice/tts', { text }, { responseType: 'blob' })
export const scorePronunciation = (audioBlob, text) => {
  const formData = new FormData()
  formData.append('audio', audioBlob, 'record.wav')
  formData.append('text', text)
  return http.post('/voice/score', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
```

- [ ] **Step 3: 创建 Pinia store**

```js
// src/stores/progress.js
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useProgressStore = defineStore('progress', () => {
  const totalStars = ref(0)
  const currentLessonProgress = ref(null)

  function setTotalStars(count) {
    totalStars.value = count
  }
  function setCurrentProgress(progress) {
    currentLessonProgress.value = progress
  }

  return { totalStars, currentLessonProgress, setTotalStars, setCurrentProgress }
})
```

```js
// src/stores/audio.js
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAudioStore = defineStore('audio', () => {
  const isPlaying = ref(false)
  const isRecording = ref(false)

  function setPlaying(val) { isPlaying.value = val }
  function setRecording(val) { isRecording.value = val }

  return { isPlaying, isRecording, setPlaying, setRecording }
})
```

- [ ] **Step 4: 提交**

```bash
git add frontend/src/{api,stores}/
git commit -m "feat(frontend): 新增 API 层与 Pinia store"
```

---

## Phase G: 前端组件

### Task 14: StarBar 与 AudioButton + useTts

**Files:**
- Create: `frontend/src/components/StarBar.vue`
- Create: `frontend/src/components/AudioButton.vue`
- Create: `frontend/src/composables/useTts.js`

- [ ] **Step 1: 创建 StarBar.vue**

```vue
<!-- src/components/StarBar.vue -->
<script setup>
import { computed } from 'vue'

const props = defineProps({
  stars: { type: Number, default: 0 },
  max: { type: Number, default: 3 }
})

const displayStars = computed(() => {
  const arr = []
  for (let i = 0; i < props.max; i++) {
    arr.push(i < props.stars ? '⭐' : '☆')
  }
  return arr
})
</script>

<template>
  <span class="star-bar">{{ displayStars.join('') }}</span>
</template>

<style scoped>
.star-bar {
  font-size: 1.5rem;
  letter-spacing: 4px;
}
</style>
```

- [ ] **Step 2: 创建 useTts composable**

```js
// src/composables/useTts.js
import { ref } from 'vue'
import { textToSpeech } from '../api/voice'

export function useTts() {
  const audioUrl = ref(null)
  const isLoading = ref(false)

  async function play(text) {
    if (isLoading.value) return
    isLoading.value = true
    try {
      const blob = await textToSpeech(text)
      if (audioUrl.value) URL.revokeObjectURL(audioUrl.value)
      audioUrl.value = URL.createObjectURL(blob)
      const audio = new Audio(audioUrl.value)
      await audio.play()
    } catch (e) {
      console.error('TTS 播放失败:', e)
      alert('发音加载失败,请重试')
    } finally {
      isLoading.value = false
    }
  }

  return { audioUrl, isLoading, play }
}
```

- [ ] **Step 3: 创建 AudioButton.vue**

```vue
<!-- src/components/AudioButton.vue -->
<script setup>
import { useTts } from '../composables/useTts'

const props = defineProps({
  text: { type: String, required: true }
})

const { isLoading, play } = useTts()
</script>

<template>
  <button class="audio-btn" :disabled="isLoading" @click="play(props.text)">
    <span v-if="!isLoading">🔊 听发音</span>
    <span v-else>加载中...</span>
  </button>
</template>

<style scoped>
.audio-btn {
  padding: 12px 24px;
  font-size: 1rem;
  border: none;
  border-radius: 24px;
  background: #4CAF50;
  color: white;
  cursor: pointer;
  transition: background 0.2s;
}
.audio-btn:hover { background: #43A047; }
.audio-btn:disabled { background: #9E9E9E; cursor: not-allowed; }
</style>
```

- [ ] **Step 4: 提交**

```bash
git add frontend/src/components/{StarBar,AudioButton}.vue frontend/src/composables/useTts.js
git commit -m "feat(frontend): 新增 StarBar 与 AudioButton 组件"
```

---

### Task 15: RecordButton + useRecorder

**Files:**
- Create: `frontend/src/composables/useRecorder.js`
- Create: `frontend/src/components/RecordButton.vue`

- [ ] **Step 1: 创建 useRecorder composable(wav 16k 16bit mono)**

```js
// src/composables/useRecorder.js
import { ref } from 'vue'

export function useRecorder() {
  const isRecording = ref(false)
  let mediaRecorder = null
  let audioChunks = []
  let audioContext = null

  async function start() {
    if (isRecording.value) return
    const stream = await navigator.mediaDevices.getUserMedia({
      audio: {
        sampleRate: 16000,
        channelCount: 1,
        echoCancellation: true
      }
    })
    audioContext = new AudioContext({ sampleRate: 16000 })
    mediaRecorder = new MediaRecorder(stream)
    audioChunks = []

    mediaRecorder.ondataavailable = (e) => {
      if (e.data.size > 0) audioChunks.push(e.data)
    }

    mediaRecorder.onstop = async () => {
      // 转换为 wav 16k 16bit mono
      const blob = new Blob(audioChunks, { type: 'audio/webm' })
      const arrayBuffer = await blob.arrayBuffer()
      const audioBuffer = await audioContext.decodeAudioData(arrayBuffer)
      const wavBlob = encodeWav(audioBuffer)
      stream.getTracks().forEach(t => t.stop())
      return wavBlob
    }

    mediaRecorder.start()
    isRecording.value = true
  }

  function stop() {
    return new Promise((resolve) => {
      if (!mediaRecorder || !isRecording.value) {
        resolve(null)
        return
      }
      const originalHandler = mediaRecorder.onstop
      mediaRecorder.onstop = async (e) => {
        const blob = new Blob(audioChunks, { type: 'audio/webm' })
        const arrayBuffer = await blob.arrayBuffer()
        const audioBuffer = await audioContext.decodeAudioData(arrayBuffer)
        const wavBlob = encodeWav(audioBuffer)
        mediaRecorder.stream.getTracks().forEach(t => t.stop())
        isRecording.value = false
        resolve(wavBlob)
      }
      mediaRecorder.stop()
    })
  }

  // 编码为 wav 16k 16bit mono
  function encodeWav(audioBuffer) {
    const numChannels = 1
    const sampleRate = 16000
    const bitDepth = 16
    const samples = audioBuffer.getChannelData(0)
    const length = samples.length * 2
    const buffer = new ArrayBuffer(44 + length)
    const view = new DataView(buffer)

    // WAV 头
    writeString(view, 0, 'RIFF')
    view.setUint32(4, 36 + length, true)
    writeString(view, 8, 'WAVE')
    writeString(view, 12, 'fmt ')
    view.setUint32(16, 16, true)
    view.setUint16(20, 1, true)
    view.setUint16(22, numChannels, true)
    view.setUint32(24, sampleRate, true)
    view.setUint32(28, sampleRate * numChannels * 2, true)
    view.setUint16(32, numChannels * 2, true)
    view.setUint16(34, bitDepth, true)
    writeString(view, 36, 'data')
    view.setUint32(40, length, true)

    // PCM 样本
    let offset = 44
    for (let i = 0; i < samples.length; i++) {
      const s = Math.max(-1, Math.min(1, samples[i]))
      view.setInt16(offset, s < 0 ? s * 0x8000 : s * 0x7FFF, true)
      offset += 2
    }
    return new Blob([buffer], { type: 'audio/wav' })
  }

  function writeString(view, offset, str) {
    for (let i = 0; i < str.length; i++) {
      view.setUint8(offset + i, str.charCodeAt(i))
    }
  }

  return { isRecording, start, stop }
}
```

- [ ] **Step 2: 创建 RecordButton.vue**

```vue
<!-- src/components/RecordButton.vue -->
<script setup>
import { ref } from 'vue'
import { useRecorder } from '../composables/useRecorder'

const emit = defineEmits(['recorded'])
const { isRecording, start, stop } = useRecorder()
const errorMessage = ref('')

async function handleClick() {
  errorMessage.value = ''
  if (!isRecording.value) {
    try {
      await start()
    } catch (e) {
      errorMessage.value = '请允许麦克风权限才能跟读'
      console.error(e)
    }
  } else {
    const wavBlob = await stop()
    if (wavBlob) emit('recorded', wavBlob)
  }
}
</script>

<template>
  <div>
    <button class="record-btn" :class="{ recording: isRecording }" @click="handleClick">
      <span v-if="!isRecording">🎤 跟读</span>
      <span v-else>⏹ 停止</span>
    </button>
    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
  </div>
</template>

<style scoped>
.record-btn {
  padding: 12px 24px;
  font-size: 1rem;
  border: none;
  border-radius: 24px;
  background: #FF9800;
  color: white;
  cursor: pointer;
}
.record-btn.recording {
  background: #f44336;
  animation: pulse 1s infinite;
}
.error {
  color: #f44336;
  font-size: 0.85rem;
  margin-top: 8px;
}
@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.6; }
}
</style>
```

- [ ] **Step 3: 提交**

```bash
git add frontend/src/composables/useRecorder.js frontend/src/components/RecordButton.vue
git commit -m "feat(frontend): 新增录音组件(wav 16k 16bit mono)"
```

---

## Phase H: 前端页面与路由

### Task 16: Vue Router 配置

**Files:**
- Create: `frontend/src/router/index.js`
- Modify: `frontend/src/main.js`
- Modify: `frontend/src/App.vue`

- [ ] **Step 1: 创建路由配置**

```js
// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', name: 'home', component: () => import('../views/HomeView.vue') },
  { path: '/theme/:themeId', name: 'theme', component: () => import('../views/ThemeView.vue') },
  { path: '/unit/:unitId', name: 'unit', component: () => import('../views/UnitView.vue') },
  { path: '/lesson/:lessonId', name: 'lesson', component: () => import('../views/LessonView.vue') }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
```

- [ ] **Step 2: 修改 main.js**

```js
// src/main.js
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './style.css'

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.mount('#app')
```

- [ ] **Step 3: 修改 App.vue**

```vue
<!-- src/App.vue -->
<script setup>
</script>

<template>
  <div class="app-container">
    <router-view />
  </div>
</template>

<style>
.app-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 16px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
}
</style>
```

- [ ] **Step 4: 创建简单 style.css**

```css
/* src/style.css */
* { box-sizing: border-box; margin: 0; padding: 0; }
body { background: #f5f5f5; color: #333; }
button { font-family: inherit; }
```

- [ ] **Step 5: 提交**

```bash
git add frontend/src/router/ frontend/src/main.js frontend/src/App.vue frontend/src/style.css
git commit -m "feat(frontend): 配置 Vue Router 与应用入口"
```

---

### Task 17: HomeView 首页

**Files:**
- Create: `frontend/src/views/HomeView.vue`

- [ ] **Step 1: 创建 HomeView**

```vue
<!-- src/views/HomeView.vue -->
<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getThemes } from '../api/theme'

const router = useRouter()
const themes = ref([])

onMounted(async () => {
  try {
    themes.value = await getThemes()
  } catch (e) {
    console.error('加载主题失败:', e)
  }
})
</script>

<template>
  <div class="home">
    <header class="header">
      <div class="avatar">👧</div>
      <div class="user-info">
        <h2>小朋友</h2>
        <p>欢迎来学英语!</p>
      </div>
    </header>

    <h2 class="section-title">选择主题</h2>
    <div class="theme-grid">
      <div
        v-for="theme in themes"
        :key="theme.id"
        class="theme-card"
        :class="{ locked: theme.isLocked }"
        @click="!theme.isLocked && router.push(`/theme/${theme.id}`)"
      >
        <div class="theme-icon">
          {{ theme.id === 1 ? '🍎' : '🚗' }}
        </div>
        <h3>{{ theme.name }}</h3>
        <p v-if="theme.isLocked">🔒 待解锁</p>
        <p v-else>点击进入 →</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border-radius: 16px;
  margin-bottom: 24px;
}
.avatar { font-size: 3rem; }
.theme-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}
.theme-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  text-align: center;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}
.theme-card:hover { transform: translateY(-4px); box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
.theme-card.locked { opacity: 0.6; cursor: not-allowed; }
.theme-icon { font-size: 3rem; margin-bottom: 8px; }
.section-title { margin-bottom: 12px; color: #555; }
</style>
```

- [ ] **Step 2: 提交**

```bash
git add frontend/src/views/HomeView.vue
git commit -m "feat(frontend): 新增首页 HomeView"
```

---

### Task 18: ThemeView 单元列表

**Files:**
- Create: `frontend/src/views/ThemeView.vue`

- [ ] **Step 1: 创建 ThemeView**

```vue
<!-- src/views/ThemeView.vue -->
<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getUnitsByTheme } from '../api/unit'

const route = useRoute()
const router = useRouter()
const units = ref([])

onMounted(async () => {
  const themeId = route.params.themeId
  try {
    units.value = await getUnitsByTheme(themeId)
  } catch (e) {
    console.error('加载单元失败:', e)
  }
})
</script>

<template>
  <div class="theme-view">
    <button class="back-btn" @click="router.push('/')">← 返回</button>
    <h1>🚗 交通工具乐园</h1>
    <div class="unit-list">
      <div
        v-for="unit in units"
        :key="unit.id"
        class="unit-card"
        :class="{ locked: unit.isLocked }"
        @click="!unit.isLocked && router.push(`/unit/${unit.id}`)"
      >
        <div class="unit-header">
          <h2>{{ unit.name }}</h2>
          <span v-if="unit.isLocked">🔒</span>
        </div>
        <p class="progress-text">
          已完成 {{ unit.completedLessons }} / {{ unit.totalLessons }} 课
        </p>
        <div class="progress-bar">
          <div class="progress-fill"
               :style="{ width: (unit.totalLessons ? (unit.completedLessons / unit.totalLessons * 100) : 0) + '%' }">
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.back-btn {
  background: none; border: none; font-size: 1rem;
  color: #667eea; cursor: pointer; margin-bottom: 12px;
}
h1 { text-align: center; margin-bottom: 24px; }
.unit-list { display: flex; flex-direction: column; gap: 16px; }
.unit-card {
  background: white; border-radius: 16px; padding: 20px;
  cursor: pointer; transition: transform 0.2s;
}
.unit-card:hover { transform: translateX(4px); }
.unit-card.locked { opacity: 0.6; cursor: not-allowed; }
.unit-header { display: flex; justify-content: space-between; align-items: center; }
.progress-text { color: #888; font-size: 0.9rem; margin: 8px 0; }
.progress-bar {
  height: 8px; background: #e0e0e0; border-radius: 4px; overflow: hidden;
}
.progress-fill {
  height: 100%; background: #4CAF50; transition: width 0.3s;
}
</style>
```

- [ ] **Step 2: 提交**

```bash
git add frontend/src/views/ThemeView.vue
git commit -m "feat(frontend): 新增 ThemeView 单元列表页"
```

---

### Task 19: UnitView 课时列表

**Files:**
- Create: `frontend/src/views/UnitView.vue`

- [ ] **Step 1: 创建 UnitView**

```vue
<!-- src/views/UnitView.vue -->
<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getLessonsByUnit } from '../api/lesson'

const route = useRoute()
const router = useRouter()
const lessons = ref([])

onMounted(async () => {
  const unitId = route.params.unitId
  try {
    lessons.value = await getLessonsByUnit(unitId)
  } catch (e) {
    console.error('加载课时失败:', e)
  }
})
</script>

<template>
  <div class="unit-view">
    <button class="back-btn" @click="router.back()">← 返回</button>
    <h1>课时列表</h1>
    <div class="lesson-list">
      <div
        v-for="(lesson, index) in lessons"
        :key="lesson.id"
        class="lesson-card"
        :class="{ locked: index > 0 }"
        @click="index === 0 && router.push(`/lesson/${lesson.id}`)"
      >
        <div class="lesson-num">{{ index + 1 }}</div>
        <div class="lesson-info">
          <h3>{{ lesson.name }}</h3>
          <p>{{ lesson.type === 'WORD' ? '单词课' : '句型课' }}</p>
        </div>
        <span v-if="index === 0" class="status">▶</span>
        <span v-else class="status">🔒</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.back-btn { background: none; border: none; font-size: 1rem; color: #667eea; cursor: pointer; margin-bottom: 12px; }
h1 { margin-bottom: 24px; }
.lesson-list { display: flex; flex-direction: column; gap: 12px; }
.lesson-card {
  display: flex; align-items: center; gap: 16px;
  background: white; border-radius: 12px; padding: 16px;
  cursor: pointer; transition: transform 0.2s;
}
.lesson-card:hover { transform: translateX(4px); }
.lesson-card.locked { opacity: 0.6; cursor: not-allowed; }
.lesson-num {
  width: 40px; height: 40px; border-radius: 50%;
  background: #667eea; color: white; display: flex;
  align-items: center; justify-content: center; font-weight: bold;
}
.lesson-info { flex: 1; }
.lesson-info h3 { margin-bottom: 4px; }
.lesson-info p { color: #888; font-size: 0.85rem; }
.status { font-size: 1.5rem; }
</style>
```

- [ ] **Step 2: 提交**

```bash
git add frontend/src/views/UnitView.vue
git commit -m "feat(frontend): 新增 UnitView 课时列表页"
```

---

### Task 20: LessonView 核心学习页

**Files:**
- Create: `frontend/src/views/LessonView.vue`

- [ ] **Step 1: 创建 LessonView**

```vue
<!-- src/views/LessonView.vue -->
<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getLessonById } from '../api/lesson'
import { completeLesson } from '../api/progress'
import { scorePronunciation } from '../api/voice'
import StarBar from '../components/StarBar.vue'
import AudioButton from '../components/AudioButton.vue'
import RecordButton from '../components/RecordButton.vue'

const route = useRoute()
const router = useRouter()
const lesson = ref(null)
const currentIndex = ref(0)
const currentItem = computed(() => {
  if (!lesson.value || !lesson.value.content) return null
  const items = lesson.value.content.items
  return items[currentIndex.value] || items[0]
})
const currentScore = ref(null)
const currentStars = ref(0)
const isComplete = ref(false)
const isScoring = ref(false)
const scoreMessage = ref('')

onMounted(async () => {
  const lessonId = route.params.lessonId
  try {
    lesson.value = await getLessonById(lessonId)
  } catch (e) {
    console.error('加载课时失败:', e)
  }
})

async function handleRecorded(wavBlob) {
  if (!currentItem.value) return
  isScoring.value = true
  scoreMessage.value = '评分中...'
  try {
    const text = currentItem.value.word || currentItem.value.sentence
    const result = await scorePronunciation(wavBlob, text)
    currentScore.value = result.score
    currentStars.value = result.score >= 80 ? 3 : result.score >= 60 ? 2 : result.score >= 40 ? 1 : 0
    scoreMessage.value = result.feedback
  } catch (e) {
    scoreMessage.value = '评分失败,请重试'
    console.error(e)
  } finally {
    isScoring.value = false
  }
}

function nextItem() {
  const items = lesson.value.content.items
  if (currentIndex.value < items.length - 1) {
    currentIndex.value++
    currentScore.value = null
    currentStars.value = 0
    scoreMessage.value = ''
  } else {
    isComplete.value = true
  }
}

async function finishLesson() {
  try {
    await completeLesson(route.params.lessonId, currentStars.value, currentScore.value || 0)
    alert('恭喜完成本课!')
    router.back()
  } catch (e) {
    console.error('保存进度失败:', e)
    alert('保存失败,请重试')
  }
}
</script>

<template>
  <div class="lesson-view" v-if="lesson">
    <div class="top-bar">
      <button class="back-btn" @click="router.back()">← 返回</button>
      <StarBar :stars="currentStars" />
    </div>

    <div v-if="!isComplete && currentItem" class="card-area">
      <div class="item-card">
        <div class="emoji">{{ currentItem.emoji }}</div>
        <h1 class="word">{{ currentItem.word || currentItem.sentence }}</h1>
        <p v-if="currentItem.phonetic" class="phonetic">{{ currentItem.phonetic }}</p>
        <p class="translation">{{ currentItem.translation }}</p>
      </div>

      <div class="actions">
        <AudioButton :text="currentItem.word || currentItem.sentence" />
        <RecordButton @recorded="handleRecorded" />
      </div>

      <div v-if="currentScore !== null" class="score-area">
        <p class="score">{{ currentScore }} 分</p>
        <p class="feedback">{{ scoreMessage }}</p>
      </div>
      <div v-else-if="isScoring" class="score-area">
        <p class="feedback">{{ scoreMessage }}</p>
      </div>

      <button class="next-btn" @click="nextItem">
        {{ currentIndex < lesson.content.items.length - 1 ? '下一步 →' : '完成本课' }}
      </button>
    </div>

    <div v-else class="complete-area">
      <h2>🎉 太棒了!</h2>
      <p>你完成了《{{ lesson.name }}》</p>
      <StarBar :stars="currentStars" />
      <button class="finish-btn" @click="finishLesson">完成</button>
    </div>
  </div>
</template>

<style scoped>
.top-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.back-btn { background: none; border: none; font-size: 1rem; color: #667eea; cursor: pointer; }
.item-card {
  background: white; border-radius: 24px; padding: 40px;
  text-align: center; margin-bottom: 24px;
}
.emoji { font-size: 5rem; margin-bottom: 16px; }
.word { font-size: 2.5rem; margin-bottom: 8px; color: #333; }
.phonetic { color: #888; font-size: 1.2rem; margin-bottom: 8px; }
.translation { color: #555; font-size: 1.1rem; }
.actions { display: flex; gap: 16px; justify-content: center; margin-bottom: 24px; }
.score-area {
  text-align: center; background: #fff3e0; border-radius: 16px;
  padding: 16px; margin-bottom: 24px;
}
.score { font-size: 2rem; font-weight: bold; color: #FF9800; }
.feedback { color: #666; }
.next-btn {
  width: 100%; padding: 16px; font-size: 1.1rem;
  border: none; border-radius: 24px; background: #667eea;
  color: white; cursor: pointer;
}
.complete-area { text-align: center; padding: 40px; }
.finish-btn {
  margin-top: 24px; padding: 16px 48px; font-size: 1.1rem;
  border: none; border-radius: 24px; background: #4CAF50;
  color: white; cursor: pointer;
}
</style>
```

- [ ] **Step 2: 启动前后端联调**

```bash
# 终端 1
cd backend && ./run.sh
# 终端 2
cd frontend && npm run dev
```
访问 `http://localhost:5173` → 点击主题 → 单元 → 课时 → 测试听发音和录音

- [ ] **Step 3: 提交**

```bash
git add frontend/src/views/LessonView.vue
git commit -m "feat(frontend): 新增核心学习页 LessonView(听读+评分+完成)"
```

---

## Phase I: 集成与测试

### Task 21: 后端 ProgressService 单元测试

**Files:**
- Create: `backend/src/test/java/com/englishapp/service/ProgressServiceImplTest.java`

- [ ] **Step 1: 编写测试**

```java
package com.englishapp.service;

import com.englishapp.domain.Lesson;
import com.englishapp.domain.UserProgress;
import com.englishapp.domain.enums.ProgressStatus;
import com.englishapp.dto.CompleteRequest;
import com.englishapp.dto.CompleteResponse;
import com.englishapp.dto.ProgressDto;
import com.englishapp.repository.LessonRepository;
import com.englishapp.repository.UserProgressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ProgressServiceImpl 单元测试
 * @author englishapp
 * @since 1.0.0
 */
class ProgressServiceImplTest {

    @Mock
    private UserProgressRepository userProgressRepository;
    @Mock
    private LessonRepository lessonRepository;

    @InjectMocks
    private ProgressServiceImpl progressService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * 首课无进度记录时应返回 UNLOCKED
     */
    @Test
    void should_returnUnlocked_when_firstLessonAndNoProgress() {
        Lesson lesson = new Lesson();
        lesson.setId(2);
        lesson.setUnitId(4);
        lesson.setSortOrder(1);

        Lesson lesson2 = new Lesson();
        lesson2.setId(3);
        lesson2.setUnitId(4);
        lesson2.setSortOrder(2);

        when(userProgressRepository.findByUserIdAndLessonId(1, 2))
                .thenReturn(Optional.empty());
        when(lessonRepository.findById(2)).thenReturn(Optional.of(lesson));
        when(lessonRepository.findByUnitIdOrderBySortOrderAsc(4))
                .thenReturn(List.of(lesson, lesson2));

        ProgressDto dto = progressService.getLessonProgress(2, 1);

        assertEquals("UNLOCKED", dto.getStatus());
        assertEquals(0, dto.getStars());
    }

    /**
     * 完成课时应解锁下一课
     */
    @Test
    void should_unlockNextLesson_when_completeCurrent() {
        Lesson current = new Lesson();
        current.setId(2);
        current.setUnitId(4);
        current.setSortOrder(1);

        Lesson next = new Lesson();
        next.setId(3);
        next.setUnitId(4);
        next.setSortOrder(2);

        UserProgress existing = new UserProgress();
        existing.setUserId(1);
        existing.setLessonId(2);
        existing.setStatus(ProgressStatus.UNLOCKED);
        existing.setStars(0);
        existing.setScore(0);

        CompleteRequest request = new CompleteRequest();
        request.setStars(3);
        request.setScore(85);

        when(userProgressRepository.findByUserIdAndLessonId(1, 2))
                .thenReturn(Optional.of(existing));
        when(lessonRepository.findById(2)).thenReturn(Optional.of(current));
        when(lessonRepository.findByUnitIdOrderBySortOrderAsc(4))
                .thenReturn(List.of(current, next));
        when(userProgressRepository.findByUserIdAndLessonId(1, 3))
                .thenReturn(Optional.empty());

        CompleteResponse response = progressService.completeLesson(2, request, 1);

        assertEquals(3, response.getNextLessonId());
        assertTrue(response.getUnlocked());
        verify(userProgressRepository).save(existing);
        assertEquals(ProgressStatus.COMPLETED, existing.getStatus());
    }

    /**
     * 重复挑战应取最高分
     */
    @Test
    void should_keepHighestScore_when_retryChallenge() {
        Lesson current = new Lesson();
        current.setId(2);
        current.setUnitId(4);
        current.setSortOrder(1);

        UserProgress existing = new UserProgress();
        existing.setUserId(1);
        existing.setLessonId(2);
        existing.setStatus(ProgressStatus.COMPLETED);
        existing.setStars(3);
        existing.setScore(90);

        CompleteRequest request = new CompleteRequest();
        request.setStars(2);
        request.setScore(75);

        when(userProgressRepository.findByUserIdAndLessonId(1, 2))
                .thenReturn(Optional.of(existing));
        when(lessonRepository.findById(2)).thenReturn(Optional.of(current));
        when(lessonRepository.findByUnitIdOrderBySortOrderAsc(4))
                .thenReturn(List.of(current));

        progressService.completeLesson(2, request, 1);

        // 应保留 90 而非 75
        assertEquals(90, existing.getScore());
        assertEquals(3, existing.getStars());
    }
}
```

- [ ] **Step 2: 运行测试**

```bash
cd backend && ./mvnw test -Dtest=ProgressServiceImplTest
```
Expected: 3 个测试全部 PASS

- [ ] **Step 3: 提交**

```bash
git add backend/src/test/java/com/englishapp/service/ProgressServiceImplTest.java
git commit -m "test(progress): 新增进度业务单元测试(解锁/最高分/首课)"
```

---

### Task 22: 端到端集成验证

**Files:**
- 无新增文件(手动验证)

- [ ] **Step 1: 启动后端**

```bash
cd backend && ./run.sh
```

- [ ] **Step 2: 启动前端**

```bash
cd frontend && npm run dev
```

- [ ] **Step 3: 手动验证完整链路**

在浏览器 `http://localhost:5173`:
1. 首页显示"小朋友"和"交通工具乐园"主题卡
2. 点击进入,看到 3 个单元
3. 点击"陆地交通",看到 4 个课时,第一课时可点击
4. 进入"认识汽车",看到 car/bus/bike/truck 卡片
5. 点 🔊 听发音(确认百度 TTS 返回音频)
6. 点 🎤 跟读(确认录音+评分返回分数)
7. 点"下一步"切换下一个单词
8. 点"完成本课",进度保存
9. 返回课时列表,下一课时已解锁

- [ ] **Step 4: 构建部署验证**

```bash
cd frontend && npm run build
# 确认产物生成在 backend/src/main/resources/static/
ls ../backend/src/main/resources/static/
cd ../backend && ./mvnw spring-boot:run
# 访问 http://localhost:8080 应直接显示前端页面
```

- [ ] **Step 5: 提交最终版本**

```bash
git add -A
git commit -m "feat: 网页版英语学习应用完成(端到端验证通过)

- 后端:单元/进度/语音评测 API + 百度 TTS/评测集成
- 前端:Vue 3 + Vite 完整应用
- 数据:交通工具主题 10 课时
- 部署:Vue 构建嵌入 Spring Boot static"
```

---

## 实现顺序建议

**推荐执行路径**(依赖顺序):
1. Task 1 → Task 3(后端基础设施)
2. Task 2(数据迁移)
3. Task 5(单元模块)
4. Task 6-8(进度模块)
5. Task 9-11(语音评测)
6. Task 12-13(前端脚手架 + API 层)
7. Task 14-15(前端组件)
8. Task 16-20(前端页面)
9. Task 21(后端测试)
10. Task 22(端到端验证)

**预计总工时**: 8-12 小时(熟练开发者)。可使用 subagent 并行:后端(Task 1-11)与前端(Task 12-20)部分可并行,前端依赖后端 API 但 API 模块可先 mock。

---

## 验收标准(对应 Spec)

- [ ] 孩子能在浏览器访问 `http://localhost:8080` 看到首页
- [ ] 能进入"交通工具乐园"主题,看到 3 个单元
- [ ] 能完成"认识汽车"课时:听发音 + 录音跟读 + 看到评分 + 看到星星
- [ ] 完成课时后,下一课时自动解锁
- [ ] 进度在刷新页面后依然保留(后端持久化)
- [ ] 孩子能独立走完 1 个完整课时,无需大人讲解
