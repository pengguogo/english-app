package com.englishapp.dto;

/** 当前登录家长与孩子 */
public record MeResponseDTO(Long familyId, String email, boolean consented, String consentVersion,
                            Integer currentChildId, ChildResponseDTO currentChild) { }
