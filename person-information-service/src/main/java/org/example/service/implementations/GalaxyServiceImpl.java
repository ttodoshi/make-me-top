package org.example.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.dto.galaxy.GalaxyDto;
import org.example.dto.galaxy.GetGalaxyInformationDto;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.repository.custom.AuthorizationHeaderRepository;
import org.example.service.GalaxyService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GalaxyServiceImpl implements GalaxyService {
    private final AuthorizationHeaderRepository authorizationHeaderRepository;
    private final WebClient.Builder webClientBuilder;

    @Override
    public List<GetGalaxyInformationDto> getGalaxies() {
        return webClientBuilder
                .baseUrl("http://galaxy-service/galaxy-app/").build()
                .get()
                .uri("galaxy/")
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToFlux(GetGalaxyInformationDto.class)
                .timeout(Duration.ofSeconds(10))
                .collectList()
                .block();
    }

    @Override
    public GalaxyDto getGalaxyBySystemId(Integer systemId) {
        return webClientBuilder
                .baseUrl("http://galaxy-service/galaxy-app/").build()
                .get()
                .uri("system/" + systemId + "/galaxy/")
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToMono(GalaxyDto.class)
                .timeout(Duration.ofSeconds(5))
                .block();
    }
}
