package com.englishapp.service;

import com.englishapp.domain.Subject;
import com.englishapp.dto.SubjectDto;
import com.englishapp.repository.SubjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 学科服务实现
 *
 * @author TRAE Agent
 * @since 2026-07-21
 */
@Service
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;

    /**
     * 构造学科服务
     * @param subjectRepository 学科数据访问接口
     */
    public SubjectServiceImpl(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    @Override
    public List<SubjectDto> getAllSubjects() {
        return subjectRepository.findAllByOrderBySortOrderAsc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * 实体转 DTO
     * @param subject 学科实体
     * @return 学科 DTO
     */
    private SubjectDto toDto(Subject subject) {
        return new SubjectDto(
                subject.getId(),
                subject.getName(),
                subject.getCode(),
                subject.getIconUrl(),
                subject.getColor(),
                subject.getSortOrder(),
                subject.getIsLocked()
        );
    }
}
