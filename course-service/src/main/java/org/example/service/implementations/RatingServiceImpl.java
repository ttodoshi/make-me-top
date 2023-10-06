package org.example.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.repository.AuthorizationHeaderRepository;
import org.example.service.RatingService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {
    private final WebClient.Builder webClientBuilder;
    private final AuthorizationHeaderRepository authorizationHeaderRepository;

    @Override
    public Map<Integer, Double> getPeopleRatingAsKeeperByPersonIdIn(List<Integer> personIds) {
        return webClientBuilder
                .baseUrl("http://feedback-service/api/v1/feedback-app/").build()
                .get()
                .uri(uri -> uri
                        .path("person/rating/")
                        .queryParam("personIds", personIds)
                        .queryParam("as", "keeper")
                        .build()
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(new ParameterizedTypeReference<Map<Integer, Double>>() {
                })
                .timeout(Duration.ofSeconds(5))
                .blockLast();
    }

    @Override
    public Map<Integer, Double> getPeopleRatingAsExplorerByPersonIdIn(List<Integer> personIds) {
        return webClientBuilder
                .baseUrl("http://feedback-service/api/v1/feedback-app/").build()
                .get()
                .uri(uri -> uri
                        .path("person/rating/")
                        .queryParam("personIds", personIds)
                        .queryParam("as", "explorer")
                        .build()
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(new ParameterizedTypeReference<Map<Integer, Double>>() {
                })
                .timeout(Duration.ofSeconds(5))
                .blockLast();
    }
}
