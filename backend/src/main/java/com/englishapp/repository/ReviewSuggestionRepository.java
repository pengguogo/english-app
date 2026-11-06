package com.englishapp.repository;

import com.englishapp.domain.ReviewSuggestion;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewSuggestionRepository extends JpaRepository<ReviewSuggestion, Long> {

    List<ReviewSuggestion> findByChildIdAndThemeId(Integer childId, Integer themeId);

    List<ReviewSuggestion> findByChildIdInAndStatus(Collection<Integer> childIds, String status);
}
