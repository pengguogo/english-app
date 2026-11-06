package com.englishapp.service;

import com.englishapp.dto.AuthRequestDTO;
import com.englishapp.dto.MeResponseDTO;
import com.englishapp.security.AuthPrincipal;

/** 家长账号：注册、登录、当前身份、监护人同意 */
public interface AuthService {
    /** 当前要求同意的说明版本 */
    String CONSENT_VERSION = "2026-11-v1";

    AuthPrincipal register(AuthRequestDTO request);

    AuthPrincipal login(AuthRequestDTO request);

    MeResponseDTO me(AuthPrincipal principal, Integer currentChildId);

    void consent(AuthPrincipal principal, String version);
}
