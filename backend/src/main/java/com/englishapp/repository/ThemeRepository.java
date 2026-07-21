package com.englishapp.repository;

import com.englishapp.domain.Theme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThemeRepository extends JpaRepository<Theme, Integer> {
    List<Theme> findAllByOrderBySortOrderAsc();

    /**
     * 按学科 ID 查询主题，按排序序号升序返回
     * @param subjectId 学科 ID
     * @return 该学科下的主题列表
     */
    List<Theme> findBySubjectIdOrderBySortOrderAsc(Integer subjectId);
}
