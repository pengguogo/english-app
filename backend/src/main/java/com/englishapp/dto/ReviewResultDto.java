package com.englishapp.dto;

import java.util.List;

public record ReviewResultDto(boolean passed, List<Integer> wrongIndexes, int creditedSeconds) {}
