package com.englishapp.dto;

import java.time.OffsetDateTime;

/** 家长列表 / 详情中的会话摘要；topic 为第一句脱敏原话，turns 为孩子发言数 */
public record ParentAgentSessionDTO(Long id, Integer childId, String childName, String topic, String sourcePage,
                                    boolean flagged, String flagLabel, long turns,
                                    OffsetDateTime createdAt, OffsetDateTime updatedAt) { }
