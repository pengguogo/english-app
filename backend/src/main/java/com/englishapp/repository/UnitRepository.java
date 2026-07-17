package com.englishapp.repository;

import com.englishapp.domain.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Integer> {
    List<Unit> findByThemeIdOrderBySortOrderAsc(Integer themeId);
}
