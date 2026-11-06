package com.englishapp.service;

import com.englishapp.common.ResultCode;
import com.englishapp.domain.Family;
import com.englishapp.domain.GuardianConsent;
import com.englishapp.domain.GuardianCredential;
import com.englishapp.dto.AuthRequestDTO;
import com.englishapp.dto.ChildResponseDTO;
import com.englishapp.dto.MeResponseDTO;
import com.englishapp.repository.AppUserRepository;
import com.englishapp.repository.FamilyRepository;
import com.englishapp.repository.GuardianConsentRepository;
import com.englishapp.repository.GuardianCredentialRepository;
import com.englishapp.security.AuthException;
import com.englishapp.security.AuthPrincipal;
import com.englishapp.security.GuardianPasswordChecker;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {
    static final long DEFAULT_FAMILY_ID = 1L;

    private final GuardianCredentialRepository credentials;
    private final FamilyRepository families;
    private final GuardianConsentRepository consents;
    private final AppUserRepository children;
    private final GuardianPasswordChecker checker;
    private final PasswordEncoder encoder;
    private final Clock clock;
    private final String ownerEmail;
    /** 未知邮箱也跑一次 BCrypt，避免按响应时间枚举邮箱 */
    private final String dummyHash;

    public AuthServiceImpl(GuardianCredentialRepository credentials, FamilyRepository families,
                           GuardianConsentRepository consents, AppUserRepository children,
                           GuardianPasswordChecker checker, PasswordEncoder encoder, Clock clock,
                           @Value("${app.owner-email:}") String ownerEmail) {
        this.credentials = credentials;
        this.families = families;
        this.consents = consents;
        this.children = children;
        this.checker = checker;
        this.encoder = encoder;
        this.clock = clock;
        this.ownerEmail = normalize(ownerEmail);
        this.dummyHash = encoder.encode("dummy-password-for-timing");
    }

    @Override
    @Transactional
    public AuthPrincipal register(AuthRequestDTO request) {
        String email = normalize(request.email());
        if (credentials.existsByEmail(email)) throw new AuthException(ResultCode.EMAIL_TAKEN);
        GuardianCredential c = new GuardianCredential();
        c.setFamilyId(resolveFamily(email));
        c.setEmail(email);
        c.setPasswordHash(encoder.encode(request.password()));
        c.setFailedAttempts(0);
        c.setCreatedAt(LocalDateTime.now(clock));
        try {
            credentials.saveAndFlush(c);
        } catch (DataIntegrityViolationException e) {
            throw new AuthException(ResultCode.EMAIL_TAKEN);
        }
        return new AuthPrincipal(c.getId(), c.getFamilyId(), email);
    }

    @Override
    public AuthPrincipal login(AuthRequestDTO request) {
        String email = normalize(request.email());
        GuardianCredential c = credentials.findByEmail(email).orElse(null);
        if (c == null) {
            encoder.matches(request.password(), dummyHash);
            throw new AuthException(ResultCode.LOGIN_FAILED);
        }
        switch (checker.check(c.getId(), request.password())) {
            case LOCKED -> throw new AuthException(ResultCode.ACCOUNT_LOCKED);
            case WRONG -> throw new AuthException(ResultCode.LOGIN_FAILED);
            default -> { }
        }
        return new AuthPrincipal(c.getId(), c.getFamilyId(), email);
    }

    @Override
    public MeResponseDTO me(AuthPrincipal p, Integer currentChildId) {
        ChildResponseDTO child = currentChildId == null ? null
                : children.findByIdAndFamilyId(currentChildId, p.familyId())
                    .map(u -> new ChildResponseDTO(u.getId(), u.getNickname(), u.getAvatarUrl())).orElse(null);
        boolean consented = consents.existsByFamilyIdAndConsentVersion(p.familyId(), CONSENT_VERSION);
        return new MeResponseDTO(p.familyId(), p.email(), consented, CONSENT_VERSION,
                child == null ? null : child.id(), child);
    }

    @Override
    @Transactional
    public void consent(AuthPrincipal p, String version) {
        if (!CONSENT_VERSION.equals(version)) throw new IllegalArgumentException("同意版本已过期，请刷新后重新阅读");
        if (consents.existsByFamilyIdAndConsentVersion(p.familyId(), version)) return;
        GuardianConsent consent = new GuardianConsent();
        consent.setFamilyId(p.familyId());
        consent.setGuardianId(p.guardianId());
        consent.setConsentVersion(version);
        consent.setConsentedAt(LocalDateTime.now(clock));
        consents.save(consent);
    }

    /** 站点所有者首次注册认领默认家庭，其余新建家庭 */
    private Long resolveFamily(String email) {
        if (!ownerEmail.isEmpty() && ownerEmail.equals(email) && !credentials.existsByFamilyId(DEFAULT_FAMILY_ID)
                && families.existsById(DEFAULT_FAMILY_ID)) {
            return DEFAULT_FAMILY_ID;
        }
        Family family = new Family();
        family.setCreatedAt(LocalDateTime.now(clock));
        return families.saveAndFlush(family).getId();
    }

    private static String normalize(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}
