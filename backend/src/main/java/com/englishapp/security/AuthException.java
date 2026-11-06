package com.englishapp.security;

import com.englishapp.common.ResultCode;

/** 身份与权限相关业务异常，HTTP 状态由 ResultCode 推导 */
public class AuthException extends RuntimeException {
    private final ResultCode resultCode;

    public AuthException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }

    public ResultCode getResultCode() { return resultCode; }

    /** 4 位业务码取前 3 位作为 HTTP 状态，如 4091 → 409 */
    public int httpStatus() {
        int code = resultCode.getCode();
        return code >= 1000 ? code / 10 : code;
    }
}
