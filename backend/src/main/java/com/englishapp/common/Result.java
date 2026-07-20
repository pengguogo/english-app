package com.englishapp.common;

/**
 * 统一 API 响应格式
 * <p>
 * 所有 Controller 接口返回均使用本类包装,统一响应结构为:
 * </p>
 * <pre>{@code
 * {
 *   "code": 200,
 *   "message": "success",
 *   "data": { ... }
 * }
 * }</pre>
 *
 * @param <T> 业务数据类型
 * @author englishapp
 * @since 1.0.0
 */
public class Result<T> {
    private final int code;
    private final String message;
    private final T data;

    /**
     * 构造统一响应对象
     *
     * @param code    状态码
     * @param message 提示信息
     * @param data    业务数据
     */
    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 构建成功响应
     *
     * @param data 业务数据
     * @param <T>  业务数据类型
     * @return 包含成功状态码与数据的 Result 对象
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    /**
     * 构建错误响应
     *
     * @param resultCode 错误状态码枚举
     * @param <T>        业务数据类型
     * @return 包含错误状态码的 Result 对象,data 为 null
     */
    public static <T> Result<T> error(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), null);
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
     * @return 提示信息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 获取业务数据
     *
     * @return 业务数据
     */
    public T getData() {
        return data;
    }
}
