# V69 成长好习惯逐页插图

- 生成日期：2026-09-03。
- 生成方式：Codex 内置 `image_gen`，采用用户指定的每次四格生成、再切图方案。
- 范围：课时 619、621、623、625，各 12 个阅读页，共 48 张不同的情节配图。
- 通过 V69 仅更改阅读页 image；正文、问答、课程顺序和旧故事不变。
- V68 的四张概览图继续用于问答，避免使用对应情节图直接提示选择题答案。
- 原创 AI 插图，不使用第三方绘本插图；参考现有四张概览图保持人物和 3D 绘本画风，原图生成记录见 [V68 来源记录](long-growth-habit-stories-v68-sources.md)。
- 所有成品保存于 `backend/src/main/resources/static/images/growth-consequences/`；命名为 `<故事键>-01.jpg` 至 `<故事键>-12.jpg`。
- 每张母图包含左上、右上、左下、右下四个连续场景；逐格裁切为独立 JPG，页面不直接展示四格母图。

## 逐页场景及最终提示词

### missed-exhibition-01-04

参考图：`missed-exhibition.jpg`。

| 位置 | 课时／页码 | 成品文件 | 场景 |
|---|---|---|---|
| 左上 | 619／1 | missed-exhibition-01.jpg | 学校教室，老师和三位孩子一起看一张无字小火车图案邀请卡，主角期待地捧卡；桌面有整座模型城草图与纸桥构想，两位伙伴兴奋讨论，背景是普通教室而非正式展演。 |
| 右上 | 619／2 | missed-exhibition-02.jpg | 在家里玩具铁路旁，男孩兴奋地搭支线，桌上桥梁任务本只画了轮廓、材料尚未动过；表现他被玩具吸引而搁置造桥。白天家中。 |
| 左下 | 619／3 | missed-exhibition-03.jpg | 放学后同学把一条接口尺寸纸递给男孩，男孩忙着画彩旗，尺子和未用的纸板在旁；用动作表现没有认真量桥，只有图案无可读文字。 |
| 右下 | 619／4 | missed-exhibition-04.jpg | 星期四夜晚，男孩与爸爸在台灯下急着拼接两块纸板做玩具桥，几个不稳的桥墩倒在旁边，墙钟与窗外夜色提示时间已晚。桌面手工，不涉及真人桥梁。 |

最终提示词（整段发送，参考图仅用于角色与画风）：

```text
Use case: illustration-story. Create ONE 2048x1536 landscape image sheet divided into an EXACT 2x2 regular grid of FOUR completely separate full-bleed story illustrations. All four panels must be equal size and have identical 4:3 aspect ratios. The vertical boundary is at precisely 50% width and horizontal boundary at precisely 50% height. No outer margin, no gutters, no border lines, no title, no panel numbers. Each panel will be cropped into its own independent reading-page image. No content crosses a panel boundary. The supplied reference is ONLY for protagonist identity and warm refined 3D storybook visual style, NOT layout. Same protagonist in all panels: 7-year-old Chinese boy Peng Zeyu, short black hair, light blue hoodie, navy trousers. Supporting adults and children are Chinese. Maintain consistent faces and props between panels. Tactile materials, believable expressive faces, legible simple narrative staging. In each panel keep important faces, hands and story props inside its central 80%, with natural scenic backgrounds extending to the edges.
TOP LEFT — PAGE 1: 学校教室，老师和三位孩子一起看一张无字小火车图案邀请卡，主角期待地捧卡；桌面有整座模型城草图与纸桥构想，两位伙伴兴奋讨论，背景是普通教室而非正式展演。
TOP RIGHT — PAGE 2: 在家里玩具铁路旁，男孩兴奋地搭支线，桌上桥梁任务本只画了轮廓、材料尚未动过；表现他被玩具吸引而搁置造桥。白天家中。
BOTTOM LEFT — PAGE 3: 放学后同学把一条接口尺寸纸递给男孩，男孩忙着画彩旗，尺子和未用的纸板在旁；用动作表现没有认真量桥，只有图案无可读文字。
BOTTOM RIGHT — PAGE 4: 星期四夜晚，男孩与爸爸在台灯下急着拼接两块纸板做玩具桥，几个不稳的桥墩倒在旁边，墙钟与窗外夜色提示时间已晚。桌面手工，不涉及真人桥梁。
Each panel depicts only its specified scene, not a collage inside a panel. No captions, readable text, letters, logos, watermarks, numbers or mascots. Papers may show diagrams and pictograms with indistinct marks. No blood, horror or humiliating adults. Preserve exact 2x2 equal rectangular geometry for lossless separate cropping.
```

### missed-exhibition-05-08

参考图：`missed-exhibition.jpg`。

| 位置 | 课时／页码 | 成品文件 | 场景 |
|---|---|---|---|
| 左上 | 619／5 | missed-exhibition-05.jpg | 学校模型桌近景，小玩具列车卡在下折的纸桥中央，纸桥墩压扁，轨道接口错开，老师在旁关闭玩具开关，三位孩子惊讶失落；无真人交通事故。 |
| 右上 | 619／6 | missed-exhibition-06.jpg | 老师打电话询问展馆安排，男孩拿着胶带站在损坏的模型桥旁，两位同学抱着准备好的车站盒子，墙上无字日历与时钟；表现截止期限已过的失落，无文字。 |
| 左下 | 619／7 | missed-exhibition-07.jpg | 安安女孩伤心地关掉纸屋车站小灯，将精心做的车站放回盒子；乐乐男孩把一张无字座位卡折起收入口袋，主角低头面对他们，安静的教室近景。 |
| 右下 | 619／8 | missed-exhibition-08.jpg | 周六展馆，主角与爸爸在观众区长椅上静静坐着，远处其他孩子展示运行的模型列车，主角手里是未使用的邀请卡，脸上失落；他们是观众没有参展，无字。 |

