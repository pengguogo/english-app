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
    /** 未登录 */
    UNAUTHORIZED(401, "请先登录"),
    /** 登录邮箱或密码错误 */
    LOGIN_FAILED(4011, "邮箱或密码错误"),
    /** 登录失败次数过多被锁定 */
    ACCOUNT_LOCKED(4012, "登录失败次数过多，请 15 分钟后再试"),
    /** 无权访问 */
    FORBIDDEN(403, "无权访问"),
    /** CSRF 令牌缺失或无效 */
    CSRF_INVALID(4030, "页面已过期，请刷新后重试"),
    /** 需要家长二次验证 */
    PARENT_VERIFY_REQUIRED(4031, "请先验证家长密码"),
    /** 家长密码错误或已锁定 */
    PARENT_PASSWORD_WRONG(4032, "家长密码不正确"),
    /** 已登录但未选择孩子 */
    CHILD_NOT_SELECTED(4091, "请先选择孩子"),
    /** 邮箱已注册 */
    EMAIL_TAKEN(4092, "该邮箱已注册"),
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
