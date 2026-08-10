# V37 数学数字认知配图生成记录

- 生成日期：2026-08-10
- 生成方式：Codex 内置 `image_gen`（OpenAI 图像生成）逐张生成
- 用途：数学「数字认知」主题 lesson 88、91 的学习卡片
- 后处理：使用 macOS `sips` 缩放至 960 × 960 px，转换为 JPG（质量 88）
- 授权说明：全部图片为本次任务 AI 生成，无第三方素材 URL；按项目使用场景及 OpenAI 相关条款使用

## 文件与课程映射

| Lesson | item 索引 | 学习项 | image key | 本地文件 |
|---|---:|---|---|---|
| 88 | 0 | 1<2 | `math-number/compare-1-2` | `math-number/compare-1-2.jpg` |
| 88 | 1 | 3>1 | `math-number/compare-3-1` | `math-number/compare-3-1.jpg` |
| 88 | 2 | 4>3 | `math-number/compare-4-3` | `math-number/compare-4-3.jpg` |
| 88 | 3 | 5>2 | `math-number/compare-5-2` | `math-number/compare-5-2.jpg` |
| 91 | 0 | 第一 | `math-number/ordinal-first` | `math-number/ordinal-first.jpg` |
| 91 | 1 | 第二 | `math-number/ordinal-second` | `math-number/ordinal-second.jpg` |
| 91 | 2 | 第三 | `math-number/ordinal-third` | `math-number/ordinal-third.jpg` |
| 91 | 3 | 第四 | `math-number/ordinal-fourth` | `math-number/ordinal-fourth.jpg` |
| 91 | 4 | 第五 | `math-number/ordinal-fifth` | `math-number/ordinal-fifth.jpg` |

## 提示词设计

统一提示词采用 `scientific-educational` 用例，要求儿童友好的方形数学学习卡片、简洁浅色背景、粗而平滑的深色轮廓、柔和阴影、主体完整且可清楚计数，并禁止文字、数字、数学符号、标签、Logo 和水印。

- 比大小：分别生成精确的两组对象，数量为 1/2 个苹果、3/1 个气球、4/3 个积木、5/2 颗星；两组水平分隔，不添加其他可计数装饰。
- 序数：每张均为恰好 5 只朝右侧终点移动的小鸭，其中 1 只蓝鸭作为目标、4 只黄鸭作为参照；蓝鸭从终点侧依次处于第 1、2、3、4、5 位。

所有最终图均经过人工检查，确认数量、顺序、画面安全性以及无文字和水印。
