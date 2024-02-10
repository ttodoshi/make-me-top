package org.example.galaxy.service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.galaxy.dto.system.CreateStarSystemDto;
import org.example.galaxy.dto.system.UpdateStarSystemDto;
import org.example.galaxy.exception.galaxy.GalaxyNotFoundException;
import org.example.galaxy.exception.orbit.OrbitNotFoundException;
import org.example.galaxy.exception.system.SystemAlreadyExistsException;
import org.example.galaxy.exception.system.SystemNotFoundException;
import org.example.galaxy.model.Orbit;
import org.example.galaxy.repository.GalaxyRepository;
import org.example.galaxy.repository.OrbitRepository;
import org.example.galaxy.repository.StarSystemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StarSystemValidatorService {
    private final GalaxyRepository galaxyRepository;
    private final OrbitRepository orbitRepository;
    private final StarSystemRepository starSystemRepository;

    @Transactional(readOnly = true)
    public void validateGetSystemsByGalaxyId(Long galaxyId) {
        if (!galaxyRepository.existsById(galaxyId)) {
            log.warn("galaxy by id {} not found", galaxyId);
            throw new GalaxyNotFoundException(galaxyId);
        }
    }

    @Transactional(readOnly = true)
    public void validatePostRequest(Long orbitId, CreateStarSystemDto system) {
        Orbit orbit = orbitRepository.findById(orbitId)
                .orElseThrow(() -> {
                    log.warn("orbit by id {} not found", orbitId);
                    return new OrbitNotFoundException(orbitId);
                });
        if (starSystemRepository.existsStarSystemByOrbit_GalaxyIdAndSystemName(
                orbit.getGalaxyId(), system.getSystemName()
        )) {
            log.warn("system '{}' already exists", system.getSystemName());
            throw new SystemAlreadyExistsException(system.getSystemName());
        }
    }

    @Transactional(readOnly = true)
    public void validatePutRequest(Long systemId, UpdateStarSystemDto system) {
        Orbit orbit = orbitRepository.findById(system.getOrbitId())
                .orElseThrow(() -> {
                    log.warn("orbit by id {} not found", system.getOrbitId());
                    return new OrbitNotFoundException(system.getOrbitId());
                });
        if (starSystemRepository.existsStarSystemByOrbit_GalaxyIdAndSystemIdNotAndSystemName(
                orbit.getGalaxyId(), systemId, system.getSystemName()
        )) {
            log.warn("system '{}' already exists", system.getSystemName());
            throw new SystemAlreadyExistsException(system.getSystemName());
        }
    }

    @Transactional(readOnly = true)
    public void validateDeleteRequest(Long systemId) {
        if (!starSystemRepository.existsById(systemId)) {
            log.warn("system by id {} not found", systemId);
            throw new SystemNotFoundException(systemId);
        }
    }
}
