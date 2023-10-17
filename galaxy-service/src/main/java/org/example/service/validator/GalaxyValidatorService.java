package org.example.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.dto.galaxy.CreateGalaxyDto;
import org.example.dto.galaxy.GalaxyDto;
import org.example.exception.classes.galaxyEX.GalaxyAlreadyExistsException;
import org.example.exception.classes.galaxyEX.GalaxyNotFoundException;
import org.example.repository.GalaxyRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GalaxyValidatorService {
    private final GalaxyRepository galaxyRepository;

    @Transactional(readOnly = true)
    public void validateGetByIdRequest(Integer galaxyId) {
        if (!galaxyRepository.existsById(galaxyId))
            throw new GalaxyNotFoundException(galaxyId);
    }

    @Transactional(readOnly = true)
    public void validatePostRequest(CreateGalaxyDto request) {
        if (galaxyExists(request.getGalaxyName()))
            throw new GalaxyAlreadyExistsException(request.getGalaxyName());
    }

    private boolean galaxyExists(String galaxyName) {
        return galaxyRepository.findAll().stream()
                .anyMatch(g -> g.getGalaxyName().equals(galaxyName));
    }

    @Transactional(readOnly = true)
    public void validatePutRequest(Integer galaxyId, GalaxyDto galaxy) {
        if (!galaxyRepository.existsById(galaxyId))
            throw new GalaxyNotFoundException(galaxyId);
        if (galaxyExists(galaxyId, galaxy.getGalaxyName()))
            throw new GalaxyAlreadyExistsException(galaxy.getGalaxyName());
    }

    private boolean galaxyExists(Integer galaxyId, String galaxyName) {
        return galaxyRepository.findAll().stream()
                .anyMatch(g -> g.getGalaxyName().equals(galaxyName) && !g.getGalaxyId().equals(galaxyId));
    }

    @Transactional(readOnly = true)
    public void validateDeleteRequest(Integer galaxyId) {
        if (!galaxyRepository.existsById(galaxyId))
            throw new GalaxyNotFoundException(galaxyId);
    }
}