最终提示词（整段发送，参考图仅用于角色与画风）：

```text
Use case: illustration-story. Create ONE 2048x1536 landscape image sheet divided into an EXACT 2x2 regular grid of FOUR completely separate full-bleed story illustrations. All four panels must be equal size and have identical 4:3 aspect ratios. The vertical boundary is at precisely 50% width and horizontal boundary at precisely 50% height. No outer margin, no gutters, no border lines, no title, no panel numbers. Each panel will be cropped into its own independent reading-page image. No content crosses a panel boundary. The supplied reference is ONLY for protagonist identity and warm refined 3D storybook visual style, NOT layout. Same protagonist in all panels: 7-year-old Chinese boy Peng Zeyu, short black hair, light blue hoodie, navy trousers. Supporting adults and children are Chinese. Maintain consistent faces and props between panels. Tactile materials, believable expressive faces, legible simple narrative staging. In each panel keep important faces, hands and story props inside its central 80%, with natural scenic backgrounds extending to the edges.
TOP LEFT — PAGE 5: 学校模型桌近景，小玩具列车卡在下折的纸桥中央，纸桥墩压扁，轨道接口错开，老师在旁关闭玩具开关，三位孩子惊讶失落；无真人交通事故。
TOP RIGHT — PAGE 6: 老师打电话询问展馆安排，男孩拿着胶带站在损坏的模型桥旁，两位同学抱着准备好的车站盒子，墙上无字日历与时钟；表现截止期限已过的失落，无文字。
BOTTOM LEFT — PAGE 7: 安安女孩伤心地关掉纸屋车站小灯，将精心做的车站放回盒子；乐乐男孩把一张无字座位卡折起收入口袋，主角低头面对他们，安静的教室近景。
BOTTOM RIGHT — PAGE 8: 周六展馆，主角与爸爸在观众区长椅上静静坐着，远处其他孩子展示运行的模型列车，主角手里是未使用的邀请卡，脸上失落；他们是观众没有参展，无字。
Each panel depicts only its specified scene, not a collage inside a panel. No captions, readable text, letters, logos, watermarks, numbers or mascots. Papers may show diagrams and pictograms with indistinct marks. No blood, horror or humiliating adults. Preserve exact 2x2 equal rectangular geometry for lossless separate cropping.
```

### missed-exhibition-09-12

参考图：`missed-exhibition.jpg`。

| 位置 | 课时／页码 | 成品文件 | 场景 |
|---|---|---|---|
| 左上 | 619／9 | missed-exhibition-09.jpg | 家中白天，男孩把制作计划本打开给两位同学看，本子用量尺寸、桥墩、桥面、试车的小图标分格表示步骤；旁边摆着尺子和准备好的材料，孩子讨论实际进度，无字。 |
| 右上 | 619／10 | missed-exhibition-10.jpg | 学校桌面，三位孩子和老师测试新纸桥，男孩用铅笔在简图上圈出桥墩摇晃位置，先放一节玩具车厢试桥；备用加宽桥墩和整列车停在旁边，表现细心检查。 |
| 左下 | 619／11 | missed-exhibition-11.jpg | 班级分享会上，完整玩具列车跨过修好的纸桥，男孩举起压扁的旧桥墩讲述失败，两位伙伴在旁展示车站，其他儿童安静鼓掌，温暖但不是隆重获奖典礼。 |
| 右下 | 619／12 | missed-exhibition-12.jpg | 一个月后男孩在家桌前先用尺子量纸板制作第一个桥墩，彩笔整齐放在一旁，任务本夹着旧邀请卡，玩具列车停在桌角，傍晚安静踏实，无字。 |

最终提示词（整段发送，参考图仅用于角色与画风）：

```text
Use case: illustration-story. Create ONE 2048x1536 landscape image sheet divided into an EXACT 2x2 regular grid of FOUR completely separate full-bleed story illustrations. All four panels must be equal size and have identical 4:3 aspect ratios. The vertical boundary is at precisely 50% width and horizontal boundary at precisely 50% height. No outer margin, no gutters, no border lines, no title, no panel numbers. Each panel will be cropped into its own independent reading-page image. No content crosses a panel boundary. The supplied reference is ONLY for protagonist identity and warm refined 3D storybook visual style, NOT layout. Same protagonist in all panels: 7-year-old Chinese boy Peng Zeyu, short black hair, light blue hoodie, navy trousers. Supporting adults and children are Chinese. Maintain consistent faces and props between panels. Tactile materials, believable expressive faces, legible simple narrative staging. In each panel keep important faces, hands and story props inside its central 80%, with natural scenic backgrounds extending to the edges.
TOP LEFT — PAGE 9: 家中白天，男孩把制作计划本打开给两位同学看，本子用量尺寸、桥墩、桥面、试车的小图标分格表示步骤；旁边摆着尺子和准备好的材料，孩子讨论实际进度，无字。
TOP RIGHT — PAGE 10: 学校桌面，三位孩子和老师测试新纸桥，男孩用铅笔在简图上圈出桥墩摇晃位置，先放一节玩具车厢试桥；备用加宽桥墩和整列车停在旁边，表现细心检查。
BOTTOM LEFT — PAGE 11: 班级分享会上，完整玩具列车跨过修好的纸桥，男孩举起压扁的旧桥墩讲述失败，两位伙伴在旁展示车站，其他儿童安静鼓掌，温暖但不是隆重获奖典礼。
BOTTOM RIGHT — PAGE 12: 一个月后男孩在家桌前先用尺子量纸板制作第一个桥墩，彩笔整齐放在一旁，任务本夹着旧邀请卡，玩具列车停在桌角，傍晚安静踏实，无字。
Each panel depicts only its specified scene, not a collage inside a panel. No captions, readable text, letters, logos, watermarks, numbers or mascots. Papers may show diagrams and pictograms with indistinct marks. No blood, horror or humiliating adults. Preserve exact 2x2 equal rectangular geometry for lossless separate cropping.
```

