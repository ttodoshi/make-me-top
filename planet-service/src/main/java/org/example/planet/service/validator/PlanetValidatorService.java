package org.example.planet.service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.planet.dto.planet.CreatePlanetDto;
import org.example.planet.dto.planet.UpdatePlanetDto;
import org.example.planet.exception.planet.PlanetAlreadyExistsException;
import org.example.planet.exception.planet.PlanetNotFoundException;
import org.example.planet.exception.system.SystemNotFoundException;
import org.example.planet.repository.PlanetRepository;
import org.example.planet.service.StarSystemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlanetValidatorService {
    private final PlanetRepository planetRepository;

    private final StarSystemService starSystemService;

    public void validateGetPlanetsRequest(String authorizationHeader, Long systemId) {
        checkSystemExists(authorizationHeader, systemId);
    }

    @Transactional(readOnly = true)
    public void validatePostRequest(String authorizationHeader, Long systemId, List<CreatePlanetDto> planets) {
        checkSystemExists(authorizationHeader, systemId);

        List<CreatePlanetDto> savingPlanetsList = new ArrayList<>();
        for (CreatePlanetDto planet : planets) {
            if (savingPlanetsList.contains(planet) || planetExists(systemId, planet.getPlanetName())) {
                log.warn("planet '{}' already exists in system {}", planet.getPlanetName(), systemId);
                throw new PlanetAlreadyExistsException(planet.getPlanetName());
            }
            savingPlanetsList.add(planet);
        }
    }

    private boolean planetExists(Long systemId, String planetName) {
        return planetRepository.findPlanetsBySystemIdOrderByPlanetNumber(systemId).stream().anyMatch(
                p -> p.getPlanetName().equals(planetName)
        );
    }

    @Transactional(readOnly = true)
    public void validatePutRequest(String authorizationHeader, Long planetId, UpdatePlanetDto planet) {
        checkSystemExists(authorizationHeader, planet.getSystemId());

        if (planetExists(planet.getSystemId(), planetId, planet.getPlanetName()))
            throw new PlanetAlreadyExistsException(planet.getPlanetName());
    }

    private void checkSystemExists(String authorizationHeader, Long systemId) {
        if (!starSystemService.existsById(authorizationHeader, systemId)) {
            log.warn("system by id {} not found", systemId);
            throw new SystemNotFoundException(systemId);
        }
    }

    private boolean planetExists(Long systemId, Long planetId, String planetName) {
        return planetRepository.findPlanetsBySystemIdOrderByPlanetNumber(systemId).stream().anyMatch(
                p -> p.getPlanetName().equals(planetName) && !p.getPlanetId().equals(planetId)
        );
    }

    @Transactional(readOnly = true)
    public void validateDeleteRequest(Long planetId) {
        if (!planetRepository.existsById(planetId)) {
            log.warn("planet by id {} not found", planetId);
            throw new PlanetNotFoundException(planetId);
        }
    }
}
