package com.englishapp.repository;

import com.englishapp.domain.GuardianConsent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuardianConsentRepository extends JpaRepository<GuardianConsent, Long> {
    boolean existsByFamilyIdAndConsentVersion(Long familyId, String consentVersion);
}
