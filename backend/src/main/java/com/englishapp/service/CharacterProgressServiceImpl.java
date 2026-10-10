package com.englishapp.service;

import com.englishapp.domain.CharacterProgress;
import com.englishapp.dto.*;
import com.englishapp.repository.CharacterProgressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CharacterProgressServiceImpl implements CharacterProgressService {
    private final CharacterCatalog catalog;
    private final CharacterProgressRepository repository;

    public CharacterProgressServiceImpl(CharacterCatalog catalog, CharacterProgressRepository repository) {
        this.catalog = catalog;
        this.repository = repository;
    }

    @Override
    public List<CharacterProgressResponseDTO> progress() {
        Map<String, CharacterProgress> saved = repository.findAll().stream()
                .collect(Collectors.toMap(CharacterProgress::word, Function.identity()));
        return catalog.items().stream().map(item -> {
            var p = saved.get(item.word());
            return p == null ? new CharacterProgressResponseDTO(item, "NEW", 0, 0, 0, "", null)
                    : new CharacterProgressResponseDTO(item, p.status(), p.independentDays(), p.wrongCount(),
                            p.assistedCount(), p.lastOutcome(), p.dueDate());
        }).toList();
    }

    @Override
    public List<CharacterItemResponseDTO> review() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Shanghai"));
        return progress().stream().filter(p -> p.dueDate() != null && !p.dueDate().isAfter(today))
                .sorted(Comparator.comparingInt((CharacterProgressResponseDTO p) -> "WRONG".equals(p.lastOutcome()) ? 0
                        : "ASSISTED".equals(p.lastOutcome()) ? 1 : 2).thenComparing(CharacterProgressResponseDTO::dueDate))
                .limit(8).map(CharacterProgressResponseDTO::item).toList();
    }

    @Override
    @Transactional
    public void record(CharacterAttemptRequestDTO request) {
        var item = catalog.items().stream().filter(i -> i.lessonId() == request.lessonId()
                && i.itemIndex() == request.itemIndex()).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不是本批汉字学习项"));
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Shanghai"));
        if (!repository.insertAttempt(request.eventId(), item.word(), request.outcome().name(), today)) return;
        var previous = repository.findAll().stream().filter(p -> p.word().equals(item.word())).findFirst().orElse(null);
        repository.save(CharacterReviewPolicy.apply(previous, item.word(), request.outcome(), today));
    }
}
