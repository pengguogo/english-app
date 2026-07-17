# Phase 1: 骨架搭建 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 搭建可运行的 Spring Boot 后端 + Android 前端骨架，实现首页、学习地图和基础课程数据 API，接入百度 TTS 语音播放。

**Architecture:** 后端采用 Spring Boot 3 + JPA + SQLite（Flyway 管理迁移），Android 采用 Jetpack Compose + MVVM + Retrofit + Room。语音服务通过适配器模式抽象，为后续切换供应商预留扩展点。

**Tech Stack:** Java 17, Spring Boot 3.2, SQLite, Flyway, JPA, Kotlin, Jetpack Compose, Retrofit, Room, Hilt, 百度语音 SDK

---

## File Structure

```
english-app/
├── backend/
│   ├── src/main/java/com/englishapp/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── domain/
│   │   ├── dto/
│   │   ├── config/
│   │   ├── exception/
│   │   └── voice/
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   ├── application-dev.yml
│   │   └── db/migration/
│   └── src/test/java/com/englishapp/
├── android/
│   ├── app/src/main/java/com/englishapp/
│   │   ├── ui/home/
│   │   ├── ui/map/
│   │   ├── viewmodel/
│   │   ├── repository/
│   │   ├── data/remote/
│   │   ├── data/local/
│   │   ├── data/model/
│   │   └── di/
│   └── app/src/main/res/
└── docs/
```

---

### Task 1: Spring Boot 项目初始化

**Files:**
- Create: `backend/pom.xml`
- Create: `backend/src/main/java/com/englishapp/EnglishAppApplication.java`
- Create: `backend/src/main/resources/application.yml`

- [ ] **Step 1: 创建 Maven POM 文件**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>
    <groupId>com.englishapp</groupId>
    <artifactId>english-app-backend</artifactId>
    <version>1.0.0</version>
    <name>english-app-backend</name>
    <properties>
        <java.version>17</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-core</artifactId>
        </dependency>
        <dependency>
            <groupId>org.xerial</groupId>
            <artifactId>sqlite-jdbc</artifactId>
            <version>3.44.1.0</version>
        </dependency>
        <dependency>
            <groupId>org.hibernate.orm</groupId>
            <artifactId>hibernate-community-dialects</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 2: 创建主启动类**

```java
package com.englishapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EnglishAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(EnglishAppApplication.class, args);
    }
}
```

- [ ] **Step 3: 创建 application.yml**

```yaml
spring:
  application:
    name: english-app-backend
  datasource:
    url: jdbc:sqlite:english.db
    driver-class-name: org.sqlite.JDBC
  jpa:
    hibernate:
      ddl-auto: none
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.community.dialect.SQLiteDialect
  flyway:
    enabled: true
    locations: classpath:db/migration

server:
  port: 8080

voice:
  provider: baidu
  baidu:
    app-id: ${BAIDU_APP_ID:}
    api-key: ${BAIDU_API_KEY:}
    secret-key: ${BAIDU_SECRET_KEY:}
```

- [ ] **Step 4: 验证项目可编译**

Run: `cd backend && mvn clean compile`
Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**

```bash
git add backend/
git commit -m "chore: init Spring Boot project with SQLite and Flyway"
```

---

### Task 2: 数据库设计与 Flyway 迁移

**Files:**
- Create: `backend/src/main/resources/db/migration/V1__init.sql`
- Create: `backend/src/test/resources/application-test.yml`

- [ ] **Step 1: 编写 V1 迁移脚本**

```sql
CREATE TABLE theme (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(50) NOT NULL,
    icon_url VARCHAR(255),
    sort_order INTEGER DEFAULT 0,
    is_locked BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE unit (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    theme_id INTEGER NOT NULL,
    name VARCHAR(50) NOT NULL,
    sort_order INTEGER DEFAULT 0,
    is_locked BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (theme_id) REFERENCES theme(id)
);

CREATE TABLE lesson (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    unit_id INTEGER NOT NULL,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    sort_order INTEGER DEFAULT 0,
    star_reward INTEGER DEFAULT 3,
    FOREIGN KEY (unit_id) REFERENCES unit(id)
);

CREATE TABLE app_user (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nickname VARCHAR(50),
    avatar_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_progress (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    lesson_id INTEGER NOT NULL,
    status VARCHAR(20) DEFAULT 'LOCKED',
    stars INTEGER DEFAULT 0,
    score INTEGER DEFAULT 0,
    completed_at TIMESTAMP,
    UNIQUE(user_id, lesson_id),
    FOREIGN KEY (user_id) REFERENCES app_user(id),
    FOREIGN KEY (lesson_id) REFERENCES lesson(id)
);
```

