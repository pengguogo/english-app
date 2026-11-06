package com.englishapp.repository;

import com.englishapp.domain.AgentMessage;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AgentMessageRepository extends JpaRepository<AgentMessage, Long> {

    List<AgentMessage> findBySessionIdOrderByIdAsc(Long sessionId);

    long countBySessionIdAndRole(Long sessionId, String role);

    /** 某些会话里出现过讲解的主题（学习状态 SEEN 的依据） */
    List<AgentMessage> findBySessionIdInAndThemeIdIsNotNull(Collection<Long> sessionIds);

    @Modifying
    @Query("delete from AgentMessage m where m.sessionId in :ids")
    void deleteBySessionIds(@Param("ids") Collection<Long> ids);
}
