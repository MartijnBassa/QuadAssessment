package com.quad.trivia.backend.dto.response;

import java.util.List;

public record QuizResponse(String quizId, List<QuizDisplayDto> results) {}
