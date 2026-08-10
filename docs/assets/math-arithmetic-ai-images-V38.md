# V38 数学加减运算配图生成记录

- 生成日期：2026-08-10
- 生成方式：Codex 内置 `image_gen`（AI 生成）
- 用途：数学“加减运算”主题 6 个 `CALCULATE` 课时的概念配图
- 外部素材：无
- 版权说明：AI 生成内容，不冒充官方素材；交付前已人工检查儿童友好性、文图语义、文字与水印。

## 文件与课时映射

| 本地文件 | image key | 课时 |
| --- | --- | --- |
| `math-arithmetic/addition-within-five.jpg` | `math-arithmetic/addition-within-five` | 92：5以内加法 |
| `math-arithmetic/addition-within-ten.jpg` | `math-arithmetic/addition-within-ten` | 93：10以内加法 |
| `math-arithmetic/addition-practice.jpg` | `math-arithmetic/addition-practice` | 94：加法综合练习 |
| `math-arithmetic/subtraction-within-five.jpg` | `math-arithmetic/subtraction-within-five` | 95：5以内减法 |
| `math-arithmetic/subtraction-within-ten.jpg` | `math-arithmetic/subtraction-within-ten` | 96：10以内减法 |
| `math-arithmetic/subtraction-practice.jpg` | `math-arithmetic/subtraction-practice` | 97：减法综合练习 |

## 最终提示词集合

所有图片使用以下共同规范：`scientific-educational`；儿童数学 App 方形课时插图；精致儿童绘本风；柔和圆润、主体清晰、缩略图可读；不显示数字、算式、答案、字母、标签、品牌或水印；对象不排成可计数队列，不直接暗示题目答案；不使用项目吉祥物。

1. `addition-within-five`：两只小篮子向同一块游戏垫倾倒并汇合彩色木块，木块自然重叠并部分藏在篮中，突出“两组合并”。
2. `addition-within-ten`：花园野餐桌上，两只小果碗向一个共享大碗汇合，水果重叠且部分被碗沿遮挡，突出“合并”。
3. `addition-practice`：教室手工角里，两名儿童把各自盒中的手工材料倒入同一个共享托盘，突出“共同汇集”。
4. `subtraction-within-five`：儿童从高边不透明收纳篮中拿走一个封闭束口袋，篮内物品完全不可见，仅以动作表达“拿走”。首轮可数玩具方案因可能暗示答案而废弃后重生成为此版本。
5. `subtraction-within-ten`：公园树上有鸟儿从枝叶间飞离，树中鸟儿部分被叶片遮挡，突出“离开”，避免可计数排列。
6. `subtraction-practice`：儿童从装有重叠蜡笔的杯子中取出蜡笔并移向笔袋，突出“从集合中移除”，数量不明确。

## 输出处理

内置工具原始输出为 PNG；项目成品使用 macOS `sips` 转换为 1024×1024、JPEG 品质 85 的 RGB JPG，以符合 `WordImageResolver` 的 `.jpg` 解析约定。
