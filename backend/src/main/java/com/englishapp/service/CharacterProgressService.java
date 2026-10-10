package com.englishapp.service;

import com.englishapp.dto.*;
import java.util.List;

public interface CharacterProgressService {
    List<CharacterProgressResponseDTO> progress();
    List<CharacterItemResponseDTO> review();
    void record(CharacterAttemptRequestDTO request);
}