### damaged-memory-book-01-04

参考图：`damaged-memory-book.jpg`。

| 位置 | 课时／页码 | 成品文件 | 场景 |
|---|---|---|---|
| 左上 | 621／1 | damaged-memory-book-01.jpg | 奶奶在家里从柜中取出蓝布封面旧纪念册，打开给男孩看唯一的院子水彩，画中有石榴树和两个小人；男孩认真请求借阅，奶奶准备把册子交给他，旁边有透明保护袋。 |
| 右上 | 621／2 | damaged-memory-book-02.jpg | 家中矮桌杂乱，打开的蓝布纪念册夹在积木小院、轨道和画笔中，透明保护袋落在椅子下，男孩专心搭积木，后方低书架特意清空的一格仍空着；妈妈温和提醒。 |
| 左下 | 621／3 | damaged-memory-book-03.jpg | 男孩弯身拾桌下玩具火车时袖口碰倒没拧紧的水瓶，清水流到摊开的旧纪念册上，纸页的石榴树水彩开始晕开；清楚表现碰倒和水流，孩子惊讶，无危险热水。 |
| 右下 | 621／4 | damaged-memory-book-04.jpg | 妈妈接过湿纪念册，用吸水纸轻轻处理表面水迹，男孩紧张地站旁边、手里纸巾已停下；近景能见皱起的旧纸和模糊的水彩颜色，绝不表现用力擦洗或吹风加热。 |

最终提示词（整段发送，参考图仅用于角色与画风）：

```text
Create a warm 3D children's picture-book illustration sheet, landscape 4:3, arranged as four equal rectangular panels in an exact 2x2 grid. No border, no captions, no text. The supplied image is a visual reference for the family and gentle style. Each panel depicts one simple scene, with all important objects away from panel edges. The boy wears a light-blue hoodie. Top-left: a Chinese grandmother shares her old blue cloth scrapbook with her seven-year-old grandson; they look at a watercolor of a courtyard with a pomegranate tree. Top-right: at home, the boy builds a little toy-block courtyard on a cluttered table; the open blue scrapbook lies among pencils and toys and an empty shelf space is visible behind him. Bottom-left: the boy has accidentally tipped a bottle of room-temperature drinking water on the table while reaching for a toy train; water is spreading onto a scrapbook page and he looks surprised. Bottom-right: his mother gently takes care of the wet book with absorbent paper while the boy watches quietly; watercolor on the paper looks smudged. Keep faces friendly and believable, distinguish all four everyday family scenes, keep the four-panel boundaries exactly at half width and half height. No injury or frightening elements. Do not repeat the same scene in different panels.
```

### damaged-memory-book-05-08

参考图：`damaged-memory-book.jpg`。

| 位置 | 课时／页码 | 成品文件 | 场景 |
|---|---|---|---|
| 左上 | 621／5 | damaged-memory-book-05.jpg | 家中，男孩在妈妈陪伴下握着电话诚实说明事情，眼神担忧，桌上受潮的蓝布纪念册平放，妈妈坐在身边耐心支持；真实安静对话场景，无手机可读界面。 |
| 右上 | 621／6 | damaged-memory-book-06.jpg | 奶奶坐在家中看干燥后起皱的旧水彩，手指停在模糊的石榴树下两个人影附近，男孩安静认真听；画里是旧小院与石榴树，没有新的完整替代画，温柔悲伤。 |
| 左下 | 621／7 | damaged-memory-book-07.jpg | 妈妈和奶奶带着干燥的受损旧画向成年纸本修护人员咨询，专业人员在整洁工作台查看起皱和晕色，男孩在旁听，原画平放不擦洗；情绪遗憾、不承诺修复如新，无文字。 |
| 右下 | 621／8 | damaged-memory-book-08.jpg | 家中桌前，奶奶把未受潮车票一张张放在托盘，男孩听取奶奶意见做无字图形标签，妈妈在旁帮助安排保存，旧纪念册放在奶奶身边；尊重物品主人的意愿。 |

最终提示词（整段发送，参考图仅用于角色与画风）：

```text
Use case: illustration-story. Create ONE 2048x1536 landscape image sheet divided into an EXACT 2x2 regular grid of FOUR completely separate full-bleed story illustrations. All four panels must be equal size and have identical 4:3 aspect ratios. The vertical boundary is at precisely 50% width and horizontal boundary at precisely 50% height. No outer margin, no gutters, no border lines, no title, no panel numbers. Each panel will be cropped into its own independent reading-page image. No content crosses a panel boundary. The supplied reference is ONLY for protagonist identity and warm refined 3D storybook visual style, NOT layout. Same protagonist in all panels: 7-year-old Chinese boy Peng Zeyu, short black hair, light blue hoodie, navy trousers. Supporting adults and children are Chinese. Maintain consistent faces and props between panels. Tactile materials, believable expressive faces, legible simple narrative staging. In each panel keep important faces, hands and story props inside its central 80%, with natural scenic backgrounds extending to the edges.
TOP LEFT — PAGE 5: 家中，男孩在妈妈陪伴下握着电话诚实说明事情，眼神担忧，桌上受潮的蓝布纪念册平放，妈妈坐在身边耐心支持；真实安静对话场景，无手机可读界面。
TOP RIGHT — PAGE 6: 奶奶坐在家中看干燥后起皱的旧水彩，手指停在模糊的石榴树下两个人影附近，男孩安静认真听；画里是旧小院与石榴树，没有新的完整替代画，温柔悲伤。
BOTTOM LEFT — PAGE 7: 妈妈和奶奶带着干燥的受损旧画向成年纸本修护人员咨询，专业人员在整洁工作台查看起皱和晕色，男孩在旁听，原画平放不擦洗；情绪遗憾、不承诺修复如新，无文字。
BOTTOM RIGHT — PAGE 8: 家中桌前，奶奶把未受潮车票一张张放在托盘，男孩听取奶奶意见做无字图形标签，妈妈在旁帮助安排保存，旧纪念册放在奶奶身边；尊重物品主人的意愿。
Each panel depicts only its specified scene, not a collage inside a panel. No captions, readable text, letters, logos, watermarks, numbers or mascots. Papers may show diagrams and pictograms with indistinct marks. No blood, horror or humiliating adults. Preserve exact 2x2 equal rectangular geometry for lossless separate cropping.
```

