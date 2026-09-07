package com.englishapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Set;

/** 绘本扩展字段校验：原课型保持兼容，缺失图片或无效题目不能交给学习页。 */
final class PicturebookContentValidator {
    private static final Set<String> ACTIONS = Set.of("ticket", "signal", "speak");

    private PicturebookContentValidator() {}

    static void validate(JsonNode root) {
        if (!root.path("picturebook").asBoolean(false)) return;
        require(Set.of("READING", "QUIZ").contains(root.path("type").asText()), "绘本课型无效");
        require(!root.path("bookTitle").asText().isBlank(), "绘本名称不能为空");
        JsonNode items = root.path("items");
        require(items.isArray() && !items.isEmpty(), "绘本内容不能为空");
        for (JsonNode item : items) {
            require(item.path("image").asText().matches("[a-z0-9-]+(?:/[a-z0-9-]+)+"), "绘本配图 key 无效");
            require("en".equals(item.path("audioLanguage").asText()), "绘本必须配置英文朗读");
            if (item.has("trainAction")) {
                require(ACTIONS.contains(item.path("trainAction").asText()), "火车互动类型无效");
            }
            if ("QUIZ".equals(root.path("type").asText())) {
                JsonNode options = item.path("options");
                require(options.isArray() && options.size() >= 2, "绘本问题至少需要两个选项");
                require(item.path("answer").isInt() && item.path("answer").asInt() >= 0
                        && item.path("answer").asInt() < options.size(), "绘本答案索引无效");
                require(!item.path("audioText").asText().isBlank(), "绘本问题广播不能为空");
            } else {
                require(!item.path("content").asText().isBlank(), "绘本原文不能为空");
            }
        }
    }

    private static void require(boolean valid, String message) {
        if (!valid) throw new IllegalArgumentException(message);
    }
}
