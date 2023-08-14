package org.example.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.dto.starsystem.StarSystemCreateRequest;
import org.example.dto.starsystem.StarSystemDTO;
import org.example.exception.classes.galaxyEX.GalaxyNotFoundException;
import org.example.exception.classes.orbitEX.OrbitNotFoundException;
import org.example.exception.classes.systemEX.SystemAlreadyExistsException;
import org.example.exception.classes.systemEX.SystemNotFoundException;
import org.example.repository.GalaxyRepository;
import org.example.repository.OrbitRepository;
import org.example.repository.StarSystemRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class StarSystemValidatorService {
    private final GalaxyRepository galaxyRepository;
    private final OrbitRepository orbitRepository;
    private final StarSystemRepository starSystemRepository;

    public void validateGetSystemWithDependencies(Integer systemId) {
        if (!starSystemRepository.existsById(systemId))
            throw new SystemNotFoundException(systemId);
    }

    public void validateGetSystemsByGalaxyId(Integer galaxyId) {
        if (!galaxyRepository.existsById(galaxyId))
            throw new GalaxyNotFoundException(galaxyId);
    }

    public void validatePostRequest(Integer orbitId, StarSystemCreateRequest request) {
        if (!orbitRepository.existsById(orbitId))
            throw new OrbitNotFoundException(orbitId);
        if (systemExists(orbitRepository.getReferenceById(orbitId).getGalaxyId(), request.getSystemName()))
            throw new SystemAlreadyExistsException(request.getSystemName());
    }

    private boolean systemExists(Integer galaxyId, String systemName) {
        return starSystemRepository.findSystemsByGalaxyId(galaxyId)
                .stream().anyMatch(s -> s.getSystemName().equals(systemName));
    }

    public void validatePutRequest(Integer systemId, StarSystemDTO system) {
        if (!starSystemRepository.existsById(systemId))
            throw new SystemNotFoundException(systemId);
        if (!orbitRepository.existsById(system.getOrbitId()))
            throw new OrbitNotFoundException(system.getOrbitId());
        if (systemExists(orbitRepository.getReferenceById(system.getOrbitId()).getGalaxyId(), systemId, system.getSystemName()))
            throw new SystemAlreadyExistsException(system.getSystemName());
    }

    private boolean systemExists(Integer galaxyId, Integer systemId, String systemName) {
        return starSystemRepository.findSystemsByGalaxyId(galaxyId)
                .stream().anyMatch(s -> Objects.equals(s.getSystemName(), systemName) && !s.getSystemId().equals(systemId));
    }

    public void validateDeleteRequest(Integer systemId) {
        if (!starSystemRepository.existsById(systemId))
            throw new SystemNotFoundException(systemId);
    }
}
