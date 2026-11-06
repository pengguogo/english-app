package com.englishapp.repository;

import com.englishapp.domain.GuardianCredential;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuardianCredentialRepository extends JpaRepository<GuardianCredential, Long> {
    Optional<GuardianCredential> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByFamilyId(Long familyId);
}