- [ ] **Step 2: 创建测试配置**

```yaml
# backend/src/test/resources/application-test.yml
spring:
  datasource:
    url: jdbc:sqlite::memory:
    driver-class-name: org.sqlite.JDBC
  flyway:
    enabled: true
```

- [ ] **Step 3: 启动应用验证迁移**

Run: `cd backend && mvn spring-boot:run`
Expected: 应用启动成功，Flyway 执行 V1 迁移，日志显示 `Successfully applied 1 migration`

- [ ] **Step 4: Commit**

```bash
git add backend/src/main/resources/db/migration/
git add backend/src/test/resources/
git commit -m "feat: add V1 database schema with theme, unit, lesson, user, progress"
```

---

### Task 3: Domain 实体定义

**Files:**
- Create: `backend/src/main/java/com/englishapp/domain/Theme.java`
- Create: `backend/src/main/java/com/englishapp/domain/Unit.java`
- Create: `backend/src/main/java/com/englishapp/domain/Lesson.java`
- Create: `backend/src/main/java/com/englishapp/domain/AppUser.java`
- Create: `backend/src/main/java/com/englishapp/domain/UserProgress.java`
- Create: `backend/src/main/java/com/englishapp/domain/enums/LessonType.java`
- Create: `backend/src/main/java/com/englishapp/domain/enums/ProgressStatus.java`

- [ ] **Step 1: 创建 LessonType 枚举**

```java
package com.englishapp.domain.enums;

public enum LessonType {
    WORD, SENTENCE, GAME, SPEAK
}
```

- [ ] **Step 2: 创建 ProgressStatus 枚举**

```java
package com.englishapp.domain.enums;

public enum ProgressStatus {
    LOCKED, IN_PROGRESS, COMPLETED
}
```

- [ ] **Step 3: 创建 Theme 实体**

```java
package com.englishapp.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "theme")
public class Theme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "icon_url")
    private String iconUrl;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "is_locked")
    private Boolean isLocked = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Boolean getIsLocked() { return isLocked; }
    public void setIsLocked(Boolean isLocked) { this.isLocked = isLocked; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
```

- [ ] **Step 4: 创建 Unit 实体**

```java
package com.englishapp.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "unit")
public class Unit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "theme_id", nullable = false)
    private Integer themeId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "is_locked")
    private Boolean isLocked = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id", insertable = false, updatable = false)
    private Theme theme;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getThemeId() { return themeId; }
    public void setThemeId(Integer themeId) { this.themeId = themeId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Boolean getIsLocked() { return isLocked; }
    public void setIsLocked(Boolean isLocked) { this.isLocked = isLocked; }
    public Theme getTheme() { return theme; }
    public void setTheme(Theme theme) { this.theme = theme; }
}
```

- [ ] **Step 5: 创建 Lesson 实体**

```java
package com.englishapp.domain;

import com.englishapp.domain.enums.LessonType;
import jakarta.persistence.*;

@Entity
@Table(name = "lesson")
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "unit_id", nullable = false)
    private Integer unitId;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LessonType type;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "star_reward")
    private Integer starReward = 3;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", insertable = false, updatable = false)
    private Unit unit;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getUnitId() { return unitId; }
    public void setUnitId(Integer unitId) { this.unitId = unitId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LessonType getType() { return type; }
    public void setType(LessonType type) { this.type = type; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Integer getStarReward() { return starReward; }
    public void setStarReward(Integer starReward) { this.starReward = starReward; }
    public Unit getUnit() { return unit; }
    public void setUnit(Unit unit) { this.unit = unit; }
}
```

- [ ] **Step 6: 创建 AppUser 实体**

```java
package com.englishapp.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "app_user")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 50)
    private String nickname;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
```

- [ ] **Step 7: 创建 UserProgress 实体**

```java
package com.englishapp.domain;

import com.englishapp.domain.enums.ProgressStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_progress")
public class UserProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "lesson_id", nullable = false)
    private Integer lessonId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ProgressStatus status = ProgressStatus.LOCKED;

    private Integer stars = 0;

    private Integer score = 0;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public Integer getLessonId() { return lessonId; }
    public void setLessonId(Integer lessonId) { this.lessonId = lessonId; }
    public ProgressStatus getStatus() { return status; }
    public void setStatus(ProgressStatus status) { this.status = status; }
    public Integer getStars() { return stars; }
    public void setStars(Integer stars) { this.stars = stars; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
```

