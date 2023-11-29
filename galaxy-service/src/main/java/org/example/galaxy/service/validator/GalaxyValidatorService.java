package org.example.galaxy.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.galaxy.dto.galaxy.CreateGalaxyDto;
import org.example.galaxy.dto.galaxy.GalaxyDto;
import org.example.galaxy.exception.classes.galaxy.GalaxyAlreadyExistsException;
import org.example.galaxy.exception.classes.galaxy.GalaxyNotFoundException;
import org.example.galaxy.repository.GalaxyRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GalaxyValidatorService {
    private final GalaxyRepository galaxyRepository;

    @Transactional(readOnly = true)
    public void validateGetByIdRequest(Long galaxyId) {
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
    public void validatePutRequest(Long galaxyId, GalaxyDto galaxy) {
        if (!galaxyRepository.existsById(galaxyId))
            throw new GalaxyNotFoundException(galaxyId);
        if (galaxyExists(galaxyId, galaxy.getGalaxyName()))
            throw new GalaxyAlreadyExistsException(galaxy.getGalaxyName());
    }

    private boolean galaxyExists(Long galaxyId, String galaxyName) {
        return galaxyRepository.findAll().stream()
                .anyMatch(g -> g.getGalaxyName().equals(galaxyName) && !g.getGalaxyId().equals(galaxyId));
    }
}
