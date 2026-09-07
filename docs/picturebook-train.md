# 绘本小火车

入口：`/app/picturebooks`；单本：`/app/picturebooks/:unitId`。首页、英语主题和已学课时均可进入。普通课时入口遇到 `content.picturebook=true` 时进入绘本旅程。

一本书对应一个现有 unit，三站对应三个 lesson：READING 故事站、QUIZ 问答站、READING 小司机站。完成沿用 progress 接口，阅读盖章给参与星星，阅读得分为 0（不冒充发音测评）；问答首次正确 100、提示后正确 70，平均分提交到服务端。不接入自动口语评分。

## 内容接口示例

新增内容通过 V72 迁移。GET `/api/v1/lessons/{id}` 仍返回统一 `{code,message,data}`，`data.content` 为 JSON 字符串：

```json
{
  "type": "READING",
  "picturebook": true,
  "bookTitle": "溪边秋千 · 绘本1",
  "items": [{
    "title": "检票上车",
    "content": "Tickets, please!",
    "translation": "请出示车票！",
    "image": "picturebooks/train-station",
    "audioLanguage": "en",
    "trainAction": "ticket"
  }]
}
```

上例 image 是迁移内的 key；接口详情解析为 `/images/picturebooks/train-station.jpg`。所有可视项必须有图片，QUIZ 图片选项使用现有 `{text,image,showText}`。新增 `picturebook`、`bookTitle`、`translation`、`prompt`（开放式中文预测题）、`trainAction`（ticket / signal / speak）。后端对绘本课型、标题、必备图片、英文语言、火车动作和答案索引进行校验；前端有专属阅读及问答组件。`announcement` 用于逐页中文到站广播，通过单独按钮播放。

本机书签使用 `picturebook:<unitId>`，保存 lessonId、页码和本趟已答题分数，避免刷新丢分。完整站点由服务端保存；保存失败保留当前页面，允许重试。浏览器存储禁用时仍可学习，但不能承诺中途页码跨刷新恢复。切页、离开、切模式和换站不自动朗读；广播必须用户点击，离开活动时停止。项目语音不可用时尝试设备语音，失败显示提示并允许继续。

故事中关键停靠点可以通过「故事途中提问」开关关闭；开放题不判断对错。火车进度动画遵循 reduced-motion，并有关闭开关。原 PDF 的课堂词汇和拼读页放在第三站。

## 验收

后端测试 → npm ci → npm test 与 npm run build → Maven clean package。用临时 SQLite 数据库启动完整 JAR，验证迁移、所有图片 URL、桌面及移动页面、错误选项重试、刷新恢复、保存失败重试和三站盖章。
