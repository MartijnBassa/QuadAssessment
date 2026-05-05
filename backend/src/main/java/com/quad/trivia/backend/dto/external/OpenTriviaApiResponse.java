package com.quad.trivia.backend.dto.external;

import com.quad.trivia.backend.dto.request.QuestionDto;

import java.util.List;

//what we get from the open trivia api
public record OpenTriviaApiResponse(
        int response_code,
        List<QuestionDto> results
) {}
