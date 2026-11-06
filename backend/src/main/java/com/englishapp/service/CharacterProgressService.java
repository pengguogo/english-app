package com.englishapp.service;

import com.englishapp.dto.*;
import java.util.List;

public interface CharacterProgressService {
    List<CharacterProgressResponseDTO> progress(Integer childId);
    List<CharacterItemResponseDTO> review(Integer childId);
    void record(Integer childId, CharacterAttemptRequestDTO request);
}
