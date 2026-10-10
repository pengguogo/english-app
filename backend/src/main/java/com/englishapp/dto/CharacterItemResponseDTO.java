package com.englishapp.dto;

public record CharacterItemResponseDTO(int lessonId, int itemIndex, String word, String phonetic,
        String image, String exampleWord, String exampleSentence, boolean imageChoice, String readingNote) {}