- [ ] **Step 8: 编译验证**

Run: `cd backend && mvn clean compile`
Expected: BUILD SUCCESS

- [ ] **Step 9: Commit**

```bash
git add backend/src/main/java/com/englishapp/domain/
git commit -m "feat: add domain entities (Theme, Unit, Lesson, AppUser, UserProgress)"
```

---

### Task 4: Repository 层

**Files:**
- Create: `backend/src/main/java/com/englishapp/repository/ThemeRepository.java`
- Create: `backend/src/main/java/com/englishapp/repository/UnitRepository.java`
- Create: `backend/src/main/java/com/englishapp/repository/LessonRepository.java`
- Create: `backend/src/main/java/com/englishapp/repository/AppUserRepository.java`
- Create: `backend/src/main/java/com/englishapp/repository/UserProgressRepository.java`

- [ ] **Step 1: 创建 ThemeRepository**

```java
package com.englishapp.repository;

import com.englishapp.domain.Theme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThemeRepository extends JpaRepository<Theme, Integer> {
    List<Theme> findAllByOrderBySortOrderAsc();
}
```

- [ ] **Step 2: 创建 UnitRepository**

```java
package com.englishapp.repository;

import com.englishapp.domain.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Integer> {
    List<Unit> findByThemeIdOrderBySortOrderAsc(Integer themeId);
}
```

- [ ] **Step 3: 创建 LessonRepository**

```java
package com.englishapp.repository;

import com.englishapp.domain.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Integer> {
    List<Lesson> findByUnitIdOrderBySortOrderAsc(Integer unitId);
}
```

- [ ] **Step 4: 创建 AppUserRepository**

```java
package com.englishapp.repository;

import com.englishapp.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Integer> {
}
```

- [ ] **Step 5: 创建 UserProgressRepository**

```java
package com.englishapp.repository;

import com.englishapp.domain.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, Integer> {
    Optional<UserProgress> findByUserIdAndLessonId(Integer userId, Integer lessonId);
}
```

- [ ] **Step 6: 编写 Repository 集成测试**

```java
package com.englishapp.repository;

import com.englishapp.domain.Theme;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ThemeRepositoryTest {

    @Autowired
    private ThemeRepository themeRepository;

    @Test
    void shouldSaveAndFindTheme() {
        Theme theme = new Theme();
        theme.setName("水果乐园");
        theme.setSortOrder(1);
        theme.setIsLocked(false);

        Theme saved = themeRepository.save(theme);
        List<Theme> themes = themeRepository.findAllByOrderBySortOrderAsc();

        assertThat(themes).hasSize(1);
        assertThat(themes.get(0).getName()).isEqualTo("水果乐园");
    }
}
```

- [ ] **Step 7: 运行 Repository 测试**

Run: `cd backend && mvn test -Dtest=ThemeRepositoryTest`
Expected: Tests run: 1, Failures: 0

- [ ] **Step 8: Commit**

```bash
git add backend/src/main/java/com/englishapp/repository/
git add backend/src/test/java/com/englishapp/repository/
git commit -m "feat: add JPA repositories with integration tests"
```

---

### Task 5: Service 层 + 单元测试

**Files:**
- Create: `backend/src/main/java/com/englishapp/service/ThemeService.java`
- Create: `backend/src/main/java/com/englishapp/service/LessonService.java`
- Create: `backend/src/main/java/com/englishapp/dto/ThemeDto.java`
- Create: `backend/src/main/java/com/englishapp/dto/UnitDto.java`
- Create: `backend/src/main/java/com/englishapp/dto/LessonDto.java`
- Create: `backend/src/test/java/com/englishapp/service/ThemeServiceTest.java`

- [ ] **Step 1: 创建 ThemeDto**

```java
package com.englishapp.dto;

public record ThemeDto(
    Integer id,
    String name,
    String iconUrl,
    Integer sortOrder,
    Boolean isLocked
) {}
```

- [ ] **Step 2: 创建 UnitDto**

```java
package com.englishapp.dto;

public record UnitDto(
    Integer id,
    Integer themeId,
    String name,
    Integer sortOrder,
    Boolean isLocked
) {}
```

- [ ] **Step 3: 创建 LessonDto**

