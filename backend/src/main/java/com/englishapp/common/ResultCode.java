package com.englishapp.common;

/**
 * 统一响应状态码枚举
 * <p>
 * 定义 API 返回的业务状态码及对应的中文提示信息,
 * 供 {@link Result} 包装响应时使用。
 * </p>
 *
 * @author englishapp
 * @since 1.0.0
 */
public enum ResultCode {
    /** 请求成功 */
    SUCCESS(200, "success"),
    /** 请求参数错误 */
    BAD_REQUEST(400, "请求参数错误"),
    /** 资源不存在 */
    NOT_FOUND(404, "资源不存在"),
    /** 语音服务暂时不可用 */
    VOICE_SERVICE_ERROR(503, "语音服务暂时不可用"),
    /** 服务器内部错误 */
    INTERNAL_ERROR(500, "服务器内部错误");

    private final int code;
    private final String message;

    /**
     * 构造状态码枚举项
     *
     * @param code    HTTP/业务状态码
     * @param message 中文提示信息
     */
    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 获取状态码
     *
     * @return 状态码数值
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取提示信息
     *
     * @return 中文提示信息
     */
    public String getMessage() {
        return message;
    }
}
