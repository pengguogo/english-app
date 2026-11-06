package com.englishapp.dto;

import java.util.List;

/** 家长会话列表分页：page 从 0 开始 */
public record ParentAgentSessionPageDTO(List<ParentAgentSessionDTO> items, boolean hasMore) { }
