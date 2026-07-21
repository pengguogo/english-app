# 单词配图后端托管设计

> 日期：2026-07-21
> 状态：待审阅

## 1. 背景与问题

### 1.1 现状

当前单词配图采用"前端打包资源 + 硬编码映射表"方案，数据流分散在三处：

1. **数据库 `lesson.content`**：每个 item 有 `image` 字段，只存纯 key（如 `"image":"chase"`），不是路径/URL。
2. **后端**：`Lesson.content` 是纯 `String`，JPA 原样存取，DTO 原样透传 JSON 字符串，**完全不解析 image 字段**。
3. **前端 `word-images/index.js`**：48 条硬编码映射 `word → import 的 jpg`，Vite 构建时打成带 hash 的静态资源。`LessonView.vue` 第 333-334 行用 `getWordImage(currentItem.word)` 取图——**用 word 文本去查前端硬编码表**，没用后端 `image` 字段的值。

### 1.2 问题

- 图片实际是前端打包资源，后端 `image` 字段存了 key 但没被真正使用。
- 维护成本高：每加一个单词配图，要改三处（放图片文件 / 改 `index.js` 映射 / 写后端迁移加 image 字段）。
- 逻辑不一致：`image` 字段存图片名（`fire-word`），前端却用 word 文本（`fire`）去查表，两边 key 体系不统一。
- 图片打包进前端 bundle，增大前端体积。

### 1.3 范围确认

经梳理，所有 48 个单词的 `image` 字段已在数据库中就绪：
- V7 迁移：Lesson 32（认识工具 8 词）、Lesson 33（建筑材料 8 词）—— 创建时已带 `image` 字段。
- V9 迁移：Lesson 22（认识狗狗 8 词）、Lesson 23（狗狗的本领 8 词）、Lesson 35（工程家族狗狗 8 词）、Lesson 36（狗狗的工具 8 词）—— 后补 `image` 字段。

**无需新增数据库迁移。**

## 2. 目标

把单词配图从前端打包资源改为后端静态资源托管 + 后端下发完整 URL。

- 数据库存纯 key（现状不变），迁移云时零数据改动。
- 后端负责拼接完整 URL，前端"拿到就用"。
- 图片物理存放于后端 `static/images/words/`，单 JAR 部署时打进 JAR。
- 未来迁移云对象存储时，只改配置不改代码和数据。

## 3. 整体架构与数据流

### 3.1 改造后数据流

```
DB lesson.content (JSON 字符串)
  └─ items[].image = "chase"  (纯 key，现状不变)
        │
        ▼
LessonService.getLessonById()
  └─ WordImageResolver.resolveContent(content)
       ├─ JSON.parse content
       ├─ 遍历 items，把 image 从 key 改写为完整 URL
       │   "${baseUrl}/images/words/chase.jpg"
       └─ 序列化回 JSON 字符串
        │
        ▼
LessonDto.content (image 已是完整 URL)
        │
        ▼
前端 LessonView.vue
  └─ currentItem.image 直接用作 <img :src>
```

### 3.2 关键原则

- 数据库存纯 key，迁移云零数据改动。
- 后端负责拼接 URL，前端零感知 baseURL。
- 只有 `getLessonById`（详情接口）处理图片，`getLessonsByUnitId`（列表接口）不动 content，避免无谓开销。
- 图片物理存放：`backend/src/main/resources/static/images/words/`，Spring Boot 默认静态资源映射即可访问 `/images/words/xxx.jpg`。

## 4. 后端改动

### 4.1 新增配置项

文件：`backend/src/main/resources/application.yml`

```yaml
app:
  word-image:
    base-url: ${WORD_IMAGE_BASE_URL:http://localhost:8080}
    path-prefix: /images/words
    extension: .jpg
```

- `base-url`：开发期 `http://localhost:8080`，生产期可改为 CDN 域名。
- `path-prefix`：静态资源路径前缀。
- `extension`：图片扩展名（统一 `.jpg`）。

### 4.2 新增 `WordImageProperties`（配置类）

位置：`backend/src/main/java/com/englishapp/config/WordImageProperties.java`

```java
/**
 * 单词配图配置属性
 * <p>
 * 支持通过环境变量 WORD_IMAGE_BASE_URL 切换图片访问基址，
 * 便于未来迁移至云对象存储(CDN)。
 * </p>
 * @author englishapp
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "app.word-image")
public class WordImageProperties {
    private String baseUrl;
    private String pathPrefix;
    private String extension;
    // getters / setters
}
```

### 4.3 新增 `WordImageResolver`（服务类）

位置：`backend/src/main/java/com/englishapp/service/WordImageResolver.java`

职责：解析 content JSON，把每个 item 的 `image` 字段从 key 改写为完整 URL。

```java
/**
 * 单词配图 URL 解析器
 * <p>
 * 负责将 lesson.content 中 item 的 image 字段(纯 key)
 * 改写为完整可访问的 URL。
 * </p>
 * @author englishapp
 * @since 1.0.0
 */
@Service
public class WordImageResolver {

    private final WordImageProperties properties;

    /**
     * 构造解析器
     * @param properties 单词配图配置
     */
    public WordImageResolver(WordImageProperties properties) {
        this.properties = properties;
    }

    /**
     * 解析 content JSON，把 items 中的 image key 转为完整 URL。
     * 无 image 字段或为空的 item 不处理。
     * JSON 解析失败时返回原 content（降级）。
     * @param content 原始 JSON 字符串
     * @return 改写后的 JSON 字符串
     */
    public String resolveContent(String content) {
        // 1. JSON.parse
        // 2. 遍历 items
        // 3. 对有 image 且非空的 item: image = baseUrl + pathPrefix + "/" + key + extension
        // 4. 序列化回 JSON 字符串
    }

    /**
     * 拼接单个图片的完整 URL。
     * @param key 图片 key(如 "chase")
     * @return 完整 URL(如 "http://localhost:8080/images/words/chase.jpg")
     */
    public String resolveUrl(String key) {
        return properties.getBaseUrl()
                + properties.getPathPrefix()
                + "/" + key
                + properties.getExtension();
    }
}
```

