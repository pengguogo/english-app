package com.englishapp.dto;

import java.time.OffsetDateTime;
import java.util.List;

/** 会话详情：chosen / correct 为 null 表示未作答 */
public record ParentAgentSessionDetailDTO(ParentAgentSessionDTO session, List<Message> messages,
                                          List<Interaction> interactions) {

    public record Message(String role, String text, String moderation, OffsetDateTime createdAt) { }

    public record Interaction(String question, List<AgentChatResponseDTO.Option> options, String chosen,
                              Boolean correct, OffsetDateTime createdAt) { }
}