### damaged-memory-book-09-12

参考图：`damaged-memory-book.jpg`。

| 位置 | 课时／页码 | 成品文件 | 场景 |
|---|---|---|---|
| 左上 | 621／9 | damaged-memory-book-09.jpg | 男孩与奶奶用桌上录音设备录下小院记忆，男孩边听边画新草图，草图有明确留白没有假装完整复原，旧画模糊且放远；设备界面没有文字，温暖下午光。 |
| 右上 | 621／10 | damaged-memory-book-10.jpg | 男孩和妈妈一起重新整理客厅：饮水杯只放独立小边桌，借来的书进入保护袋和书架固定一格，积木在地垫上；男孩正在把漫画送回书架，用品位置清楚。 |
| 左下 | 621／11 | damaged-memory-book-11.jpg | 学校家庭故事会，男孩向同学展示新故事册中的石榴树草图，图上人影位置留一块空白，桌边小音箱播放奶奶录音，老师与同学专心听，原始旧画没有带来。 |
| 右下 | 621／12 | damaged-memory-book-12.jpg | 奶奶家，男孩归还一本普通画册，祖孙一起检查完整书页，柜内安全放着蓝布旧纪念册；男孩口袋露出无字图标清单，一只喝过的杯子已放在厨房台上，平静负责。 |

最终提示词（整段发送，参考图仅用于角色与画风）：

```text
Use case: illustration-story. Create ONE 2048x1536 landscape image sheet divided into an EXACT 2x2 regular grid of FOUR completely separate full-bleed story illustrations. All four panels must be equal size and have identical 4:3 aspect ratios. The vertical boundary is at precisely 50% width and horizontal boundary at precisely 50% height. No outer margin, no gutters, no border lines, no title, no panel numbers. Each panel will be cropped into its own independent reading-page image. No content crosses a panel boundary. The supplied reference is ONLY for protagonist identity and warm refined 3D storybook visual style, NOT layout. Same protagonist in all panels: 7-year-old Chinese boy Peng Zeyu, short black hair, light blue hoodie, navy trousers. Supporting adults and children are Chinese. Maintain consistent faces and props between panels. Tactile materials, believable expressive faces, legible simple narrative staging. In each panel keep important faces, hands and story props inside its central 80%, with natural scenic backgrounds extending to the edges.
TOP LEFT — PAGE 9: 男孩与奶奶用桌上录音设备录下小院记忆，男孩边听边画新草图，草图有明确留白没有假装完整复原，旧画模糊且放远；设备界面没有文字，温暖下午光。
TOP RIGHT — PAGE 10: 男孩和妈妈一起重新整理客厅：饮水杯只放独立小边桌，借来的书进入保护袋和书架固定一格，积木在地垫上；男孩正在把漫画送回书架，用品位置清楚。
BOTTOM LEFT — PAGE 11: 学校家庭故事会，男孩向同学展示新故事册中的石榴树草图，图上人影位置留一块空白，桌边小音箱播放奶奶录音，老师与同学专心听，原始旧画没有带来。
BOTTOM RIGHT — PAGE 12: 奶奶家，男孩归还一本普通画册，祖孙一起检查完整书页，柜内安全放着蓝布旧纪念册；男孩口袋露出无字图标清单，一只喝过的杯子已放在厨房台上，平静负责。
Each panel depicts only its specified scene, not a collage inside a panel. No captions, readable text, letters, logos, watermarks, numbers or mascots. Papers may show diagrams and pictograms with indistinct marks. No blood, horror or humiliating adults. Preserve exact 2x2 equal rectangular geometry for lossless separate cropping.
```

### empty-stage-place-01-04

参考图：`empty-stage-place.jpg`。

| 位置 | 课时／页码 | 成品文件 | 场景 |
|---|---|---|---|
| 左上 | 623／1 | empty-stage-place-01.jpg | 影子剧场排练室，男孩兴奋地把纸狐狸举到明亮窗前，墙上投出狐狸耳朵的影子；老师在旁介绍演出安排，旁边有白幕布、纸树与观众椅，期待正式演出的心情。 |
| 右上 | 623／2 | empty-stage-place-02.jpg | 夜晚家中，男孩在沙发上盯着播放无文字动画的平板不愿停下，爸爸站旁边指向收尾用的时钟；旁边桌上有未完成纸狐狸，窗外已黑，表现玩兴与该休息的冲突。 |
| 左下 | 623／3 | empty-stage-place-03.jpg | 影子剧彩排，白色幕布上纸兔已经到了小门边，男孩手里纸狐狸却垂着，他困倦走神，老师温和叫他，另一个孩子等待接台词；没有受伤或恐怖气氛。 |
| 右下 | 623／4 | empty-stage-place-04.jpg | 睡前房间，男孩忧心地反复看用小图标表达的狐狸戏路线卡，妈妈坐床边听他说担心，纸偶盒和明天的衣服已整理好；妈妈帮助收尾，灯光柔和，文字不可读。 |

最终提示词（整段发送，参考图仅用于角色与画风）：

