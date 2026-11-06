package com.englishapp.dto;

import java.util.List;

/**
 * 课程向导结果。
 *
 * @param type       RECOMMEND（1–3 个候选）/ CLARIFY（2–3 个跨主题候选 + 追问）/ NO_MATCH（topics 3–4 个）
 * @param speech     给 TTS 的短句，≤40 字，固定模板生成
 * @param candidates 候选课程，≤3；NO_MATCH 时为空数组
 * @param topics     热门主题；仅 NO_MATCH 时非空
 */
public record AgentGuideResponseDTO(Type type, String speech, List<Candidate> candidates, List<AgentTopicDTO> topics) {

    public enum Type { RECOMMEND, CLARIFY, NO_MATCH }

    /**
     * @param route 服务端生成的前端路由，前端直接 router.push，不要自己拼
     * @param image 站内图片 URL（已校验文件存在），可为 null
     */
    public record Candidate(Integer lessonId, String title, String themeName, String image, String route) { }
}
