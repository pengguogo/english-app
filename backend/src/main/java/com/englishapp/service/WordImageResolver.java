package com.englishapp.service;

import com.englishapp.config.WordImageProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 单词配图 URL 解析器
 * <p>
 * 负责将 lesson.content 中 item 的 image 字段(纯 key,如 "chase")
 * 改写为完整可访问的 URL(如 "http://localhost:8080/images/words/chase.jpg")。
 * 数据库只存纯 key,迁移云存储时仅需修改配置,无需改动数据。
 * </p>
 *
 * @author englishapp
 * @since 1.0.0
 */
@Service
public class WordImageResolver {

    private static final Logger log = LoggerFactory.getLogger(WordImageResolver.class);

    /** content 中承载单词项的数组字段名 */
    private static final String ITEMS_FIELD = "items";

    /** item 中图片 key 的字段名 */
    private static final String IMAGE_FIELD = "image";

    private final WordImageProperties properties;

    /** JSON 序列化/反序列化器,Spring Boot 默认已配置,这里复用单例 */
    private final ObjectMapper objectMapper;

    /**
     * 构造解析器
     * @param properties 单词配图配置
     * @param objectMapper Jackson ObjectMapper
     */
    public WordImageResolver(WordImageProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    /**
     * 解析 content JSON,把 items 中每个 item 的 image key 转为完整 URL。
     * <p>
     * 处理规则:
     * <ul>
     *   <li>无 image 字段或为空字符串的 item 原样保留</li>
     *   <li>有 image 字段则改写为 baseUrl + pathPrefix + "/" + key + extension</li>
     *   <li>JSON 解析失败时返回原 content(降级,保证业务可用)</li>
     * </ul>
     *
     * @param content 原始 JSON 字符串
     * @return 改写后的 JSON 字符串;解析失败时返回原 content
     */
    public String resolveContent(String content) {
        if (content == null || content.isBlank()) {
            return content;
        }
        try {
            JsonNode root = objectMapper.readTree(content);
            JsonNode itemsNode = root.get(ITEMS_FIELD);
            // 无 items 数组(如句型课的其他结构)或非数组,原样返回
            if (itemsNode == null || !itemsNode.isArray()) {
                return content;
            }
            // 遍历 items,改写 image 字段
            ArrayNode items = (ArrayNode) itemsNode;
            for (JsonNode itemNode : items) {
                if (!(itemNode instanceof ObjectNode item)) {
                    continue;
                }
                JsonNode imageNode = item.get(IMAGE_FIELD);
                // image 字段缺失或为空,跳过
                if (imageNode == null || imageNode.isNull() || imageNode.asText().isBlank()) {
                    continue;
                }
                String key = imageNode.asText();
                item.put(IMAGE_FIELD, resolveUrl(key));
            }
            return objectMapper.writeValueAsString(root);
        } catch (JsonProcessingException e) {
            // JSON 解析失败时降级:返回原 content,记录日志,保证课时可用
            log.error("解析 lesson content 失败,返回原始 content", e);
            return content;
        }
    }

    /**
     * 拼接单个图片的完整 URL。
     * @param key 图片 key(如 "chase")
     * @return 完整 URL(如 "http://localhost:8080/images/words/chase.jpg")
     */
    public String resolveUrl(String key) {
        return properties.getBaseUrl()
                + properties.getPathPrefix()
                + "/" + key
                + properties.getExtension();
    }
}
