package com.englishapp.voice.dto;

/**
 * 发音评测结果
 * <p>
 * 封装发音评测返回的分数与反馈文案,用于 /score 接口响应。
 * </p>
 *
 * @author englishapp
 * @since 1.0.0
 */
public class ScoreResponse {
    /** 评测分数,范围 0-100 */
    private Integer score;
    /** 反馈文案,根据分数分级生成鼓励语 */
    private String feedback;

    /**
     * 构造发音评测结果
     *
     * @param score    分数(0-100)
     * @param feedback 反馈文案
     */
    public ScoreResponse(Integer score, String feedback) {
        this.score = score;
        this.feedback = feedback;
    }

    /**
     * 获取分数
     *
     * @return 分数(0-100)
     */
    public Integer getScore() {
        return score;
    }

    /**
     * 获取反馈文案
     *
     * @return 反馈文案
     */
    public String getFeedback() {
        return feedback;
    }
}
