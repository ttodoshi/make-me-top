package org.example.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.repository.custom.AuthorizationHeaderRepository;
import org.example.service.RatingService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {
    private final AuthorizationHeaderRepository authorizationHeaderRepository;

    private final WebClient.Builder webClientBuilder;

    @Override
    public Double getExplorerRating(Integer personId) {
        return webClientBuilder
                .baseUrl("http://person-information-service/info/").build()
                .get()
                .uri("explorer/" + personId + "/rating/")
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToMono(Double.class)
                .timeout(Duration.ofSeconds(5))
                .block();
    }

    @Override
    public Double getKeeperRating(Integer personId) {
        return webClientBuilder
                .baseUrl("http://person-information-service/info/").build()
                .get()
                .uri("keeper/" + personId + "/rating/")
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToMono(Double.class)
                .timeout(Duration.ofSeconds(5))
                .block();
    }
}
