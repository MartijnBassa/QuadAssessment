package com.quad.trivia.backend.dto.response;

import java.util.List;

public record CheckAnswersResponse(List<AnswerResult> results, int score) {}