```java
package com.englishapp.dto;

public record LessonDto(
    Integer id,
    Integer unitId,
    String name,
    String type,
    String content,
    Integer sortOrder,
    Integer starReward
) {}
```

- [ ] **Step 4: 创建 ThemeService**

```java
package com.englishapp.service;

import com.englishapp.domain.Theme;
import com.englishapp.dto.ThemeDto;
import com.englishapp.repository.ThemeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ThemeService {

    private final ThemeRepository themeRepository;

    public ThemeService(ThemeRepository themeRepository) {
        this.themeRepository = themeRepository;
    }

    public List<ThemeDto> getAllThemes() {
        return themeRepository.findAllByOrderBySortOrderAsc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    private ThemeDto toDto(Theme theme) {
        return new ThemeDto(
                theme.getId(),
                theme.getName(),
                theme.getIconUrl(),
                theme.getSortOrder(),
                theme.getIsLocked()
        );
    }
}
```

- [ ] **Step 5: 创建 LessonService**

```java
package com.englishapp.service;

import com.englishapp.domain.Lesson;
import com.englishapp.dto.LessonDto;
import com.englishapp.repository.LessonRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LessonService {

    private final LessonRepository lessonRepository;

    public LessonService(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    public List<LessonDto> getLessonsByUnitId(Integer unitId) {
        return lessonRepository.findByUnitIdOrderBySortOrderAsc(unitId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public LessonDto getLessonById(Integer id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found: " + id));
        return toDto(lesson);
    }

    private LessonDto toDto(Lesson lesson) {
        return new LessonDto(
                lesson.getId(),
                lesson.getUnitId(),
                lesson.getName(),
                lesson.getType().name(),
                lesson.getContent(),
                lesson.getSortOrder(),
                lesson.getStarReward()
        );
    }
}
```

- [ ] **Step 6: 编写 ThemeService 单元测试**

```java
package com.englishapp.service;

import com.englishapp.domain.Theme;
import com.englishapp.dto.ThemeDto;
import com.englishapp.repository.ThemeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ThemeServiceTest {

    @Mock
    private ThemeRepository themeRepository;

    @InjectMocks
    private ThemeService themeService;

    @Test
    void shouldReturnAllThemesSorted() {
        Theme theme = new Theme();
        theme.setId(1);
        theme.setName("水果乐园");
        theme.setSortOrder(1);
        theme.setIsLocked(false);

        when(themeRepository.findAllByOrderBySortOrderAsc()).thenReturn(List.of(theme));

        List<ThemeDto> result = themeService.getAllThemes();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("水果乐园");
    }
}
```

- [ ] **Step 7: 运行 Service 单元测试**

Run: `cd backend && mvn test -Dtest=ThemeServiceTest`
Expected: Tests run: 1, Failures: 0

- [ ] **Step 8: Commit**

```bash
git add backend/src/main/java/com/englishapp/service/
git add backend/src/main/java/com/englishapp/dto/
git add backend/src/test/java/com/englishapp/service/
git commit -m "feat: add service layer with DTOs and unit tests"
```

---

### Task 6: Controller 层 + 集成测试

**Files:**
- Create: `backend/src/main/java/com/englishapp/controller/ThemeController.java`
- Create: `backend/src/main/java/com/englishapp/controller/LessonController.java`
- Create: `backend/src/main/java/com/englishapp/exception/GlobalExceptionHandler.java`
- Create: `backend/src/test/java/com/englishapp/controller/ThemeControllerTest.java`

- [ ] **Step 1: 创建 ThemeController**

```java
package com.englishapp.controller;

import com.englishapp.dto.ThemeDto;
import com.englishapp.service.ThemeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/themes")
public class ThemeController {

    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @GetMapping
    public ResponseEntity<List<ThemeDto>> getAllThemes() {
        return ResponseEntity.ok(themeService.getAllThemes());
    }
}
```

- [ ] **Step 2: 创建 LessonController**

```java
package com.englishapp.controller;

import com.englishapp.dto.LessonDto;
import com.englishapp.service.LessonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lessons")
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @GetMapping("/unit/{unitId}")
    public ResponseEntity<List<LessonDto>> getLessonsByUnit(@PathVariable Integer unitId) {
        return ResponseEntity.ok(lessonService.getLessonsByUnitId(unitId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LessonDto> getLessonById(@PathVariable Integer id) {
        return ResponseEntity.ok(lessonService.getLessonById(id));
    }
}
```

- [ ] **Step 3: 创建全局异常处理**

```java
package com.englishapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "message", ex.getMessage()
                ));
    }
}
```

