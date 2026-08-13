# 场景化语音档案

课程内容通过稳定的 `voiceProfile` 描述教学场景，由百度 TTS 映射为不同的语速、
音调和音量。

## 可用档案

| `voiceProfile` | 使用场景 |
|---|---|
| `mimi-warm` | Mimi 日常陪伴 |
| `mimi-excited` | 完成与庆祝 |
| `mimi-encouraging` | 困难题和重试鼓励 |
| `story-narrator` | 中文故事旁白 |
| `challenge-curious` | 数学、百科和挑战问题 |
| `english-teacher` | 单词、句型和自然拼读 |
| `train-conductor` | 火车角色对话 |

课程项示例：

```json
{
  "text": "前面就是彩虹山谷，我们出发吧！",
  "voiceProfile": "mimi-excited",
  "audio": "/audio/mimi/rainbow-valley.mp3"
}
```

- `voiceProfile` 可省略，前端模板会按课型选择默认档案。
- `audio` 可省略；填写后优先播放审核过的真人或预生成音频，不请求 TTS。
- 固定课程默认允许磁盘缓存。未来动态开放问题应传 `cacheable: false`，避免保存儿童输入。

## TTS 接口

```http
POST /api/v1/voice/tts
Content-Type: application/json

{
  "text": "这题有点难，需要一个提示吗？",
  "lan": "zh",
  "voiceProfile": "mimi-encouraging",
  "cacheable": true
}
```

当前只调用百度 TTS。缓存键包含供应商、档案、语言和文本，同一句固定课程内容只需
合成一次。

前端在音频通道闲置后会先执行约 350ms 的输出预热，再播放正式语音，避免手机、
蓝牙耳机或音箱从休眠状态恢复时吞掉开头几个字。连续朗读不会逐句重复预热。

## 真人音频约定

审核后的 MP3 放入 `backend/src/main/resources/static/audio/`，课程项使用以
`/audio/` 开头的 URL。真人录音必须获得录音者授权，不使用儿童录音训练或克隆音色。
