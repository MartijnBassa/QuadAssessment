package com.quad.trivia.backend.client;

import com.quad.trivia.backend.dto.external.OpenTriviaApiResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class OpenTriviaClient {

    private final RestTemplate restTemplate = new RestTemplate();

    public OpenTriviaApiResponse fetchQuestions(
            int amount,
            Integer category,
            String difficulty,
            String type
    ) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("https://opentdb.com/api.php").queryParam("amount", amount);

        if (category != null) builder.queryParam("category", category);
        if (difficulty != null) builder.queryParam("difficulty", difficulty);
        if (type != null) builder.queryParam("type", type);

        return restTemplate.getForObject(builder.toUriString(), OpenTriviaApiResponse.class);
    }
}
