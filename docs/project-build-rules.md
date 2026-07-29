# 项目构建与课程内容交付规则

本文是项目构建、课程内容和配图交付的长期规则。历史迁移中存在的空 `image`
属于存量技术债；从本文生效后，所有新增或修改的课程内容必须遵守本文。

文档中的“必须”是合并门禁，不满足时不得将内容标记为完成。

## 1. 构建环境

- 后端固定使用 Maven 和 Java 17。执行 Maven 前必须显式设置
  `JAVA_HOME=/Users/dawn/Library/Java/JavaVirtualMachines/corretto-17.0.13/Contents/Home`。
- 前端固定使用 npm；存在 `package-lock.json` 时使用 `npm ci` 安装锁定版本，
  不使用其他包管理器生成新的锁文件。
- 前端生产产物输出到 `backend/src/main/resources/static/app/`。该目录是生成物，
  禁止手工修改；课程图片位于同级 `static/images/`，不会被前端构建清理。
- 数据库结构和种子内容只通过 Flyway 演进。已提交的迁移禁止修改，必须新增
  `V{版本号}__{描述}.sql`。

## 2. 标准构建流程

```bash
# 1. 后端测试
cd backend
export JAVA_HOME=/Users/dawn/Library/Java/JavaVirtualMachines/corretto-17.0.13/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"
mvn test

# 2. 前端锁定依赖并构建
cd ../frontend
npm ci
npm run build

# 3. 生成包含前端与课程图片的单 JAR
cd ../backend
mvn clean package

# 4. 启动生产包
java -jar target/english-app-backend-1.0.0.jar
```

正式交付不得以 `-DskipTests` 代替完整构建。仅本地排查构建问题时可临时跳过测试，
但完成任务前必须重新执行上述完整流程。

## 3. 课程内容规则

### 3.1 数据与课型

- 新课程通过新的 Flyway 迁移写入 `lesson` 表，禁止直接修改
  `backend/english.db` 作为交付结果。
- `lesson.type`、`content.type` 和前端模板必须一致，只允许
  `WORD`、`SENTENCE`、`READING`、`QUIZ`、`CALCULATE`、`PHONICS`、
  `DIALOGUE`。
- `content.items` 必须存在且非空；字段结构必须匹配对应模板，选项题的
  `answer` 必须是有效选项索引，计算题答案必须可被当前模板正确比较。
- 新增课型或字段时，必须同时更新后端枚举/校验、前端课时模板、接口示例和测试，
  不得只写数据库内容。

### 3.2 配图是强制交付物

- **新增或修改课程时，所有可视学习项必须配图。** `WORD`、`SENTENCE`、
  `READING`、`QUIZ`、`CALCULATE`、`PHONICS` 的每个 `items[]` 都必须包含
  非空 `image`；空字符串、缺失字段、远程 URL 和仅用 emoji 代替图片均不合格。
- `DIALOGUE` 当前只展示固定头像，没有课程场景图。新增或修改 `DIALOGUE`
  时必须同时提供至少一张与场景相关的图片并完成模板展示支持；在模板支持落地前，
  不得新增纯文本对话课程。
- 图片必须与当前词语、句子、故事页或题目直接相关，不能使用无关通用图凑数；
  同一张图片可以复用，但必须保证不会泄露题目答案。
- 课程迁移、图片文件和来源记录必须在同一次变更中提交，不接受“先上内容、后补图”。

### 3.3 图片存储与引用

- 图片统一存放在
  `backend/src/main/resources/static/images/<主题>/<文件名>.jpg`。
- `image` 只存不带 `/images/` 前缀和扩展名的 key，例如文件
  `static/images/ocean/sea-turtle.jpg` 对应 `"image":"ocean/sea-turtle"`。
- 新 key 使用小写 ASCII `kebab-case`，按主题建目录；禁止绝对路径、
  `../`、查询参数、Base64 和外部热链。
- 当前 `WordImageResolver` 统一补全 `.jpg`，所以新增课程图片必须为 JPG。
  如需引入其他格式，必须先调整解析配置和测试，再添加内容。
- 图片应适合儿童观看、主体清晰、无水印和敏感信息，并控制到 Web 合理尺寸；
  透明角色图转换成 JPG 时必须检查背景与可读性。
- 网络素材必须确认使用权，并在 `docs/assets/` 下记录来源 URL、下载日期、
  本地文件名和版权说明；AI 生成图片应记录生成方式，不得冒充官方素材。

## 4. 课程变更验收

课程内容变更必须完成以下检查：

1. 新迁移中不存在空 `image`：

   ```bash
   rg -n '"image"\s*:\s*""' backend/src/main/resources/db/migration/V24__新增课程.sql
   ```

   将文件名替换为本次新增的实际迁移；命令应无输出。

2. 逐一核对迁移中的 `image` key 均存在对应 JPG 文件，且大小写完全一致。
3. 执行标准构建流程，确保 Flyway、后端测试和前端构建全部通过。
4. 启动单 JAR，调用新增课时详情接口，确认返回的 `items[].image` 是可访问 URL：

   ```bash
   curl http://localhost:8080/api/v1/lessons/新增课时ID
   curl -I http://localhost:8080/images/主题/文件名.jpg
   ```

5. 在桌面端和移动端至少各检查一次课程页面：图片无 404、无明显裁切或拉伸，
   文图对应，加载失败时不会阻断课程完成流程。

## 5. 完成定义

- [ ] 新增 Flyway 迁移，且未修改历史迁移
- [ ] 每个可视学习项都有配图，`DIALOGUE` 已提供并展示课程场景图
- [ ] 图片 key、文件路径和 `.jpg` 扩展规则一致
- [ ] 素材来源或 AI 生成信息已记录
- [ ] 后端测试通过
- [ ] 前端构建通过
- [ ] 单 JAR 打包并完成 API、图片 URL 和页面验收
