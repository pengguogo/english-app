# V35 语文拼音启蒙 AI 配图记录

- 生成日期：2026-08-10
- 生成方式：Codex 内置 `image_gen`（默认内置模式）
- 用途：语文「拼音启蒙」WORD 课程卡片
- 输出处理：内置工具生成 PNG，使用 macOS `sips` 转换为 960×960、RGB、JPEG 质量 88
- 版权说明：AI 生成素材，无第三方网络素材；不得冒充官方图片

## 统一提示词

```text
Use case: scientific-educational
Asset type: Chinese pinyin learning card illustration for children
Style/medium: polished 3D storybook illustration, soft rounded forms,
consistent children's educational app style
Composition/framing: centered main subject, square composition,
generous padding, clear single focal point
Lighting/mood: bright soft daylight, encouraging and calm
Constraints: no written letters, no Chinese characters, no numbers,
no captions, no watermark, no logos, no mascot, no frightening elements
```

每张图片在统一提示词后追加与学习项直接对应的主体和场景描述。计数图额外要求精确数量。

## 文件与学习项映射

| 课时 | 学习项 | 本地文件 |
|---|---|---|
| 62 单韵母 a o e | ā、á、ǎ、à | `chinese-pinyin/child-speaking-a.jpg` |
| 62 单韵母 a o e | ō | `chinese-pinyin/rooster-crowing.jpg` |
| 62 单韵母 a o e | ē | `chinese-pinyin/white-goose.jpg` |
| 63 单韵母 i u ü | ī | `chinese-pinyin/shirt.jpg` |
| 63 单韵母 i u ü | ū | `chinese-pinyin/black-crow.jpg` |
| 63 单韵母 i u ü | ǖ、ǘ | `chinese-pinyin/colorful-fish.jpg` |
| 63 单韵母 i u ü | í | `chinese-pinyin/polite-bow.jpg` |
| 63 单韵母 i u ü | ú | `chinese-pinyin/empty-hands.jpg` |
| 64 复韵母 | ai | `chinese-pinyin/loving-family.jpg` |
| 64 复韵母 | ei | `chinese-pinyin/child-calling.jpg` |
| 64 复韵母 | ui | `chinese-pinyin/brave-child.jpg` |
| 64 复韵母 | ao | `chinese-pinyin/gold-medal.jpg` |
| 64 复韵母 | ou | `chinese-pinyin/globe-europe.jpg` |
| 64 复韵母 | iu | `chinese-pinyin/excellent-star.jpg` |
| 65 声母 b p m f | bā | `chinese-pinyin/eight-blocks.jpg` |
| 65 声母 b p m f | pā | `chinese-pinyin/child-lying-prone.jpg` |
| 65 声母 b p m f | mā | `chinese-pinyin/mother.jpg` |
| 65 声母 b p m f | fā | `chinese-pinyin/send-paper-plane.jpg` |
| 65 声母 b p m f | bó | `chinese-pinyin/friendly-uncle.jpg` |
| 65 声母 b p m f | pō | `chinese-pinyin/grassy-hillside.jpg` |
| 66 声母 d t n l | dā | `chinese-pinyin/building-blocks.jpg` |
| 66 声母 d t n l | tā | `chinese-pinyin/young-boy.jpg` |
| 66 声母 d t n l | ná | `chinese-pinyin/pick-up-toy.jpg` |
| 66 声母 d t n l | lā | `chinese-pinyin/pull-wagon.jpg` |
| 66 声母 d t n l | dú | `chinese-pinyin/reading-book.jpg` |
| 66 声母 d t n l | tú | `chinese-pinyin/drawing-picture.jpg` |
| 67 声母 g k h | gē | `chinese-pinyin/older-brother.jpg` |
| 67 声母 g k h | kē | `chinese-pinyin/science-experiment.jpg` |
| 67 声母 g k h | hē | `chinese-pinyin/drinking-water.jpg` |
| 67 声母 g k h | gū | `chinese-pinyin/friendly-aunt.jpg` |
| 67 声母 g k h | kū | `chinese-pinyin/crying-child.jpg` |
| 67 声母 g k h | hú | `chinese-pinyin/peaceful-lake.jpg` |

全部文件存放于 `backend/src/main/resources/static/images/chinese-pinyin/`，
课程内容仅保存不含 `/images/` 前缀和 `.jpg` 扩展名的 key。
