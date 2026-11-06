package com.englishapp.repository;

import com.englishapp.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Integer> {
    /** 本家庭孩子档案，按创建顺序 */
    List<AppUser> findByFamilyIdOrderByIdAsc(Long familyId);

    /** 孩子必须属于指定家庭 */
    Optional<AppUser> findByIdAndFamilyId(Integer id, Long familyId);
}
