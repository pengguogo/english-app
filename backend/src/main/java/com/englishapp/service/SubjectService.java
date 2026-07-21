package com.englishapp.service;

import com.englishapp.dto.SubjectDto;

import java.util.List;

/**
 * 学科服务接口
 *
 * @author TRAE Agent
 * @since 2026-07-21
 */
public interface SubjectService {

    /**
     * 获取所有学科（按 sortOrder 升序）
     * @return 学科 DTO 列表
     */
    List<SubjectDto> getAllSubjects();
}
