package com.quad.trivia.backend.controller;

import com.quad.trivia.backend.dto.request.SubmitAnswersRequest;
import com.quad.trivia.backend.dto.response.CheckAnswersResponse;
import com.quad.trivia.backend.dto.response.QuizResponse;
import com.quad.trivia.backend.service.TriviaService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TriviaController {

    private final TriviaService triviaService;

    public TriviaController(TriviaService triviaService) {
        this.triviaService = triviaService;
    }

    @GetMapping("/questions")
    public QuizResponse getQuestions(
            @RequestParam int amount,
            @RequestParam(required = false) Integer category,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String type
    ) {
        return triviaService.getQuestions(amount, category, difficulty, type);
    }

    @PostMapping("/checkanswers")
    public CheckAnswersResponse checkAnswers(@RequestBody SubmitAnswersRequest request) {
        return triviaService.checkAnswers(request);
    }
}
