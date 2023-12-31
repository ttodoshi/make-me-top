package org.example.galaxy.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.galaxy.dto.starsystem.CreateStarSystemDto;
import org.example.galaxy.dto.starsystem.UpdateStarSystemDto;
import org.example.galaxy.exception.classes.galaxy.GalaxyNotFoundException;
import org.example.galaxy.exception.classes.orbit.OrbitNotFoundException;
import org.example.galaxy.exception.classes.system.SystemAlreadyExistsException;
import org.example.galaxy.exception.classes.system.SystemNotFoundException;
import org.example.galaxy.repository.GalaxyRepository;
import org.example.galaxy.repository.OrbitRepository;
import org.example.galaxy.repository.StarSystemRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class StarSystemValidatorService {
    private final GalaxyRepository galaxyRepository;
    private final OrbitRepository orbitRepository;
    private final StarSystemRepository starSystemRepository;

    @Transactional(readOnly = true)
    public void validateGetSystemsByGalaxyId(Long galaxyId) {
        if (!galaxyRepository.existsById(galaxyId))
            throw new GalaxyNotFoundException(galaxyId);
    }

    @Transactional(readOnly = true)
    public void validatePostRequest(Long orbitId, CreateStarSystemDto request) {
        if (!orbitRepository.existsById(orbitId))
            throw new OrbitNotFoundException(orbitId);
        if (systemExists(orbitRepository.getReferenceById(orbitId).getGalaxyId(), request.getSystemName()))
            throw new SystemAlreadyExistsException(request.getSystemName());
    }

    private boolean systemExists(Long galaxyId, String systemName) {
        return starSystemRepository.findStarSystemsByOrbit_GalaxyId(galaxyId)
                .stream().anyMatch(s -> s.getSystemName().equals(systemName));
    }

    @Transactional(readOnly = true)
    public void validatePutRequest(Long systemId, UpdateStarSystemDto system) {
        if (!orbitRepository.existsById(system.getOrbitId()))
            throw new OrbitNotFoundException(system.getOrbitId());
        if (systemExists(orbitRepository.getReferenceById(system.getOrbitId()).getGalaxyId(), systemId, system.getSystemName()))
            throw new SystemAlreadyExistsException(system.getSystemName());
    }

    private boolean systemExists(Long galaxyId, Long systemId, String systemName) {
        return starSystemRepository.findStarSystemsByOrbit_GalaxyId(galaxyId)
                .stream().anyMatch(s -> Objects.equals(s.getSystemName(), systemName) && !s.getSystemId().equals(systemId));
    }

    @Transactional(readOnly = true)
    public void validateDeleteRequest(Long systemId) {
        if (!starSystemRepository.existsById(systemId))
            throw new SystemNotFoundException(systemId);
    }
}
