package com.englishapp.security;

import com.englishapp.common.ResultCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Clock;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfException;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

/**
 * 鉴权配置：服务端 Session + Cookie CSRF（XSRF-TOKEN / X-XSRF-TOKEN）。
 * 公开：静态资源、图片、/api/v1/auth/**、课程浏览 GET、TTS；其余 /api/v1/** 需登录。
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, SecurityContextRepository contexts,
                                                   ObjectMapper mapper) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
            .securityContext(sc -> sc.securityContextRepository(contexts))
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .formLogin(f -> f.disable())
            .httpBasic(b -> b.disable())
            .logout(l -> l.disable())
            .requestCache(rc -> rc.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET,
                        "/api/v1/subjects/**", "/api/v1/themes/**", "/api/v1/units/**",
                        "/api/v1/lessons/**", "/api/v1/learning/**",
                        "/api/v1/progress/unit/**", "/api/v1/progress/lesson/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/voice/tts").permitAll()
                // 游客可提交课后复习：只判分不落库（Controller 按 currentChildId 分支），CSRF 照常
                .requestMatchers(HttpMethod.POST, "/api/v1/lessons/*/review").permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll())
            .exceptionHandling(e -> e
                .authenticationEntryPoint((req, res, ex) -> write(res, mapper, 401, ResultCode.UNAUTHORIZED))
                .accessDeniedHandler((req, res, ex) -> write(res, mapper, 403,
                        ex instanceof CsrfException ? ResultCode.CSRF_INVALID : ResultCode.FORBIDDEN)));
        return http.build();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    /** 锁定与二次验证过期判断统一用此时钟，测试可替换 */
    @Bean
    public Clock clock() { return Clock.systemDefaultZone(); }

    private static void write(HttpServletResponse res, ObjectMapper mapper, int status, ResultCode code)
            throws IOException {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", code.getCode());
        body.put("message", code.getMessage());
        body.put("data", null);
        res.setStatus(status);
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding("UTF-8");
        res.setHeader("Cache-Control", "no-store");
        mapper.writeValue(res.getWriter(), body);
    }
}
