package org.example.progress.repository.implementations;

import lombok.RequiredArgsConstructor;
import org.example.progress.dto.galaxy.GetGalaxyDto;
import org.example.progress.exception.classes.connect.ConnectException;
import org.example.progress.repository.GalaxyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class GalaxyRepositoryImpl implements GalaxyRepository {
    private final WebClient.Builder webClientBuilder;

    @Override
    public GetGalaxyDto findGalaxyById(Long galaxyId) {
        return webClientBuilder
                .baseUrl("http://galaxy-service/api/v1/galaxy-app/").build()
                .get()
                .uri(uri -> uri
                        .path("galaxies/{galaxyId}/")
                        .build(galaxyId)
                )
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToMono(GetGalaxyDto.class)
                .timeout(Duration.ofSeconds(5))
                .block();
    }
}