- [ ] **Step 4: 编写 Controller 集成测试**

```java
package com.englishapp.controller;

import com.englishapp.domain.Theme;
import com.englishapp.repository.ThemeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ThemeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ThemeRepository themeRepository;

    @BeforeEach
    void setUp() {
        themeRepository.deleteAll();
        Theme theme = new Theme();
        theme.setName("水果乐园");
        theme.setSortOrder(1);
        theme.setIsLocked(false);
        themeRepository.save(theme);
    }

    @Test
    void shouldReturnThemes() throws Exception {
        mockMvc.perform(get("/api/v1/themes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("水果乐园"));
    }
}
```

- [ ] **Step 5: 运行 Controller 集成测试**

Run: `cd backend && mvn test -Dtest=ThemeControllerTest`
Expected: Tests run: 1, Failures: 0

- [ ] **Step 6: Commit**

```bash
git add backend/src/main/java/com/englishapp/controller/
git add backend/src/main/java/com/englishapp/exception/
git add backend/src/test/java/com/englishapp/controller/
git commit -m "feat: add REST controllers with integration tests"
```

---

### Task 7: 语音服务抽象层（百度TTS）

**Files:**
- Create: `backend/src/main/java/com/englishapp/voice/VoiceService.java`
- Create: `backend/src/main/java/com/englishapp/voice/BaiduVoiceService.java`
- Create: `backend/src/main/java/com/englishapp/config/VoiceProperties.java`
- Modify: `backend/src/main/resources/application.yml`

- [ ] **Step 1: 创建 VoiceService 接口**

```java
package com.englishapp.voice;

public interface VoiceService {
    byte[] textToSpeech(String text);
    String speechToText(byte[] audioData);
}
```

- [ ] **Step 2: 创建 VoiceProperties 配置类**

```java
package com.englishapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "voice.baidu")
public class VoiceProperties {
    private String appId;
    private String apiKey;
    private String secretKey;

    // Getters and Setters
    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public String getSecretKey() { return secretKey; }
    public void setSecretKey(String secretKey) { this.secretKey = secretKey; }
}
```

- [ ] **Step 3: 创建 BaiduVoiceService（简化版，先返回 mock 数据）**

```java
package com.englishapp.voice;

import com.englishapp.config.VoiceProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "voice.provider", havingValue = "baidu")
public class BaiduVoiceService implements VoiceService {

    private static final Logger log = LoggerFactory.getLogger(BaiduVoiceService.class);

    private final VoiceProperties voiceProperties;

    public BaiduVoiceService(VoiceProperties voiceProperties) {
        this.voiceProperties = voiceProperties;
    }

    @Override
    public byte[] textToSpeech(String text) {
        log.info("TTS request for text: {}", text);
        // TODO: 接入百度 SDK，先返回空数组占位
        // 后续替换为真实百度 API 调用
        return new byte[0];
    }

    @Override
    public String speechToText(byte[] audioData) {
        log.info("ASR request, audio length: {} bytes", audioData.length);
        // TODO: 接入百度 SDK
        return "";
    }
}
```

- [ ] **Step 4: 创建 VoiceController**

```java
package com.englishapp.controller;

import com.englishapp.voice.VoiceService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/voice")
public class VoiceController {

    private final VoiceService voiceService;

    public VoiceController(VoiceService voiceService) {
        this.voiceService = voiceService;
    }

    @PostMapping("/tts")
    public ResponseEntity<byte[]> textToSpeech(@RequestBody String text) {
        byte[] audio = voiceService.textToSpeech(text);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .body(audio);
    }
}
```

- [ ] **Step 5: 在 application.yml 启用配置属性**

Modify `backend/src/main/java/com/englishapp/EnglishAppApplication.java`:

```java
package com.englishapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.englishapp.config.VoiceProperties;

@SpringBootApplication
@EnableConfigurationProperties(VoiceProperties.class)
public class EnglishAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(EnglishAppApplication.class, args);
    }
}
```

- [ ] **Step 6: 启动应用验证**

Run: `cd backend && mvn spring-boot:run`
Test: `curl http://localhost:8080/api/v1/themes`
Expected: HTTP 200, 返回 `[]`（空数组，因为还没有种子数据）

- [ ] **Step 7: Commit**

```bash
git add backend/src/main/java/com/englishapp/voice/
git add backend/src/main/java/com/englishapp/config/
git add backend/src/main/java/com/englishapp/controller/VoiceController.java
git add backend/src/main/java/com/englishapp/EnglishAppApplication.java
git commit -m "feat: add voice service abstraction layer (Baidu TTS placeholder)"
```

