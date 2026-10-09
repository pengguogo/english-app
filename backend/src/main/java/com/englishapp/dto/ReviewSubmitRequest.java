package com.englishapp.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ReviewSubmitRequest(@NotNull List<Integer> answers) {}