```text
Use case: illustration-story. Create ONE 2048x1536 landscape image sheet divided into an EXACT 2x2 regular grid of FOUR completely separate full-bleed story illustrations. All four panels must be equal size and have identical 4:3 aspect ratios. The vertical boundary is at precisely 50% width and horizontal boundary at precisely 50% height. No outer margin, no gutters, no border lines, no title, no panel numbers. Each panel will be cropped into its own independent reading-page image. No content crosses a panel boundary. The supplied reference is ONLY for protagonist identity and warm refined 3D storybook visual style, NOT layout. Same protagonist in all panels: 7-year-old Chinese boy Peng Zeyu, short black hair, light blue hoodie, navy trousers. Supporting adults and children are Chinese. Maintain consistent faces and props between panels. Tactile materials, believable expressive faces, legible simple narrative staging. In each panel keep important faces, hands and story props inside its central 80%, with natural scenic backgrounds extending to the edges.
TOP LEFT — PAGE 1: 影子剧场排练室，男孩兴奋地把纸狐狸举到明亮窗前，墙上投出狐狸耳朵的影子；老师在旁介绍演出安排，旁边有白幕布、纸树与观众椅，期待正式演出的心情。
TOP RIGHT — PAGE 2: 夜晚家中，男孩在沙发上盯着播放无文字动画的平板不愿停下，爸爸站旁边指向收尾用的时钟；旁边桌上有未完成纸狐狸，窗外已黑，表现玩兴与该休息的冲突。
BOTTOM LEFT — PAGE 3: 影子剧彩排，白色幕布上纸兔已经到了小门边，男孩手里纸狐狸却垂着，他困倦走神，老师温和叫他，另一个孩子等待接台词；没有受伤或恐怖气氛。
BOTTOM RIGHT — PAGE 4: 睡前房间，男孩忧心地反复看用小图标表达的狐狸戏路线卡，妈妈坐床边听他说担心，纸偶盒和明天的衣服已整理好；妈妈帮助收尾，灯光柔和，文字不可读。
Each panel depicts only its specified scene, not a collage inside a panel. No captions, readable text, letters, logos, watermarks, numbers or mascots. Papers may show diagrams and pictograms with indistinct marks. No blood, horror or humiliating adults. Preserve exact 2x2 equal rectangular geometry for lossless separate cropping.
```

### empty-stage-place-05-08

参考图：`empty-stage-place.jpg`。

| 位置 | 课时／页码 | 成品文件 | 场景 |
|---|---|---|---|
| 左上 | 623／5 | empty-stage-place-05.jpg | 演出前后台，男孩疲倦地握纸狐狸、眼皮沉重，爸爸与老师陪他坐在安静休息区喝水，远处透过门能见观众入场，表现不能勉强上场，需要照顾和休息。 |
| 右上 | 623／6 | empty-stage-place-06.jpg | 后台近景，男孩难过地把纸狐狸木柄交给准备代演的安安女孩，老师在旁协调，幕布后有一个空出的矮凳和纸偶袋；交接动作清楚、同伴没有得意或嘲笑。 |
| 左下 | 623／7 | empty-stage-place-07.jpg | 安静休息室，爸爸抱着泪眼男孩，男孩攥着空纸偶布袋，半开的门外隐约见亮着的舞台和观众剪影，表现演出正在继续而他不能参加，温暖安慰不恐怖。 |
| 右下 | 623／8 | empty-stage-place-08.jpg | 散场后台，安安把纸狐狸归还主角，乐乐抱着纸兔，三位孩子认真对话，主角眼含遗憾向他们道谢道歉；桌上有演完的纸树，老师在远处照看，无庆功夸张表情。 |

最终提示词（整段发送，参考图仅用于角色与画风）：

```text
Use case: illustration-story. Create ONE 2048x1536 landscape image sheet divided into an EXACT 2x2 regular grid of FOUR completely separate full-bleed story illustrations. All four panels must be equal size and have identical 4:3 aspect ratios. The vertical boundary is at precisely 50% width and horizontal boundary at precisely 50% height. No outer margin, no gutters, no border lines, no title, no panel numbers. Each panel will be cropped into its own independent reading-page image. No content crosses a panel boundary. The supplied reference is ONLY for protagonist identity and warm refined 3D storybook visual style, NOT layout. Same protagonist in all panels: 7-year-old Chinese boy Peng Zeyu, short black hair, light blue hoodie, navy trousers. Supporting adults and children are Chinese. Maintain consistent faces and props between panels. Tactile materials, believable expressive faces, legible simple narrative staging. In each panel keep important faces, hands and story props inside its central 80%, with natural scenic backgrounds extending to the edges.
TOP LEFT — PAGE 5: 演出前后台，男孩疲倦地握纸狐狸、眼皮沉重，爸爸与老师陪他坐在安静休息区喝水，远处透过门能见观众入场，表现不能勉强上场，需要照顾和休息。
TOP RIGHT — PAGE 6: 后台近景，男孩难过地把纸狐狸木柄交给准备代演的安安女孩，老师在旁协调，幕布后有一个空出的矮凳和纸偶袋；交接动作清楚、同伴没有得意或嘲笑。
BOTTOM LEFT — PAGE 7: 安静休息室，爸爸抱着泪眼男孩，男孩攥着空纸偶布袋，半开的门外隐约见亮着的舞台和观众剪影，表现演出正在继续而他不能参加，温暖安慰不恐怖。
BOTTOM RIGHT — PAGE 8: 散场后台，安安把纸狐狸归还主角，乐乐抱着纸兔，三位孩子认真对话，主角眼含遗憾向他们道谢道歉；桌上有演完的纸树，老师在远处照看，无庆功夸张表情。
Each panel depicts only its specified scene, not a collage inside a panel. No captions, readable text, letters, logos, watermarks, numbers or mascots. Papers may show diagrams and pictograms with indistinct marks. No blood, horror or humiliating adults. Preserve exact 2x2 equal rectangular geometry for lossless separate cropping.
```

