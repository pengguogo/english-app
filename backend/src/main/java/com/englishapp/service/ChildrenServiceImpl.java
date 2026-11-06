package com.englishapp.service;

import com.englishapp.domain.AppUser;
import com.englishapp.dto.ChildRequestDTO;
import com.englishapp.dto.ChildResponseDTO;
import com.englishapp.repository.AppUserRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChildrenServiceImpl implements ChildrenService {
    private final AppUserRepository users;
    private final Clock clock;

    public ChildrenServiceImpl(AppUserRepository users, Clock clock) {
        this.users = users;
        this.clock = clock;
    }

    @Override
    public List<ChildResponseDTO> list(Long familyId) {
        return users.findByFamilyIdOrderByIdAsc(familyId).stream().map(ChildrenServiceImpl::toDto).toList();
    }

    @Override
    @Transactional
    public ChildResponseDTO create(Long familyId, ChildRequestDTO request) {
        AppUser child = new AppUser();
        child.setFamilyId(familyId);
        child.setNickname(request.nickname().trim());
        child.setAvatarUrl(request.avatarUrl());
        child.setCreatedAt(LocalDateTime.now(clock));
        return toDto(users.save(child));
    }

    @Override
    public ChildResponseDTO requireOwned(Long familyId, Integer childId) {
        return users.findByIdAndFamilyId(childId, familyId).map(ChildrenServiceImpl::toDto)
                .orElseThrow(() -> new NoSuchElementException("孩子不存在"));
    }

    private static ChildResponseDTO toDto(AppUser u) {
        return new ChildResponseDTO(u.getId(), u.getNickname(), u.getAvatarUrl());
    }
}
