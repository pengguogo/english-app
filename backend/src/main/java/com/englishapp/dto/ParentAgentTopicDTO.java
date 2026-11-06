package com.englishapp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.OffsetDateTime;
import java.util.List;

/** 主题学习情况：sessions 为最近 5 个相关会话 id（倒序）；suggestion 仅 PRACTICE 时有 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ParentAgentTopicDTO(Integer childId, Integer themeId, String themeName, String status,
                                  List<Long> sessions, int correct, int wrong, OffsetDateTime lastAt,
                                  Suggestion suggestion) {

    public record Suggestion(Long id, String basis, String route) { }
}
