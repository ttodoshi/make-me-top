package org.example.repository.custom;

import lombok.RequiredArgsConstructor;
import org.example.dto.starsystem.StarSystemDTO;
import org.example.dto.starsystem.StarSystemWithDependenciesGetResponse;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.exception.classes.galaxyEX.GalaxyNotFoundException;
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

    public List<StarSystemDTO> getSystemsByGalaxyId(Integer galaxyId) {
        return webClientBuilder
                .baseUrl("http://galaxy-service/galaxy-app/").build()
                .get()
                .uri("galaxy/" + galaxyId + "/system/")
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, response -> {
                    throw new GalaxyNotFoundException(galaxyId);
                })
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(StarSystemDTO.class)
                .timeout(Duration.ofSeconds(5))
                .collectList()
                .block();
    }

    @Override
    public StarSystemWithDependenciesGetResponse getStarSystemWithDependencies(Integer systemId) {
        return webClientBuilder
                .baseUrl("http://galaxy-service/galaxy-app/").build()
                .get()
                .uri("system/" + systemId + "?withDependencies=true")
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, response -> {
                    throw new CourseNotFoundException(systemId);
                })
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToMono(StarSystemWithDependenciesGetResponse.class)
                .timeout(Duration.ofSeconds(5))
                .block();
    }
}