### 4.4 修改 `LessonService`

`getLessonById` 调用 `WordImageResolver` 处理 content；`getLessonsByUnitId`（列表）不动。

```java
public LessonDto getLessonById(Integer id) {
    Lesson lesson = lessonRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Lesson not found: " + id));
    LessonDto dto = toDto(lesson);
    // 详情接口:解析 content 并把 image key 转为完整 URL
    String resolvedContent = wordImageResolver.resolveContent(dto.content());
    return new LessonDto(
            dto.id(), dto.unitId(), dto.name(), dto.type(),
            resolvedContent, dto.sortOrder(), dto.starReward()
    );
}
```

### 4.5 修改启动类

添加 `@EnableConfigurationProperties(WordImageProperties.class)`。

### 4.6 静态资源访问

图片放到 `backend/src/main/resources/static/images/words/`。

Spring Boot 默认把 `classpath:/static/` 映射为根路径，所以 `static/images/words/chase.jpg` 可通过 `http://localhost:8080/images/words/chase.jpg` 访问。

现有 `WebConfig` 的 `PathResourceResolver` 逻辑是"非 api 且资源存在则返回，否则返回 index.html"。图片文件存在，会被正确返回，**无需改动 `WebConfig`**。

## 5. 前端改动

### 5.1 删除文件

- `frontend/src/assets/word-images/`（32 张图 + `index.js`）
- `frontend/src/assets/rubble-crew/`（16 张图 + `index.js`）

### 5.2 修改 `LessonView.vue`

**移除：**
- 第 22 行 `import { getWordImage } from '../assets/word-images/index.js'`

**修改模板（第 333-336 行）：**

```html
<!-- 修改前 -->
<div v-if="currentItem.image && getWordImage(currentItem.word)" class="word-image-wrapper">
  <img :src="getWordImage(currentItem.word)" :alt="currentItem.word" class="word-image" />
</div>
<div v-else class="emoji">{{ currentItem.emoji }}</div>

<!-- 修改后 -->
<div v-if="currentItem.image" class="word-image-wrapper">
  <img :src="currentItem.image" :alt="currentItem.word" class="word-image" />
</div>
<div v-else class="emoji">{{ currentItem.emoji }}</div>
```

逻辑简化：后端下发的 `image` 已是完整 URL，直接用；无 image 字段则回退 emoji。

### 5.3 `normalizeContent` 无需改动

该函数只处理 `words` / `sentences` / `items` 三种结构的归一化，`image` 字段会被原样保留在 item 对象中。

## 6. 图片文件迁移

用脚本将 48 张图片从两个前端目录移动到后端：

```
源:
  frontend/src/assets/word-images/*.jpg  (32 张)
  frontend/src/assets/rubble-crew/*.jpg  (16 张)

目标:
  backend/src/main/resources/static/images/words/*.jpg  (48 张)
```

文件名保持不变（如 `fire-word.jpg`、`wrench-corgi.jpg`、`dozer-blade.jpg`），与 DB 里 `image` key 一致。

## 7. 云迁移路径（未来）

当迁移至 OSS/CDN 时：

1. 把 `static/images/words/` 下所有图片上传至云存储。
2. 修改 `application.yml` 或环境变量：`WORD_IMAGE_BASE_URL=https://cdn.example.com`。
3. 重启服务，完成。

数据库零改动，前端零改动。

## 8. 测试策略

### 8.1 后端单元测试

**`WordImageResolverTest`：**

- `should_returnFullUrl_when_imageKeyExists`：有 image 字段的 item 被正确拼接为完整 URL。
- `should_keepItemUnchanged_when_imageFieldMissing`：无 image 字段的 item 原样保留。
- `should_keepItemUnchanged_when_imageFieldEmpty`：image 为空字符串的 item 原样保留。
- `should_handleMultipleItems_inOneContent`：多个 item 批量处理。
- `should_returnOriginalContent_when_contentIsInvalidJson`：JSON 解析失败时返回原 content（降级）。

**`LessonServiceTest`（补充）：**

- `should_returnLessonWithResolvedImageUrl_when_getLessonById`：详情接口返回的 content 含完整 URL。
- `should_returnLessonWithOriginalContent_when_getLessonsByUnitId`：列表接口不处理 image。

### 8.2 手动验证

1. 启动后端，访问 `http://localhost:8080/images/words/chase.jpg` 确认图片可访问。
2. 启动前端，打开单词课（如 Lesson 22），确认图片正常显示。
3. 打开旧 rubble-crew 单词课（Lesson 32/33），确认图片正常显示。
4. 打开句型课，确认仍显示 emoji。

## 9. 不做的事（YAGNI）

- 不给句型课加 image 字段。
- 不引入图片上传/管理后台。
- 不做图片懒加载优化（48 张图，单课最多 8 张，无需）。
- 不做图片格式优化（WebP 转换等）。
- 不改 `WebConfig`（现有配置已兼容静态图片访问）。
- 不新增数据库迁移（48 个单词的 image 字段已在 V7/V9 中就绪）。
