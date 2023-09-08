package org.example.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.dto.planet.CreatePlanetDto;
import org.example.dto.planet.UpdatePlanetDto;
import org.example.exception.classes.planetEX.PlanetAlreadyExistsException;
import org.example.exception.classes.planetEX.PlanetNotFoundException;
import org.example.repository.PlanetRepository;
import org.example.service.StarSystemService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PlanetValidatorService {
    private final PlanetRepository planetRepository;

    private final StarSystemService starSystemService;

    public void validateGetPlanetsRequest(Integer systemId) {
        starSystemService.checkSystemExists(systemId);
    }

    @Transactional(readOnly = true)
    public void validatePostRequest(Integer systemId, List<CreatePlanetDto> planets) {
        starSystemService.checkSystemExists(systemId);
        List<CreatePlanetDto> savingPlanetsList = new ArrayList<>();
        for (CreatePlanetDto planet : planets) {
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

    @Transactional(readOnly = true)
    public void validatePutRequest(Integer planetId, UpdatePlanetDto planet) {
        starSystemService.checkSystemExists(planet.getSystemId());
        if (planetExists(planet.getSystemId(), planetId, planet.getPlanetName()))
            throw new PlanetAlreadyExistsException(planet.getPlanetName());
    }

    private boolean planetExists(Integer systemId, Integer planetId, String planetName) {
        return planetRepository.findPlanetsBySystemId(systemId).stream().anyMatch(
                p -> p.getPlanetName().equals(planetName) && !p.getPlanetId().equals(planetId)
        );
    }

    @Transactional(readOnly = true)
    public void validateDeleteRequest(Integer planetId) {
        if (!planetRepository.existsById(planetId))
            throw new PlanetNotFoundException(planetId);
    }
}
