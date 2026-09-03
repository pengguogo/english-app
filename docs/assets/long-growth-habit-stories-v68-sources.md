# V68 成长好习惯长篇故事与配图来源

- 交付日期：2026-09-03
- 迁移：`V68__add_long_growth_habit_stories.sql`
- 追加到既有主题 49「成长好习惯」，单元 153～156，课时 619～626。
- 原有十篇故事及配套问答保持不变；本次新增 4 篇、48 个阅读页、24 道问答。
- 正文各 1785、1851、1911、1888 字符（含标点）；每篇 12 页，适合亲子共读或连续朗读。

## 内容来源与创作边界

四篇均为本项目原创现实成长故事，不复制已有绘本原文、人物对白或插图。

- 《没有开上展台的列车》与《幕布后空着的位置》借鉴《伊索寓言》中“提前准备，机会到来时准备不足会付出代价”的叙事结构。参考美国国会图书馆 [The Ants & the Grasshopper](https://read.gov/aesop/052.html)，访问日期 2026-09-03。仅吸收准备与后果之间的因果关系，没有沿用拒绝帮助的结局，也不将休息或艺术活动描述成错误。
- 《被藏起来的裂缝》借鉴《伊索寓言》中“不诚实会损害信任”的母题。参考美国国会图书馆 [The Shepherd Boy & the Wolf](https://read.gov/aesop/043.html)，访问日期 2026-09-03。不照搬狼、死亡或无人救助情节，改为隐瞒物品损坏、同伴作品受损及逐步恢复信任。
- 《再也拼不回的那一页》围绕家庭纪念物的不可替代性原创；不是对特定绘本的改写。
- 严重后果来自情节中的连续选择：失去正式展演机会、原画永久受损、临场换角、损坏同伴作品并暂时失去保管职责。补救有作用，但不会撤销损失。
- 成人承担照护、设备检查、修理和必要沟通责任。没有体罚、羞辱、剥夺基本照护，也没有把所有责任推给孩子。睡眠篇描述特定角色的经历，不承诺单次晚睡必然造成某种结果；纪念册篇由成人寻求专业意见，不提供儿童自行修复旧画的教程。
- 问答检查因果链、他人感受、补救边界和迁移应用。每课答案索引 0/1/2 各两题。

## 图片来源和使用

- 生成方式：Codex 内置 `image_gen`，通过 `imagegen` 技能；非 CLI/API 回退。
- 生成日期：2026-09-03；用途：`illustration-story`。
- 原创 AI 绘本概览图，没有下载或复用第三方绘本插图，不冒充官方或手绘素材。
- 原始 PNG 保留在 Codex 生成目录；使用 macOS `sips` 等比缩放至宽 1200 像素、JPEG 质量 85，不拉伸。
- 每篇阅读与问答复用本篇概览图，表现人物和核心场景，不包含答案文字或具体结局提示。
- 以下文件均位于 `backend/src/main/resources/static/images/growth-consequences/`，数据库键省略 `/images/` 与 `.jpg`。

| 文件 | 故事 | 核心场景 |
|---|---|---|
| missed-exhibition.jpg | 没有开上展台的列车 | 三位孩子和老师检查纸桥与玩具列车 |
| damaged-memory-book.jpg | 再也拼不回的那一页 | 祖孙与妈妈一起翻看家庭纪念册 |
| empty-stage-place.jpg | 幕布后空着的位置 | 孩子和老师在影子剧场排练狐狸与纸树 |
| hidden-crack.jpg | 被藏起来的裂缝 | 孩子与老师商量黏土小镇布展 |

## 最终生成提示词

每张图的提示词由以下共同前缀、对应场景和共同约束组成，未提供外部参考图片。

共同前缀：

```text
Use case: illustration-story. Asset type: original Chinese children's reading course overview illustration. Create one landscape 16:9 image in refined warm 3D storybook illustration style, tactile paper and clay details, believable gentle expressions, coherent anatomy and uncluttered focal composition.
```

### missed-exhibition.jpg

```text
Scene: A classroom maker fair preparation table with a large handmade cardboard railway bridge and tiny wooden train, three Chinese children collaborating, short black-haired seven-year-old boy Peng Zeyu wearing light blue hoodie and navy trousers thoughtfully looking at a task notebook, adult teacher beside them. A small indoor exhibition stage and analog clock in background. Bridge is a tabletop toy, no humans on it. Quiet anticipation and responsibility.
```

### damaged-memory-book.jpg

```text
Scene: A family living room craft table with an old fabric-bound family scrapbook opened to faded generic watercolor drawings of a little rural cottage, a closed reusable water bottle on a separate side table, a wooden toy train on the floor, a short black-haired seven-year-old Chinese boy Peng Zeyu in light blue hoodie and navy trousers sitting with his kind elderly grandmother and mother, carefully looking at keepsakes. Bittersweet tender mood, respect for irreplaceable memories.
```

### empty-stage-place.jpg

```text
Scene: A community children's shadow puppet theatre rehearsal room, softly lit white fabric screen in wooden frame, simple paper fox puppet and paper tree silhouettes, short black-haired seven-year-old Chinese boy Peng Zeyu wearing light blue hoodie and navy trousers holding a puppet beside another child and a kind adult teacher, unoccupied audience seats in foreground, a folded blue bedtime blanket and book on a backstage bench suggesting need for rest. Reflective, emotionally nuanced atmosphere.
```

### hidden-crack.jpg

```text
Scene: A classroom art exhibition preparation table with a low wooden display shelf, several small handmade clay houses on a padded tray, tape measure and drawing notebook on table, short black-haired seven-year-old Chinese boy Peng Zeyu in light blue hoodie and navy trousers and a girl with braided hair thoughtfully discussing the shared display with a kind female teacher. An adult toolbox CLOSED on the floor, no child power tools. Mood of accountability, fragile artwork and rebuilding trust.
```

共同约束：

```text
No text, no letters, no logos, no watermarks, no copyrighted characters, no mascots, no horror. Keep all key subjects within central 80% for responsive display.
```

## 验收记录

- 在内存 SQLite 中顺序执行 V1～V68；执行 V68 前后比较所有既有 theme、unit、lesson 行，内容完全一致。主题 49 从 10 个单元增加为 14 个。
- 所有 72 个新增学习项的图片键均非空、格式有效，均能找到对应 JPG；24 道题的答案索引合法。
- 严格按顺序执行 Java 17 下的 `mvn test`、`npm ci`、`npm run build`、`mvn clean package`，全部通过；两次 Maven 验收各 48 项测试，零失败、零错误、零跳过。
- 启动打包后的单 JAR，使用独立临时数据库和 18068 端口；Flyway 68 成功，8 个课时详情接口及 4 个 JPG URL 均返回 HTTP 200，图片为 image/jpeg。
- 使用系统 Chrome / Playwright，在桌面 1440×1000 和手机 390×844 视口逐项走完全部 8 个课时，验证 48 页阅读、24 道题、完成结算、保存进度及返回主题页面；两种尺寸均无横向溢出、无页面脚本异常。
- 检查两种尺寸的截图：主体清晰，文字和图片对应，没有明显拉伸或影响主体的裁切。手机尺寸为浏览器模拟，未使用真实手机设备。
- 两种尺寸均额外拦截新图请求模拟图片加载失败，正文、12 页翻页和阅读完成仍然可用。
