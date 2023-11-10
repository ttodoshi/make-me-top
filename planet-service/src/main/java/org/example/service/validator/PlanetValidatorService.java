package org.example.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.dto.planet.CreatePlanetDto;
import org.example.dto.planet.UpdatePlanetDto;
import org.example.exception.classes.planetEX.PlanetAlreadyExistsException;
import org.example.exception.classes.planetEX.PlanetNotFoundException;
import org.example.exception.classes.systemEX.SystemNotFoundException;
import org.example.repository.PlanetRepository;
import org.example.repository.StarSystemRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PlanetValidatorService {
    private final PlanetRepository planetRepository;

    private final StarSystemRepository starSystemRepository;

    public void validateGetPlanetsRequest(Integer systemId) {
        checkSystemExists(systemId);
    }

    @Transactional(readOnly = true)
    public void validatePostRequest(Integer systemId, List<CreatePlanetDto> planets) {
        checkSystemExists(systemId);
        List<CreatePlanetDto> savingPlanetsList = new ArrayList<>();
        for (CreatePlanetDto planet : planets) {
            if (savingPlanetsList.contains(planet) || planetExists(systemId, planet.getPlanetName()))
                throw new PlanetAlreadyExistsException(planet.getPlanetName());
            savingPlanetsList.add(planet);
        }
    }

    private boolean planetExists(Integer systemId, String planetName) {
        return planetRepository.findPlanetsBySystemIdOrderByPlanetNumber(systemId).stream().anyMatch(
                p -> p.getPlanetName().equals(planetName)
        );
    }

    @Transactional(readOnly = true)
    public void validatePutRequest(Integer planetId, UpdatePlanetDto planet) {
        checkSystemExists(planet.getSystemId());
        if (planetExists(planet.getSystemId(), planetId, planet.getPlanetName()))
            throw new PlanetAlreadyExistsException(planet.getPlanetName());
    }

    private void checkSystemExists(Integer systemId) {
        if (!starSystemRepository.existsById(systemId))
            throw new SystemNotFoundException(systemId);
    }

    private boolean planetExists(Integer systemId, Integer planetId, String planetName) {
        return planetRepository.findPlanetsBySystemIdOrderByPlanetNumber(systemId).stream().anyMatch(
                p -> p.getPlanetName().equals(planetName) && !p.getPlanetId().equals(planetId)
        );
    }

    @Transactional(readOnly = true)
    public void validateDeleteRequest(Integer planetId) {
        if (!planetRepository.existsById(planetId))
            throw new PlanetNotFoundException(planetId);
    }
}
