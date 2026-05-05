package com.quad.trivia.backend.dto.request;

import java.util.Map;

public record SubmitAnswersRequest(String quizId, Map<Integer, String> answers) {}
