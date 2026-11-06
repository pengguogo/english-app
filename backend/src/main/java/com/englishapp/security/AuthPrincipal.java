package com.englishapp.security;

import java.io.Serializable;

/** 登录后存入 SecurityContext（随 Session 保存）的家长身份 */
public record AuthPrincipal(Long guardianId, Long familyId, String email) implements Serializable {}
