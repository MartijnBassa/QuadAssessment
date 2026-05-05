package com.quad.trivia.backend.service;

import com.quad.trivia.backend.client.OpenTriviaClient;
import com.quad.trivia.backend.dto.external.OpenTriviaApiResponse;
import com.quad.trivia.backend.dto.request.QuestionDto;
import com.quad.trivia.backend.dto.request.SubmitAnswersRequest;
import com.quad.trivia.backend.dto.response.*;
import com.quad.trivia.backend.exception.TriviaErrorCode;
import com.quad.trivia.backend.exception.TriviaException;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TriviaService {

    // Maximum number of active quiz sessions stored in memory at once
    private static final int MAX_QUIZ_SESSIONS = 300;

    private final OpenTriviaClient client;

    // Stores correct answers for each quiz, keyed by the quiz ID
    private final Map<String, List<String>> quizAnswers = new ConcurrentHashMap<>();

    public TriviaService(OpenTriviaClient client) {
        this.client = client;
    }

    public QuizResponse getQuestions(int amount, Integer category, String difficulty, String type) {
        OpenTriviaApiResponse apiResponse = client.fetchQuestions(amount, category, difficulty, type);

        if (apiResponse.response_code() != 0) {
            throw new TriviaException(TriviaErrorCode.fromCode(apiResponse.response_code()));
        }

        //ID to link a submitted quiz to the correct answers
        String quizId = UUID.randomUUID().toString();
        List<String> correctAnswers = new ArrayList<>();
        List<QuizDisplayDto> displayQuestions = new ArrayList<>();

        for (QuestionDto q : apiResponse.results()) {
            QuestionDto decoded = decodeQuestion(q);

            // Save the correct answer so we can check it later when the user submits
            correctAnswers.add(decoded.correct_answer());

            // Build the list of options the user will see (correct answer is hidden inside)
            List<String> options = buildOptions(decoded);

            QuizDisplayDto displayQuestion = new QuizDisplayDto(
                    decoded.category(),
                    decoded.question(),
                    options,
                    decoded.difficulty(),
                    decoded.type()
            );
            displayQuestions.add(displayQuestion);
        }

        // If we're getting too many sessions, remove one to free up space
        if (quizAnswers.size() >= MAX_QUIZ_SESSIONS) {
            String keyToRemove = quizAnswers.keySet().iterator().next();
            quizAnswers.remove(keyToRemove);
        }

        quizAnswers.put(quizId, correctAnswers);
        return new QuizResponse(quizId, displayQuestions);
    }

    public CheckAnswersResponse checkAnswers(SubmitAnswersRequest request) {
        List<String> correctAnswers = quizAnswers.get(request.quizId());

        if (correctAnswers == null) {
            throw new TriviaException("Quiz session not found or expired.");
        }

        List<AnswerResult> results = new ArrayList<>();
        int score = 0;

        for (int i = 0; i < correctAnswers.size(); i++) {
            String correctAnswer = correctAnswers.get(i);
            String submittedAnswer = request.answers().get(i);

            boolean isCorrect = correctAnswer.equals(submittedAnswer);

            if (isCorrect) {
                score++;
            }

            results.add(new AnswerResult(i, isCorrect, correctAnswer));
        }

        return new CheckAnswersResponse(results, score);
    }

    // Builds the list of answer options shown to the user.
    // Boolean questions always show True then False.
    // Multiple choice questions are shuffled so the correct answer isn't always last.
    private List<String> buildOptions(QuestionDto q) {
        if ("boolean".equals(q.type())) {
            return List.of("True", "False");
        }

        List<String> options = new ArrayList<>(q.incorrect_answers());
        options.add(q.correct_answer());
        Collections.shuffle(options);
        return options;
    }

    // Decodes HTML entities in all text fields.
    // The trivia API sends characters like & as &amp; — this converts them back.
    private QuestionDto decodeQuestion(QuestionDto q) {
        List<String> decodedIncorrectAnswers = new ArrayList<>();
        for (String answer : q.incorrect_answers()) {
            decodedIncorrectAnswers.add(decode(answer));
        }

        return new QuestionDto(
                decode(q.category()),
                decode(q.question()),
                decode(q.correct_answer()),
                decodedIncorrectAnswers,
                q.difficulty(),
                q.type()
        );
    }

    private static String decode(String input) {
        if (input == null) {
            return null;
        }
        return StringEscapeUtils.unescapeHtml4(input);
    }
}
