# 汉字认读

V82 覆盖原汉字识读 lesson 68–73；V83 按 2024 修订版人教统编一年级上册识字表补齐为 73 课、280 字。旧课时进度不迁移到认字状态。
选字范围与大班使用方式见 `docs/curriculum/hanzi-grade1-2024.md`。
首页“认字与每日复习”进入 `/app/character-review`，新版和旧版课时页均支持。

## 学习与复习

单字学习：看图认识 → 听音选字 → 看字选图 → 无图、无拼音听音辨认 → 在短句中找字。
拼音按需展开，跟读仅作发音辅助。选项洗牌，图片选项没有字词标签。
答对后高亮正确选项并显示绿色成功反馈，约 900ms 后自动下一问；最后一问保存成功后自动进入下一字。
答错留在当前题；保存失败保留重试入口；切换学习项或离开页面取消自动跳转。
首次错误优先记为 WRONG，否则看提示记为 ASSISTED，全部独立答对记为 INDEPENDENT。
“记为待巩固”也记为 WRONG，不增加独立认对天数。

识字状态：NEW（未学习）、REVIEWING（待巩固）、RECOGNIZED（跨天认对）。
同一天重复答对只计一天；三个不同日期独立认对后标记跨天认对。
第一次独立认对隔 1 天复习、第二次隔 3 天、第三次及以后隔 7 天；错误或提示后隔 1 天。
错误或提示清零连续巩固天数，保留累计错误/提示次数，同天补答不会恢复天数。
日期按 Asia/Shanghai 计算。每日队列最多 8 字，只取已学且到期字，优先认错、提示、再按到期时间。
复习时只做无图听音辨认，练习期间隐藏课程字表和家长记录。

## 接口

统一 `{code,message,data}`，固定 `user_id=1`。

- `GET /api/v1/characters/progress`：280 项；每项包含 `item`、`status`、`independentDays`、`wrongCount`、`assistedCount`、`lastOutcome`、`dueDate`。
- `GET /api/v1/characters/review`：最多 8 个到期学习项。
- `POST /api/v1/characters/attempts`：记录一轮单字辨认结果。

```json
{"eventId":"unique-attempt-id","lessonId":68,"itemIndex":0,"outcome":"INDEPENDENT"}
```

`item` 字段：`lessonId`、`itemIndex`（从 0 开始）、`word`、`phonetic`、`image`（解析后的 URL）、`exampleWord`、`exampleSentence`、`imageChoice`、`readingNote`。
`imageChoice` 缺省为 true；虚词与抽象字为 false，图片用于理解语境，第三步改为听词找字；多音字通过组词定位目标读音。
课程内容仍为 WORD，在 items 中新增 `recognition:true`、`exampleWord`、`exampleSentence`，后端目录仅接受汉字识读主题的 WORD 课程标记项，拒绝其他课程和越界索引。
`outcome` 只允许 INDEPENDENT / ASSISTED / WRONG，eventId 最长 80 位，只允许 ASCII 字母数字与连字符。
服务端通过事务和唯一 eventId 去重，保存失败时前端用同一事件重试，成功后才能进入下一字。
认读结果由前端练习汇总，上述接口不用于防作弊或考试评分。

## 素材与验收

V82 为连续新增迁移，不修改 V15。24 张无水印 AI 插画以 JPG 同批交付，来源详见 `docs/assets/hanzi-pilot-v82-sources.md`。
测试覆盖同日重复、跨天巩固、错误退回、复习优先级、事件去重、非法索引、真实 SQLite 迁移及所有配图文件。
正式验收依次执行后端测试、npm ci、前端构建、Maven 完整打包，然后检查单 JAR API、图片、桌面与移动页面。

## 2026-10-10 验收记录

- Corretto 17：后端 98 项测试通过，Maven clean package 成功，未跳过测试。
- npm ci、38 项前端测试和生产构建通过。
- 单 JAR 使用独立临时 SQLite 库启动；24 个配图 URL 返回有效 JPG，课程接口返回完整认字字段。
- 旧版实测完整五步：首次答错、后续纠正后保存，仍记为 WRONG / 待巩固。
- 真实 API 重复 eventId 提交只累计一次；隔天队列优先错误、随后提示字。
- 新版移动视口 390×844 检查通过，未出现图片加载失败和横向溢出；收起重复摘要后认字卡片靠前。
- 桌面每日复习仅展示听音辨认，课程字表与家长记录隐藏。