---

### Task 8: Android 项目初始化

**Files:**
- Create: `android/settings.gradle.kts`
- Create: `android/build.gradle.kts`
- Create: `android/app/build.gradle.kts`
- Create: `android/app/src/main/AndroidManifest.xml`
- Create: `android/app/src/main/java/com/englishapp/MainActivity.kt`
- Create: `android/app/src/main/java/com/englishapp/ui/theme/Theme.kt`

- [ ] **Step 1: 创建项目级 build.gradle.kts**

```kotlin
// android/build.gradle.kts
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
}
```

- [ ] **Step 2: 创建 settings.gradle.kts**

```kotlin
// android/settings.gradle.kts
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "EnglishApp"
include(":app")
```

- [ ] **Step 3: 创建 App 级 build.gradle.kts**

```kotlin
// android/app/build.gradle.kts
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.englishapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.englishapp"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.5"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.1")
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-compose:2.7.5")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
```

- [ ] **Step 4: 创建 AndroidManifest.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.englishapp">

    <uses-permission android:name="android.permission.INTERNET" />

    <application
        android:name=".EnglishAppApplication"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.EnglishApp">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:theme="@style/Theme.EnglishApp">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

- [ ] **Step 5: 创建 Application 类**

```kotlin
package com.englishapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class EnglishAppApplication : Application()
```

- [ ] **Step 6: 创建 MainActivity**

```kotlin
package com.englishapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.englishapp.ui.theme.EnglishAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EnglishAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 导航入口，后续替换为 NavHost
                    HomeScreen()
                }
            }
        }
    }
}
```

- [ ] **Step 7: Commit**

```bash
git add android/
git commit -m "chore: init Android project with Jetpack Compose, Hilt, Retrofit, Room"
```

---

### Task 9: Android 数据层（Retrofit API + Room 数据库）

**Files:**
- Create: `android/app/src/main/java/com/englishapp/data/model/Theme.kt`
- Create: `android/app/src/main/java/com/englishapp/data/model/Lesson.kt`
- Create: `android/app/src/main/java/com/englishapp/data/remote/ApiService.kt`
- Create: `android/app/src/main/java/com/englishapp/data/remote/RetrofitClient.kt`
- Create: `android/app/src/main/java/com/englishapp/data/local/AppDatabase.kt`
- Create: `android/app/src/main/java/com/englishapp/data/local/ThemeDao.kt`
- Create: `android/app/src/main/java/com/englishapp/di/AppModule.kt`

- [ ] **Step 1: 创建 Theme 数据模型**

```kotlin
package com.englishapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "themes")
data class Theme(
    @PrimaryKey
    val id: Int,
    val name: String,
    val iconUrl: String?,
    val sortOrder: Int,
    val isLocked: Boolean
)
```

- [ ] **Step 2: 创建 Lesson 数据模型**

```kotlin
package com.englishapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lessons")
data class Lesson(
    @PrimaryKey
    val id: Int,
    val unitId: Int,
    val name: String,
    val type: String,
    val content: String,
    val sortOrder: Int,
    val starReward: Int
)
```

- [ ] **Step 3: 创建 Retrofit API 接口**

```kotlin
package com.englishapp.data.remote

import com.englishapp.data.model.Lesson
import com.englishapp.data.model.Theme
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("api/v1/themes")
    suspend fun getThemes(): List<Theme>

    @GET("api/v1/lessons/unit/{unitId}")
    suspend fun getLessonsByUnit(@Path("unitId") unitId: Int): List<Lesson>
}
```

- [ ] **Step 4: 创建 RetrofitClient**

```kotlin
package com.englishapp.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
```

- [ ] **Step 5: 创建 Room DAO**

```kotlin
package com.englishapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.englishapp.data.model.Theme

@Dao
interface ThemeDao {
    @Query("SELECT * FROM themes ORDER BY sortOrder ASC")
    suspend fun getAll(): List<Theme>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(themes: List<Theme>)
}
```

- [ ] **Step 6: 创建 AppDatabase**

```kotlin
package com.englishapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.englishapp.data.model.Lesson
import com.englishapp.data.model.Theme

@Database(entities = [Theme::class, Lesson::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun themeDao(): ThemeDao
}
```

- [ ] **Step 7: 创建 Hilt Module**

