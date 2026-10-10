package com.englishapp.service;

import com.englishapp.domain.CharacterProgress;
import com.englishapp.dto.CharacterAttemptRequestDTO;
import com.englishapp.dto.CharacterAttemptRequestDTO.Outcome;
import com.englishapp.dto.CharacterItemResponseDTO;
import com.englishapp.repository.CharacterProgressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CharacterProgressServiceImplTest {
    CharacterCatalog catalog = mock(CharacterCatalog.class);
    CharacterProgressRepository repository = mock(CharacterProgressRepository.class);
    CharacterProgressServiceImpl service = new CharacterProgressServiceImpl(catalog, repository);
    LocalDate today = LocalDate.now(ZoneId.of("Asia/Shanghai"));
    CharacterItemResponseDTO mountain = new CharacterItemResponseDTO(68, 0, "山", "shān", "/images/hanzi-pilot/hanzi-shan.jpg", "小山", "小山很高。", true, "");
    CharacterItemResponseDTO water = new CharacterItemResponseDTO(68, 1, "水", "shuǐ", "/images/hanzi-pilot/hanzi-shui.jpg", "河水", "河水流过小山。", true, "");
    @BeforeEach
    void setup() {
        when(catalog.items()).thenReturn(List.of(mountain, water));
        when(repository.findAll()).thenReturn(List.of());
    }
    @Test
    void should_显示未学习而非掌握_当_没有按字记录() {
        var result = service.progress();
        assertEquals(2, result.size());
        assertEquals("NEW", result.get(0).status());
        assertNull(result.get(0).dueDate());
    }
    @Test
    void should_优先复习错字且不包含未来与未学字_当_读取复习队列() {
        when(repository.findAll()).thenReturn(List.of(
                new CharacterProgress("山", 1, today.minusDays(1), 0, 0, "INDEPENDENT", today),
                new CharacterProgress("水", 0, null, 1, 0, "WRONG", today)));
        assertEquals(List.of(water, mountain), service.review());
        when(repository.findAll()).thenReturn(List.of(
                new CharacterProgress("山", 1, today, 0, 0, "INDEPENDENT", today.plusDays(1))));
        assertTrue(service.review().isEmpty());
    }
    @Test
    void should_只更新一次_当_网络重试重复事件() {
        var request = new CharacterAttemptRequestDTO("event-1", 68, 0, Outcome.INDEPENDENT);
        when(repository.insertAttempt("event-1", "山", "INDEPENDENT", today)).thenReturn(true, false);
        service.record(request);
        service.record(request);
        verify(repository, times(1)).save(any());
    }
    @Test
    void should_拒绝非法课时及索引_当_提交认字结果() {
        assertThrows(IllegalArgumentException.class,
                () -> service.record(new CharacterAttemptRequestDTO("bad", 62, 0, Outcome.INDEPENDENT)));
        assertThrows(IllegalArgumentException.class,
                () -> service.record(new CharacterAttemptRequestDTO("bad", 68, 99, Outcome.INDEPENDENT)));
        verify(repository, never()).insertAttempt(anyString(), anyString(), anyString(), any());
    }
}
