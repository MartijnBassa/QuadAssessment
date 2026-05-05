package com.quad.trivia.backend;

import com.quad.trivia.backend.controller.TriviaController;
import com.quad.trivia.backend.dto.request.SubmitAnswersRequest;
import com.quad.trivia.backend.dto.response.AnswerResult;
import com.quad.trivia.backend.dto.response.CheckAnswersResponse;
import com.quad.trivia.backend.dto.response.QuizDisplayDto;
import com.quad.trivia.backend.dto.response.QuizResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TriviaService {

    @Mock
    private com.quad.trivia.backend.service.TriviaService triviaService;

    @InjectMocks
    private TriviaController triviaController;

    @Test
    void getQuestions_returnsQuizResponse() {
        List<QuizDisplayDto> questions = List.of(
            new QuizDisplayDto("Geography",
                "Route 66 in the United States spans the entire mainland of America, from California to New York.",
                List.of("True", "False"), "medium", "boolean"),
            new QuizDisplayDto("General Knowledge",
                "When did the website \"Facebook\" launch?",
                List.of("2006", "2005", "2003", "2004"), "medium", "multiple")
        );
        when(triviaService.getQuestions(2, null, null, null))
                .thenReturn(new QuizResponse("test-quiz-id", questions));

        QuizResponse response = triviaController.getQuestions(2, null, null, null);

        assertEquals("test-quiz-id", response.quizId());
        assertEquals(2, response.results().size());
        assertEquals("Geography", response.results().get(0).category());
    }

    @Test
    void checkAnswers_returnsResultsAndScore() {
        List<AnswerResult> results = List.of(
            new AnswerResult(0, false, "False"),
            new AnswerResult(1, true, "2004")
        );
        SubmitAnswersRequest request = new SubmitAnswersRequest("test-quiz-id", Map.of(0, "True", 1, "2004"));
        when(triviaService.checkAnswers(request))
                .thenReturn(new CheckAnswersResponse(results, 1));

        CheckAnswersResponse response = triviaController.checkAnswers(request);

        assertEquals(1, response.score());
        assertEquals(2, response.results().size());
        assertEquals("False", response.results().get(0).correctAnswer());
    }
}
