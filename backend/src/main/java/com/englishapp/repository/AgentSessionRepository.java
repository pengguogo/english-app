package com.englishapp.repository;

import com.englishapp.domain.AgentSession;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AgentSessionRepository extends JpaRepository<AgentSession, Long> {

    Optional<AgentSession> findByIdAndFamilyId(Long id, Long familyId);

    /** 家长列表：一律按 familyId 过滤，childId / flagged 只是筛选条件（null 表示不筛） */
    @Query("select s from AgentSession s where s.familyId = :familyId"
            + " and (:childId is null or s.childId = :childId)"
            + " and (:flagged is null or s.flagged = :flagged)"
            + " order by s.updatedAt desc, s.id desc")
    Slice<AgentSession> search(@Param("familyId") Long familyId, @Param("childId") Integer childId,
                              @Param("flagged") Boolean flagged, Pageable pageable);

    List<AgentSession> findByCreatedAtBefore(LocalDateTime cutoff);

    List<AgentSession> findByChildIdIn(Collection<Integer> childIds);
}
