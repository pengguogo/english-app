package com.englishapp.repository;

import com.englishapp.domain.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 学科数据访问接口
 *
 * @author TRAE Agent
 * @since 2026-07-21
 */
@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> {

    /**
     * 按排序序号升序获取所有学科
     * @return 排序后的学科列表
     */
    List<Subject> findAllByOrderBySortOrderAsc();
}
