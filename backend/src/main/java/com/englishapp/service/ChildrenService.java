package com.englishapp.service;

import com.englishapp.dto.ChildRequestDTO;
import com.englishapp.dto.ChildResponseDTO;
import java.util.List;

/** 本家庭孩子档案（app_user） */
public interface ChildrenService {
    List<ChildResponseDTO> list(Long familyId);

    ChildResponseDTO create(Long familyId, ChildRequestDTO request);

    /** 孩子不属于该家庭时抛 NoSuchElementException（404） */
    ChildResponseDTO requireOwned(Long familyId, Integer childId);
}
