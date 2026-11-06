package com.englishapp.dto;

/**
 * 主题入口（/agent/topics 与 NO_MATCH 的 topics）。
 *
 * @param image 站内图片 URL（已校验文件存在），可为 null
 * @param route 服务端生成的主题页路由，如 /theme/30?subjectId=4，前端直接 router.push
 */
public record AgentTopicDTO(Integer themeId, String name, String image, String route) { }
