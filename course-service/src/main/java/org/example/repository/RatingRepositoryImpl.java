package org.example.repository;

import lombok.RequiredArgsConstructor;
import org.example.exception.classes.connectEX.ConnectException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RatingRepositoryImpl implements RatingRepository {
    private final AuthorizationHeaderRepository authorizationHeaderRepository;
    @Value("${info_app_url}")
    private String INFO_APP_URL;

    @Override
    public Double getExplorerRating(Integer personId) {
        WebClient webClient = WebClient.create(INFO_APP_URL);
        return webClient.get()
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
        WebClient webClient = WebClient.create(INFO_APP_URL);
        return webClient.get()
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
