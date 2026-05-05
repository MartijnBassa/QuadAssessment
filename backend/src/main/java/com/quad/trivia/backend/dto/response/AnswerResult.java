package com.quad.trivia.backend.dto.response;

public record AnswerResult(int questionIndex, boolean correct, String correctAnswer) {}
