package com.quad.trivia.backend.exception;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum TriviaErrorCode {

    NO_RESULTS(1, "No results. Not enough questions."),
    INVALID_PARAM(2, "Invalid parameter."),
    TOKEN_NOT_FOUND(3, "Session token not found."),
    TOKEN_EMPTY(4, "Token empty."),
    RATE_LIMIT(5, "Too many requests.");

    public final int code;
    public final String message;

    private static final Map<Integer, String> BY_CODE = Arrays.stream(values())
            .collect(Collectors.toMap(e -> e.code, e -> e.message));

    TriviaErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String fromCode(int code) {
        return BY_CODE.getOrDefault(code, "Unknown error.");
    }
}
