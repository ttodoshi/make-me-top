package org.example.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.dto.planet.PlanetCreateRequest;
import org.example.dto.planet.PlanetUpdateRequest;
import org.example.dto.starsystem.StarSystemDTO;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.planetEX.PlanetAlreadyExistsException;
import org.example.exception.classes.planetEX.PlanetNotFoundException;
import org.example.exception.classes.systemEX.SystemNotFoundException;
import org.example.repository.AuthorizationHeaderRepository;
import org.example.repository.PlanetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PlanetValidatorService {
    private final PlanetRepository planetRepository;
    private final AuthorizationHeaderRepository authorizationHeaderRepository;
    private final WebClient.Builder webClientBuilder;

    public void validateGetPlanetsRequest(Integer systemId) {
        checkSystemExists(systemId);
    }

    public void validatePostRequest(Integer systemId, List<PlanetCreateRequest> planets) {
        checkSystemExists(systemId);
        List<PlanetCreateRequest> savingPlanetsList = new LinkedList<>();
        for (PlanetCreateRequest planet : planets) {
            if (savingPlanetsList.contains(planet) || planetExists(systemId, planet.getPlanetName()))
                throw new PlanetAlreadyExistsException(planet.getPlanetName());
            savingPlanetsList.add(planet);
        }
    }

    private boolean planetExists(Integer systemId, String planetName) {
        return planetRepository.findPlanetsBySystemId(systemId).stream().anyMatch(
                p -> p.getPlanetName().equals(planetName)
        );
    }

    public void validatePutRequest(Integer planetId, PlanetUpdateRequest planet) {
        checkSystemExists(planet.getSystemId());
        if (planetExists(planet.getSystemId(), planetId, planet.getPlanetName()))
            throw new PlanetAlreadyExistsException(planet.getPlanetName());
    }

    private void checkSystemExists(Integer systemId) {
        webClientBuilder
                .baseUrl("http://galaxy-service/galaxy-app/").build()
                .get()
                .uri("system/" + systemId)
                .header("Authorization", authorizationHeaderRepository.getAuthorizationHeader())
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, response -> {
                    throw new SystemNotFoundException(systemId);
                })
                .onStatus(HttpStatus::isError, response -> {
                    throw new ConnectException();
                })
                .bodyToMono(StarSystemDTO.class)
                .timeout(Duration.ofSeconds(5))
                .block();
    }

    private boolean planetExists(Integer systemId, Integer planetId, String planetName) {
        return planetRepository.findPlanetsBySystemId(systemId).stream().anyMatch(
                p -> p.getPlanetName().equals(planetName) && !p.getPlanetId().equals(planetId)
        );
    }

    public void validateDeleteRequest(Integer planetId) {
        if (!planetRepository.existsById(planetId))
            throw new PlanetNotFoundException(planetId);
    }
}
