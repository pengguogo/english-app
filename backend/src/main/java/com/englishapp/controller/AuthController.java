package com.englishapp.controller;

import com.englishapp.common.Result;
import com.englishapp.dto.AuthRequestDTO;
import com.englishapp.dto.ConsentRequestDTO;
import com.englishapp.dto.MeResponseDTO;
import com.englishapp.dto.ParentVerifyRequestDTO;
import com.englishapp.dto.ParentVerifyResponseDTO;
import com.englishapp.security.AuthPrincipal;
import com.englishapp.security.SessionCurrentChild;
import com.englishapp.security.SessionLogin;
import com.englishapp.service.AuthService;
import com.englishapp.service.ParentPasswordService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.time.Clock;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

/** 家长账号：注册、登录、退出、当前身份、监护人同意、家长二次验证 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService auth;
    private final SessionLogin sessions;
    private final SessionCurrentChild current;
    private final ParentPasswordService parentPasswords;
    private final Clock clock;

    public AuthController(AuthService auth, SessionLogin sessions, SessionCurrentChild current,
                          ParentPasswordService parentPasswords, Clock clock) {
        this.auth = auth;
        this.sessions = sessions;
        this.current = current;
        this.parentPasswords = parentPasswords;
        this.clock = clock;
    }

    @PostMapping("/register")
    public Result<MeResponseDTO> register(@Valid @RequestBody AuthRequestDTO request,
                                          HttpServletRequest req, HttpServletResponse res) {
        AuthPrincipal p = auth.register(request);
        sessions.login(p, req, res);
        return Result.success(auth.me(p, null));
    }

    @PostMapping("/login")
    public Result<MeResponseDTO> login(@Valid @RequestBody AuthRequestDTO request,
                                       HttpServletRequest req, HttpServletResponse res) {
        AuthPrincipal p = auth.login(request);
        sessions.login(p, req, res);
        return Result.success(auth.me(p, null));
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest req) {
        sessions.logout(req);
        return Result.success(null);
    }

    /** 公开：未登录 data=null；读取一次 CsrfToken，确保下发 XSRF-TOKEN cookie */
    @GetMapping("/me")
    public Result<MeResponseDTO> me(CsrfToken csrf) {
        csrf.getToken();
        return Result.success(current.optionalPrincipal()
                .map(p -> auth.me(p, current.currentChildId().orElse(null))).orElse(null));
    }

    @PostMapping("/consent")
    public Result<MeResponseDTO> consent(@Valid @RequestBody ConsentRequestDTO request) {
        AuthPrincipal p = current.principal();
        auth.consent(p, request.version());
        return Result.success(auth.me(p, current.currentChildId().orElse(null)));
    }

    @PostMapping("/parent-verify")
    public Result<ParentVerifyResponseDTO> parentVerify(@Valid @RequestBody ParentVerifyRequestDTO request) {
        parentPasswords.verify(request.password());
        return Result.success(new ParentVerifyResponseDTO(
                clock.instant().plus(SessionCurrentChild.PARENT_VERIFY_TTL)));
    }
}
