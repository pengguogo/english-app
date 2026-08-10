# V36 语文古诗朗诵配图生成记录

- 生成日期：2026-08-10
- 生成方式：Codex 内置 `image_gen`（OpenAI 图像生成）逐张生成；按下述三个批次生成并转换为 JPEG
- 使用范围：语文主题「古诗朗诵」，lesson 74–79 的 24 个 `SENTENCE` 学习项
- 版权说明：AI 生成素材，无外部素材 URL；仅用于本项目课程展示，不冒充官方插画

## 生成批次与提示词约束

### 批次一：静夜思、春晓、咏鹅、悯农（16 张）

- 成品尺寸：`1448×1086`（4:3）
- 视觉风格：儿童绘本水彩与水粉画，主体柔和、清晰、易识别
- 后处理：PNG 原图转换为 JPEG，质量参数 82

```text
Use case: illustration-story
Asset type: children's Chinese poetry lesson item illustration
Primary request: illustrate the specified classical Chinese poem line with a scene directly matching its meaning
Style/medium: polished children's picture-book watercolor and gouache, gentle and easy to recognize
Composition/framing: landscape 4:3, clear single scene, main subject prominent
Constraints: age-appropriate; culturally respectful; historically inspired where people or buildings appear; no text, no Chinese characters, no calligraphy, no logo, no watermark
Avoid: photorealism, modern objects, frightening imagery, unrelated clutter
```

### 批次二：登鹳雀楼（4 张）

- 成品尺寸：`960×960`
- 视觉风格：精致 3D 儿童绘本风，圆润造型、清晰空间层次和温暖光照
- 核心约束：逐句呈现夕阳依山、黄河入海、登高远眺和更上一层楼；适合儿童；无文字、无水印、无现代物件

### 批次三：望庐山瀑布（4 张）

- 成品尺寸：`1024×1024`
- 视觉风格：电影感 3D 儿童绘本风，宏大但不惊险，瀑布、山峰、云雾和光线层次清晰
- 核心约束：逐句呈现日照紫烟、遥望悬瀑、飞流直下和银河落天；适合儿童；无文字、无水印、无现代物件

每张图片在对应批次约束上加入下表中的诗句和场景描述，确保画面与学习项直接对应。

| 本地文件 | 对应诗句 | 单图提示词核心场景 |
|---|---|---|
| `quiet-night-thought-1.jpg` | 床前明月光 | 古代卧室窗外明月，银色月光清晰洒在床前地面 |
| `quiet-night-thought-2.jpg` | 疑是地上霜 | 月光铺满室内地面，如一层洁白寒霜 |
| `quiet-night-thought-3.jpg` | 举头望明月 | 唐代诗人抬头凝望窗外满月 |
| `quiet-night-thought-4.jpg` | 低头思故乡 | 诗人低头沉思，远方暖灯村落象征故乡 |
| `spring-dawn-1.jpg` | 春眠不觉晓 | 春日清晨，孩子仍在传统卧室安睡，晨光入窗 |
| `spring-dawn-2.jpg` | 处处闻啼鸟 | 春花枝头多只小鸟鸣叫，孩子临窗倾听 |
| `spring-dawn-3.jpg` | 夜来风雨声 | 春夜风雨吹动庭院花枝，从温暖室内望向窗外 |
| `spring-dawn-4.jpg` | 花落知多少 | 雨后庭院石板散落许多粉白花瓣 |
| `ode-to-goose-1.jpg` | 鹅鹅鹅 | 三只白鹅在绿水池塘边欢快鸣叫 |
| `ode-to-goose-2.jpg` | 曲项向天歌 | 白鹅弯曲长颈、昂首向蓝天鸣叫 |
| `ode-to-goose-3.jpg` | 白毛浮绿水 | 白鹅安静漂浮在清澈碧绿水面 |
| `ode-to-goose-4.jpg` | 红掌拨清波 | 透明水面剖视角展示红色鹅掌划水形成波纹 |
| `farmers-toil-1.jpg` | 锄禾日当午 | 农民在正午烈日下为青绿禾苗锄草 |
| `farmers-toil-2.jpg` | 汗滴禾下土 | 农民弯身劳作，汗珠清晰滴入禾苗下泥土 |
| `farmers-toil-3.jpg` | 谁知盘中餐 | 饭桌上的白米饭与窗外农田、劳作农民相连接 |
| `farmers-toil-4.jpg` | 粒粒皆辛苦 | 手掌中的颗颗稻米与背景中手工收割的农民相连接 |
| `stork-tower-1.jpg` | 白日依山尽 | 夕阳贴近连绵群山，暮色逐渐笼罩远景 |
| `stork-tower-2.jpg` | 黄河入海流 | 壮阔黄河奔流向远方海面 |
| `stork-tower-3.jpg` | 欲穷千里目 | 古人在高楼远眺层叠千里山河 |
| `stork-tower-4.jpg` | 更上一层楼 | 古人继续登上鹳雀楼更高一层 |
| `lushan-waterfall-1.jpg` | 日照香炉生紫烟 | 阳光照耀庐山香炉峰，山间升起淡紫云雾 |
| `lushan-waterfall-2.jpg` | 遥看瀑布挂前川 | 从远处望见白色瀑布如长练悬挂山前 |
| `lushan-waterfall-3.jpg` | 飞流直下三千尺 | 瀑布从高崖飞泻直下，展现强烈高度感 |
| `lushan-waterfall-4.jpg` | 疑是银河落九天 | 夜色般的山景中，瀑布如璀璨银河从天而降 |

## 文件位置

全部文件位于：

```text
backend/src/main/resources/static/images/chinese-poem/
```