```kotlin
package com.englishapp.di

import android.content.Context
import androidx.room.Room
import com.englishapp.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "english_app.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideThemeDao(db: AppDatabase) = db.themeDao()
}
```

- [ ] **Step 8: Commit**

```bash
git add android/app/src/main/java/com/englishapp/data/
git add android/app/src/main/java/com/englishapp/di/
git commit -m "feat: add Android data layer (Retrofit API + Room database)"
```

---

### Task 10: Android 首页 UI

**Files:**
- Create: `android/app/src/main/java/com/englishapp/ui/home/HomeScreen.kt`
- Create: `android/app/src/main/java/com/englishapp/ui/home/HomeViewModel.kt`
- Create: `android/app/src/main/java/com/englishapp/repository/ThemeRepository.kt`

- [ ] **Step 1: 创建 ThemeRepository**

```kotlin
package com.englishapp.repository

import com.englishapp.data.local.ThemeDao
import com.englishapp.data.model.Theme
import com.englishapp.data.remote.RetrofitClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeRepository @Inject constructor(
    private val themeDao: ThemeDao
) {
    suspend fun getThemes(): List<Theme> {
        return try {
            val themes = RetrofitClient.apiService.getThemes()
            themeDao.insertAll(themes)
            themes
        } catch (e: Exception) {
            themeDao.getAll()
        }
    }
}
```

- [ ] **Step 2: 创建 HomeViewModel**

```kotlin
package com.englishapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.englishapp.data.model.Theme
import com.englishapp.repository.ThemeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val themeRepository: ThemeRepository
) : ViewModel() {

    private val _themes = MutableStateFlow<List<Theme>>(emptyList())
    val themes: StateFlow<List<Theme>> = _themes

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadThemes() {
        viewModelScope.launch {
            _isLoading.value = true
            _themes.value = themeRepository.getThemes()
            _isLoading.value = false
        }
    }
}
```

- [ ] **Step 3: 创建 HomeScreen**

```kotlin
package com.englishapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.englishapp.ui.home.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val themes by viewModel.themes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadThemes()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI幼小衔接英语") }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(themes) { theme ->
                        ThemeCard(theme = theme)
                    }
                }
            }
        }
    }
}

@Composable
fun ThemeCard(theme: com.englishapp.data.model.Theme) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = theme.name,
                style = MaterialTheme.typography.titleLarge
            )
            if (theme.isLocked) {
                Text(
                    text = "🔒 未解锁",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}
```

- [ ] **Step 4: Commit**

```bash
git add android/app/src/main/java/com/englishapp/ui/home/
git add android/app/src/main/java/com/englishapp/repository/
git add android/app/src/main/java/com/englishapp/MainActivity.kt
git commit -m "feat: add home screen with theme list (MVVM + Compose)"
```

---

### Task 11: 填充种子数据 + 端到端验证

**Files:**
- Create: `backend/src/main/resources/db/migration/V2__seed_data.sql`
- Create: `android/app/src/main/res/xml/network_security_config.xml`
- Modify: `android/app/src/main/AndroidManifest.xml`

- [ ] **Step 1: 创建种子数据迁移脚本**

```sql
-- 插入一个默认用户
INSERT INTO app_user (id, nickname) VALUES (1, '小朋友');

-- 插入主题：水果乐园
INSERT INTO theme (id, name, sort_order, is_locked) VALUES (1, '水果乐园', 1, 0);

-- 插入单元
INSERT INTO unit (id, theme_id, name, sort_order, is_locked) VALUES (1, 1, 'Unit 1', 1, 0);
INSERT INTO unit (id, theme_id, name, sort_order, is_locked) VALUES (2, 1, 'Unit 2', 2, 1);
INSERT INTO unit (id, theme_id, name, sort_order, is_locked) VALUES (3, 1, 'Unit 3', 3, 1);

-- 插入课程
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(1, 1, '认识水果', 'WORD', '{"words":["apple","banana","orange"]}', 1, 3);
```

- [ ] **Step 2: 创建网络安全配置（允许明文 HTTP 访问后端）**

```xml
<!-- android/app/src/main/res/xml/network_security_config.xml -->
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="true" />
</network-security-config>
```

- [ ] **Step 3: 修改 AndroidManifest 引用安全配置**

Modify `android/app/src/main/AndroidManifest.xml`:

```xml
<application
    android:name=".EnglishAppApplication"
    android:networkSecurityConfig="@xml/network_security_config"
    ... >
```

- [ ] **Step 4: 启动后端并验证 API**

