package com.englishapp.repository;

import com.englishapp.domain.AgentInteraction;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AgentInteractionRepository extends JpaRepository<AgentInteraction, Long> {

    Optional<AgentInteraction> findByQuizTokenHashAndChildId(String quizTokenHash, Integer childId);

    List<AgentInteraction> findBySessionIdOrderByIdAsc(Long sessionId);

    List<AgentInteraction> findBySessionIdIn(Collection<Long> sessionIds);

    /** 某孩子某主题已作答的题目，按作答顺序（未作答的行不计入状态） */
    List<AgentInteraction> findByChildIdAndThemeIdAndChosenIsNotNullOrderByIdAsc(Integer childId, Integer themeId);

    @Modifying
    @Query("delete from AgentInteraction i where i.sessionId in :ids")
    void deleteBySessionIds(@Param("ids") Collection<Long> ids);
}
