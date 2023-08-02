package org.example.validator;

import lombok.RequiredArgsConstructor;
import org.example.dto.galaxy.GalaxyCreateRequest;
import org.example.dto.galaxy.GalaxyDTO;
import org.example.exception.classes.galaxyEX.GalaxyAlreadyExistsException;
import org.example.exception.classes.galaxyEX.GalaxyNotFoundException;
import org.example.repository.GalaxyRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GalaxyValidator {
    private final GalaxyRepository galaxyRepository;

    public void validateGetByIdRequest(Integer galaxyId) {
        if (!galaxyRepository.existsById(galaxyId))
            throw new GalaxyNotFoundException(galaxyId);
    }

    public void validatePostRequest(GalaxyCreateRequest request) {
        if (galaxyExists(request.getGalaxyName()))
            throw new GalaxyAlreadyExistsException(request.getGalaxyName());
    }

    public void validatePutRequest(Integer galaxyId, GalaxyDTO galaxy) {
        if (!galaxyRepository.existsById(galaxyId))
            throw new GalaxyNotFoundException(galaxyId);
        if (galaxyExists(galaxy.getGalaxyName()))
            throw new GalaxyAlreadyExistsException(galaxy.getGalaxyName());
    }

    private boolean galaxyExists(String galaxyName) {
        return galaxyRepository.findAll().stream()
                .anyMatch(g -> g.getGalaxyName().equals(galaxyName));
    }

    public void validateDeleteRequest(Integer galaxyId) {
        if (!galaxyRepository.existsById(galaxyId))
            throw new GalaxyNotFoundException(galaxyId);
    }
}