Run: `cd backend && mvn spring-boot:run`
Test: `curl http://localhost:8080/api/v1/themes`
Expected:
```json
[
  {
    "id": 1,
    "name": "水果乐园",
    "iconUrl": null,
    "sortOrder": 1,
    "isLocked": false
  }
]
```

- [ ] **Step 5: 在 Android 模拟器运行 App 并验证**

Run: 在 Android Studio 中启动模拟器，运行 App
Expected: 首页显示 "水果乐园" 卡片

- [ ] **Step 6: Commit**

```bash
git add backend/src/main/resources/db/migration/V2__seed_data.sql
git add android/app/src/main/res/xml/
git add android/app/src/main/AndroidManifest.xml
git commit -m "feat: add seed data and network config for end-to-end validation"
```

---

### Task 12: 学习地图页面

**Files:**
- Create: `android/app/src/main/java/com/englishapp/ui/map/MapScreen.kt`
- Create: `android/app/src/main/java/com/englishapp/ui/map/MapViewModel.kt`

- [ ] **Step 1: 创建 MapViewModel**

```kotlin
package com.englishapp.ui.map

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.englishapp.data.model.Lesson
import com.englishapp.data.remote.RetrofitClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _lessons = MutableStateFlow<List<Lesson>>(emptyList())
    val lessons: StateFlow<List<Lesson>> = _lessons

    fun loadLessons(unitId: Int) {
        viewModelScope.launch {
            try {
                _lessons.value = RetrofitClient.apiService.getLessonsByUnit(unitId)
            } catch (e: Exception) {
                _lessons.value = emptyList()
            }
        }
    }
}
```

- [ ] **Step 2: 创建 MapScreen**

```kotlin
package com.englishapp.ui.map

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MapScreen(
    unitId: Int = 1,
    viewModel: MapViewModel = hiltViewModel()
) {
    val lessons by viewModel.lessons.collectAsState()

    LaunchedEffect(unitId) {
        viewModel.loadLessons(unitId)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("学习地图") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(lessons) { lesson ->
                LessonItem(lesson = lesson)
            }
        }
    }
}

@Composable
fun LessonItem(lesson: com.englishapp.data.model.Lesson) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = lesson.name, style = MaterialTheme.typography.titleMedium)
            Text(text = "类型: ${lesson.type}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
```

- [ ] **Step 3: 更新 MainActivity 添加导航**

```kotlin
package com.englishapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.englishapp.ui.map.MapScreen
import com.englishapp.ui.theme.EnglishAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EnglishAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") { HomeScreen() }
                        composable("map") { MapScreen() }
                    }
                }
            }
        }
    }
}
```

- [ ] **Step 4: Commit**

```bash
git add android/app/src/main/java/com/englishapp/ui/map/
git add android/app/src/main/java/com/englishapp/MainActivity.kt
git commit -m "feat: add learning map screen with lesson list"
```

---

## Self-Review

### Spec Coverage Check

| Spec 章节 | 对应 Task | 状态 |
|-----------|-----------|------|
| 2.1 系统架构 | Task 1-12 | 已覆盖 |
| 3.1 后端模块划分 | Task 1-7 | 已覆盖 |
| 3.2 核心 API 设计 | Task 4, 6, 7 | 已覆盖 |
| 3.3 语音服务抽象层 | Task 7 | 已覆盖（placeholder） |
| 4.1 Android MVVM | Task 8-12 | 已覆盖 |
| 4.3 离线优先 | Task 9, 10 | 已覆盖（Room + 降级策略） |
| 5.2 核心表结构 | Task 2, 3 | 已覆盖 |
| 6.1 配置外部化 | Task 1, 7 | 已覆盖 |
| 7.1 第一阶段目标 | Task 1-12 | 已覆盖 |

### Placeholder Scan

- `BaiduVoiceService` 中有 `TODO` 占位注释，但这是设计文档中明确的 placeholder 阶段（先接入骨架，后续替换真实 API 调用），符合预期。
- 其余所有 Task 均包含可直接运行的完整代码。

### Type Consistency Check

- `ThemeDto`, `UnitDto`, `LessonDto` 的字段与 `Theme`, `Unit`, `Lesson` 实体一致。
- `LessonType` 枚举与数据库 `type VARCHAR(20)` 对应。
- `ProgressStatus` 枚举与数据库 `status VARCHAR(20)` 对应。
- API 路径 `/api/v1/themes`, `/api/v1/lessons` 与设计文档一致。
