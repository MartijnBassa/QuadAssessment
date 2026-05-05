package com.quad.trivia.backend.dto.request;

import java.util.List;

//one question from the trivia api, with the correct answer
public record QuestionDto(
        String category,
        String question,
        String correct_answer,
        List<String> incorrect_answers,
        String difficulty,
        String type
) {}
