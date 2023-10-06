package org.example.repository;

import lombok.RequiredArgsConstructor;
import org.example.dto.starsystem.GetStarSystemWithDependenciesDto;
import org.example.dto.starsystem.StarSystemDto;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.exception.classes.galaxyEX.GalaxyNotFoundException;
import org.example.repository.AuthorizationHeaderRepository;
import org.example.repository.StarSystemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StarSystemRepositoryImpl implements StarSystemRepository {
    private final AuthorizationHeaderRepository authorizationHeaderRepository;
    private final WebClient.Builder webClientBuilder;

    public List<StarSystemDto> getSystemsByGalaxyId(Integer galaxyId) {
        return webClientBuilder
                .baseUrl("http://galaxy-service/api/v1/galaxy-app/").build()
                .get()
                .uri(uri -> uri
                        .path("galaxies/{galaxyId}/systems/")
                        .build(galaxyId)
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, response -> {
                    throw new GalaxyNotFoundException(galaxyId);
                })
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(StarSystemDto.class)
                .timeout(Duration.ofSeconds(5))
                .collectList()
                .block();
    }

    @Override
    public GetStarSystemWithDependenciesDto getStarSystemWithDependencies(Integer systemId) {
        return webClientBuilder
                .baseUrl("http://galaxy-service/api/v1/galaxy-app/").build()
                .get()
                .uri(uri -> uri
                        .path("systems/{systemId}/")
                        .queryParam("withDependencies", true)
                        .build(systemId)
                )
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, response -> {
                    throw new CourseNotFoundException(systemId);
                })
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToMono(GetStarSystemWithDependenciesDto.class)
                .timeout(Duration.ofSeconds(5))
                .block();
    }
}
