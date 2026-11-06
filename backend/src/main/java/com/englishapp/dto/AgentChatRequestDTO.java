package com.englishapp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * /agent/chat 请求。
 *
 * @param text      孩子说的话（识别结果或打字），1–100 字
 * @param turnToken /agent/asr 返回的回合令牌：有效时本轮不再扣额度
 * @param context   当前所在课程的学习项；服务端按 id 读库取正文，不接收前端文本
 * @param sessionId 上一轮响应里的会话 id；属于当前孩子且 30 分钟内有活动才续用，否则新建
 */
public record AgentChatRequestDTO(@NotBlank @Size(max = 100) String text,
                                  @Size(max = 512) String turnToken,
                                  @Valid Context context,
                                  Long sessionId) {

    public record Context(Integer lessonId, Integer itemIndex) { }
}
