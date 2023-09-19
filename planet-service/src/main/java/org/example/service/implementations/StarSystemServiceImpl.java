package org.example.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.dto.starsystem.StarSystemDto;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.systemEX.SystemNotFoundException;
import org.example.repository.AuthorizationHeaderRepository;
import org.example.service.StarSystemService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class StarSystemServiceImpl implements StarSystemService {
    private final AuthorizationHeaderRepository authorizationHeaderRepository;
    private final WebClient.Builder webClientBuilder;

    @Override
    public void checkSystemExists(Integer systemId) {
        webClientBuilder
                .baseUrl("http://galaxy-service/api/v1/galaxy-app/").build()
                .get()
                .uri(uri -> uri
                        .path("system/{systemId}/")
                        .build(systemId)
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, response -> {
                    throw new SystemNotFoundException(systemId);
                })
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToMono(StarSystemDto.class)
                .timeout(Duration.ofSeconds(5))
                .block();
    }
}