### empty-stage-place-09-12

参考图：`empty-stage-place.jpg`。

| 位置 | 课时／页码 | 成品文件 | 场景 |
|---|---|---|---|
| 左上 | 623／9 | empty-stage-place-09.jpg | 家中晚饭后，爸爸妈妈与男孩一起安排收尾：纸狐狸放进盒子，小卡片画明天继续的尾巴图标，爸爸把手机收进篮子，桌边摆短故事书和洗漱用品；安稳支持，无可读文字。 |
| 右上 | 623／10 | empty-stage-place-10.jpg | 温暖卧室，男孩已放下未完成狐狸尾巴的手工盒，坐床边向爸爸倾诉担忧，爸爸蹲下倾听，妈妈在门口温和陪伴，小夜灯亮着；重点是主动表达而非偷偷熬夜。 |
| 左下 | 623／11 | empty-stage-place-11.jpg | 数周后小型班级影子戏，主角清醒专注地稳稳举起一棵纸树，另一位女孩持狐狸，老师在侧面提示，台下少量孩子安静观看；主角承担较短旁白，小而踏实的成功。 |
| 右下 | 623／12 | empty-stage-place-12.jpg | 家中夜晚，桌上立着奶奶和男孩拿纸狐狸的合影，男孩把未完成小尾巴放进手工盒，再朝洗漱间走去；短故事书和睡衣备好，明天继续的卡片仅有图标，无字，平静收尾。 |

最终提示词（整段发送，参考图仅用于角色与画风）：

```text
Use case: illustration-story. Create ONE 2048x1536 landscape image sheet divided into an EXACT 2x2 regular grid of FOUR completely separate full-bleed story illustrations. All four panels must be equal size and have identical 4:3 aspect ratios. The vertical boundary is at precisely 50% width and horizontal boundary at precisely 50% height. No outer margin, no gutters, no border lines, no title, no panel numbers. Each panel will be cropped into its own independent reading-page image. No content crosses a panel boundary. The supplied reference is ONLY for protagonist identity and warm refined 3D storybook visual style, NOT layout. Same protagonist in all panels: 7-year-old Chinese boy Peng Zeyu, short black hair, light blue hoodie, navy trousers. Supporting adults and children are Chinese. Maintain consistent faces and props between panels. Tactile materials, believable expressive faces, legible simple narrative staging. In each panel keep important faces, hands and story props inside its central 80%, with natural scenic backgrounds extending to the edges.
TOP LEFT — PAGE 9: 家中晚饭后，爸爸妈妈与男孩一起安排收尾：纸狐狸放进盒子，小卡片画明天继续的尾巴图标，爸爸把手机收进篮子，桌边摆短故事书和洗漱用品；安稳支持，无可读文字。
TOP RIGHT — PAGE 10: 温暖卧室，男孩已放下未完成狐狸尾巴的手工盒，坐床边向爸爸倾诉担忧，爸爸蹲下倾听，妈妈在门口温和陪伴，小夜灯亮着；重点是主动表达而非偷偷熬夜。
BOTTOM LEFT — PAGE 11: 数周后小型班级影子戏，主角清醒专注地稳稳举起一棵纸树，另一位女孩持狐狸，老师在侧面提示，台下少量孩子安静观看；主角承担较短旁白，小而踏实的成功。
BOTTOM RIGHT — PAGE 12: 家中夜晚，桌上立着奶奶和男孩拿纸狐狸的合影，男孩把未完成小尾巴放进手工盒，再朝洗漱间走去；短故事书和睡衣备好，明天继续的卡片仅有图标，无字，平静收尾。
Each panel depicts only its specified scene, not a collage inside a panel. No captions, readable text, letters, logos, watermarks, numbers or mascots. Papers may show diagrams and pictograms with indistinct marks. No blood, horror or humiliating adults. Preserve exact 2x2 equal rectangular geometry for lossless separate cropping.
```

### hidden-crack-01-04

参考图：`hidden-crack.jpg`。

| 位置 | 课时／页码 | 成品文件 | 场景 |
|---|---|---|---|
| 左上 | 625／1 | hidden-crack-01.jpg | 教室黏土小镇布展前，男孩为安全托盘里的邮局和面包店摆放无字图标名牌，女孩展示门前有小鸟的黏土邮局，老师说明小助手职责；完好的低木架在旁尚未摆放作品，期待且信任。 |
| 右上 | 625／2 | hidden-crack-02.jpg | 教室低矮木展示架旁，男孩俯身捡笔、手肘刚碰到木架侧面，低架侧连接处出现细裂缝、上层板略倾斜；黏土作品仍安全留在旁边桌上，男孩惊讶，没有砸人。 |
| 左下 | 625／3 | hidden-crack-03.jpg | 近景男孩心虚地把彩色装饰纸贴在低木架侧面遮住裂缝，纸的边缘下仍能看到裂口，上层板不稳；同学在背景询问，主角回避目光；表现错误行为后果的叙事画面。 |
| 右下 | 625／4 | hidden-crack-04.jpg | 老师检查稍歪的低木架，询问男孩，男孩紧张地摇头，手攥着衣角，裂缝藏在侧面装饰纸之后；另一个女孩抱着黏土邮局托盘等待，表现错过诚实报告机会。 |

最终提示词（整段发送，参考图仅用于角色与画风）：

