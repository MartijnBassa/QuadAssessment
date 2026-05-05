package com.quad.trivia.backend.dto.response;

import java.util.List;

public record QuizDisplayDto(
        String category,
        String question,
        List<String> options,
        String difficulty,
        String type
) {}