```text
Use case: illustration-story. Create ONE 2048x1536 landscape image sheet divided into an EXACT 2x2 regular grid of FOUR completely separate full-bleed story illustrations. All four panels must be equal size and have identical 4:3 aspect ratios. The vertical boundary is at precisely 50% width and horizontal boundary at precisely 50% height. No outer margin, no gutters, no border lines, no title, no panel numbers. Each panel will be cropped into its own independent reading-page image. No content crosses a panel boundary. The supplied reference is ONLY for protagonist identity and warm refined 3D storybook visual style, NOT layout. Same protagonist in all panels: 7-year-old Chinese boy Peng Zeyu, short black hair, light blue hoodie, navy trousers. Supporting adults and children are Chinese. Maintain consistent faces and props between panels. Tactile materials, believable expressive faces, legible simple narrative staging. In each panel keep important faces, hands and story props inside its central 80%, with natural scenic backgrounds extending to the edges.
TOP LEFT — PAGE 1: 教室黏土小镇布展前，男孩为安全托盘里的邮局和面包店摆放无字图标名牌，女孩展示门前有小鸟的黏土邮局，老师说明小助手职责；完好的低木架在旁尚未摆放作品，期待且信任。
TOP RIGHT — PAGE 2: 教室低矮木展示架旁，男孩俯身捡笔、手肘刚碰到木架侧面，低架侧连接处出现细裂缝、上层板略倾斜；黏土作品仍安全留在旁边桌上，男孩惊讶，没有砸人。
BOTTOM LEFT — PAGE 3: 近景男孩心虚地把彩色装饰纸贴在低木架侧面遮住裂缝，纸的边缘下仍能看到裂口，上层板不稳；同学在背景询问，主角回避目光；表现错误行为后果的叙事画面。
BOTTOM RIGHT — PAGE 4: 老师检查稍歪的低木架，询问男孩，男孩紧张地摇头，手攥着衣角，裂缝藏在侧面装饰纸之后；另一个女孩抱着黏土邮局托盘等待，表现错过诚实报告机会。
Each panel depicts only its specified scene, not a collage inside a panel. No captions, readable text, letters, logos, watermarks, numbers or mascots. Papers may show diagrams and pictograms with indistinct marks. No blood, horror or humiliating adults. Preserve exact 2x2 equal rectangular geometry for lossless separate cropping.
```

### hidden-crack-05-08

参考图：`hidden-crack.jpg`。

| 位置 | 课时／页码 | 成品文件 | 场景 |
|---|---|---|---|
| 左上 | 625／5 | hidden-crack-05.jpg | 低展示架上层板倾斜，黏土邮局和面包店已经滑落到下方桌面裂成几块，小鸟翅膀断开；老师伸手示意孩子退后，儿童双手离开碎片，无人受伤、无血，清楚安全边界。 |
| 右上 | 625／6 | hidden-crack-06.jpg | 教室安全区域，男孩哭着向老师和伙伴说明经过，老师蹲下认真听；远处桌上有损坏作品和被揭开的装饰纸、侧面裂缝，孩子没有接触碎片，没有羞辱指责的姿势。 |
| 左下 | 625／7 | hidden-crack-07.jpg | 展览桌上邮局原定位置空着，老师摆了一个无文字的制作过程照片框，旁边面包店屋顶有接缝；女孩抱着空托盘失落，男孩站旁边尊重地停下伸出的手，安静遗憾。 |
| 右下 | 625／8 | hidden-crack-08.jpg | 老师坐在桌旁与男孩平等交谈，把小助手无字图标名牌暂时放进抽屉，桌上有检查记录本；男孩难过又认真听，温暖支持，背景其他孩子正常参加活动，不是孤立惩罚。 |

最终提示词（整段发送，参考图仅用于角色与画风）：

```text
Use case: illustration-story. Create ONE 2048x1536 landscape image sheet divided into an EXACT 2x2 regular grid of FOUR completely separate full-bleed story illustrations. All four panels must be equal size and have identical 4:3 aspect ratios. The vertical boundary is at precisely 50% width and horizontal boundary at precisely 50% height. No outer margin, no gutters, no border lines, no title, no panel numbers. Each panel will be cropped into its own independent reading-page image. No content crosses a panel boundary. The supplied reference is ONLY for protagonist identity and warm refined 3D storybook visual style, NOT layout. Same protagonist in all panels: 7-year-old Chinese boy Peng Zeyu, short black hair, light blue hoodie, navy trousers. Supporting adults and children are Chinese. Maintain consistent faces and props between panels. Tactile materials, believable expressive faces, legible simple narrative staging. In each panel keep important faces, hands and story props inside its central 80%, with natural scenic backgrounds extending to the edges.
TOP LEFT — PAGE 5: 低展示架上层板倾斜，黏土邮局和面包店已经滑落到下方桌面裂成几块，小鸟翅膀断开；老师伸手示意孩子退后，儿童双手离开碎片，无人受伤、无血，清楚安全边界。
TOP RIGHT — PAGE 6: 教室安全区域，男孩哭着向老师和伙伴说明经过，老师蹲下认真听；远处桌上有损坏作品和被揭开的装饰纸、侧面裂缝，孩子没有接触碎片，没有羞辱指责的姿势。
BOTTOM LEFT — PAGE 7: 展览桌上邮局原定位置空着，老师摆了一个无文字的制作过程照片框，旁边面包店屋顶有接缝；女孩抱着空托盘失落，男孩站旁边尊重地停下伸出的手，安静遗憾。
BOTTOM RIGHT — PAGE 8: 老师坐在桌旁与男孩平等交谈，把小助手无字图标名牌暂时放进抽屉，桌上有检查记录本；男孩难过又认真听，温暖支持，背景其他孩子正常参加活动，不是孤立惩罚。
Each panel depicts only its specified scene, not a collage inside a panel. No captions, readable text, letters, logos, watermarks, numbers or mascots. Papers may show diagrams and pictograms with indistinct marks. No blood, horror or humiliating adults. Preserve exact 2x2 equal rectangular geometry for lossless separate cropping.
```

### hidden-crack-09-12

参考图：`hidden-crack.jpg`。

| 位置 | 课时／页码 | 成品文件 | 场景 |
|---|---|---|---|
| 左上 | 625／9 | hidden-crack-09.jpg | 老师陪同三位孩子讨论补救，黏土邮局残片已经由大人收在安全盒里，女孩示意暂时不要粘接，主角认真记录图形清单；妈妈在旁准备材料，孩子不接触锋利碎片。 |
| 右上 | 625／10 | hidden-crack-10.jpg | 两周后男孩在美术室发现空托盘边缘开裂，他先把托盘单独放在桌上并指给老师看，旁边的画具保持原样，老师准备更换完整托盘；没人被指责，问题及时停住。 |
| 左下 | 625／11 | hidden-crack-11.jpg | 女孩与男孩各扶住一个装着纸画的轻盒子一侧，确认拿稳后一起放到桌面，老师在附近看护，男孩专注，女孩谨慎但轻轻微笑；不是让他独自拿黏土作品。 |
| 右下 | 625／12 | hidden-crack-12.jpg | 学期末教室，展示柜里面包店屋顶仍有接缝、新邮局旁有收着旧小鸟的小盒；主角合上检查记录本，旁边的新木架侧面干净完整，没有掩盖损坏的装饰纸，老师与他一起整理。 |

最终提示词（整段发送，参考图仅用于角色与画风）：

```text
Use case: illustration-story. Create ONE 2048x1536 landscape image sheet divided into an EXACT 2x2 regular grid of FOUR completely separate full-bleed story illustrations. All four panels must be equal size and have identical 4:3 aspect ratios. The vertical boundary is at precisely 50% width and horizontal boundary at precisely 50% height. No outer margin, no gutters, no border lines, no title, no panel numbers. Each panel will be cropped into its own independent reading-page image. No content crosses a panel boundary. The supplied reference is ONLY for protagonist identity and warm refined 3D storybook visual style, NOT layout. Same protagonist in all panels: 7-year-old Chinese boy Peng Zeyu, short black hair, light blue hoodie, navy trousers. Supporting adults and children are Chinese. Maintain consistent faces and props between panels. Tactile materials, believable expressive faces, legible simple narrative staging. In each panel keep important faces, hands and story props inside its central 80%, with natural scenic backgrounds extending to the edges.
TOP LEFT — PAGE 9: 老师陪同三位孩子讨论补救，黏土邮局残片已经由大人收在安全盒里，女孩示意暂时不要粘接，主角认真记录图形清单；妈妈在旁准备材料，孩子不接触锋利碎片。
TOP RIGHT — PAGE 10: 两周后男孩在美术室发现空托盘边缘开裂，他先把托盘单独放在桌上并指给老师看，旁边的画具保持原样，老师准备更换完整托盘；没人被指责，问题及时停住。
BOTTOM LEFT — PAGE 11: 女孩与男孩各扶住一个装着纸画的轻盒子一侧，确认拿稳后一起放到桌面，老师在附近看护，男孩专注，女孩谨慎但轻轻微笑；不是让他独自拿黏土作品。
BOTTOM RIGHT — PAGE 12: 学期末教室，展示柜里面包店屋顶仍有接缝、新邮局旁有收着旧小鸟的小盒；主角合上检查记录本，旁边的新木架侧面干净完整，没有掩盖损坏的装饰纸，老师与他一起整理。
Each panel depicts only its specified scene, not a collage inside a panel. No captions, readable text, letters, logos, watermarks, numbers or mascots. Papers may show diagrams and pictograms with indistinct marks. No blood, horror or humiliating adults. Preserve exact 2x2 equal rectangular geometry for lossless separate cropping.
```

## 切图处理

- 12 张母图实际尺寸均为 1448×1086，四等分原始单格 724×543。
- 使用 macOS `sips --cropOffset` 按左上、右上、左下、右下顺序裁切，每格四边各内缩 5 像素以去掉细分隔线，成品 714×533。
- 从原始 PNG 直接裁成 JPEG，质量 85，不放大、不拉伸。全部 48 张图片内容哈希互不相同。
- 已逐组查看完整分镜，并抽查切出的独立画面边缘；裁切不包含相邻场景。
- 纪念册第 1～4 页首次生成被服务通用内容检查拒绝，改用以上普通家庭场景提示词重试成功。文中记录的是最终使用的提示词。

## 验收记录

- 顺序执行 V1～V69 并比较迁移前后的数据：仅四个阅读课时的 48 个 image 字段改变，全部正文、页序、问答、旧课程和主题／单元数据不变。
- 检查 48 个独立图片键、对应 JPG 文件及内容哈希，确认无缺失、无重复图片；新增图片合计约 5.85 MiB。
- 按规定顺序完成 `mvn test` → `npm ci` → `npm run build` → `mvn clean package`；使用 Corretto 17，两次 Maven 验收均为 48 项测试通过，零失败、零错误、零跳过。
- 启动最终单 JAR，原临时预览数据库从 V68 成功升级到 V69；本地预览沿用 18068 端口。
- 验证 8 个课时 API，以及 48 张逐页图和 4 张问答概览图的 URL，均返回 HTTP 200，图片可正常解码。
- Chrome / Playwright 分别以桌面 1440×1000、手机 390×844 视口走完 48 页阅读与 24 道问答，逐页比对显示的图片 URL；完成结算、保存进度及返回主题页均通过。
- 保存两种尺寸下全部阅读页截图并抽查实际页面：图文对应，图片无拉伸和相邻分格残留，页面无横向溢出或脚本异常。手机尺寸为浏览器模拟，未使用真实手机设备。
- 两种尺寸均额外模拟新图片加载失败，仍能阅读正文、翻完 12 页并进入完成结算。
