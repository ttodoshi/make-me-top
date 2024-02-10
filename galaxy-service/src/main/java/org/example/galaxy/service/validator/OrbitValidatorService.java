package org.example.galaxy.service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.galaxy.dto.orbit.CreateOrbitWithStarSystemsDto;
import org.example.galaxy.dto.orbit.UpdateOrbitDto;
import org.example.galaxy.dto.system.CreateStarSystemDto;
import org.example.galaxy.exception.galaxy.GalaxyNotFoundException;
import org.example.galaxy.exception.orbit.OrbitCoordinatesException;
import org.example.galaxy.exception.orbit.OrbitNotFoundException;
import org.example.galaxy.exception.system.SystemAlreadyExistsException;
import org.example.galaxy.repository.GalaxyRepository;
import org.example.galaxy.repository.OrbitRepository;
import org.example.galaxy.repository.StarSystemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrbitValidatorService {
    private final GalaxyRepository galaxyRepository;
    private final OrbitRepository orbitRepository;
    private final StarSystemRepository starSystemRepository;

    @Transactional(readOnly = true)
    public void validatePostRequest(Long galaxyId, CreateOrbitWithStarSystemsDto orbit) {
        if (!galaxyRepository.existsById(galaxyId)) {
            log.warn("galaxy by id {} not found", galaxyId);
            throw new GalaxyNotFoundException(galaxyId);
        }
        if (orbitRepository.existsOrbitByGalaxyIdAndOrbitLevel(galaxyId, orbit.getOrbitLevel())) {
            log.warn("orbit with orbit level {} already exists in galaxy {}", orbit.getOrbitLevel(), galaxyId);
            throw new OrbitCoordinatesException();
        }
        Set<CreateStarSystemDto> savingSystemsList = new HashSet<>();
        for (CreateStarSystemDto system : orbit.getSystemList()) {
            if (savingSystemsList.contains(system) || systemExists(galaxyId, system.getSystemName())) {
                log.warn("system '{}' already exists", system.getSystemName());
                throw new SystemAlreadyExistsException(system.getSystemName());
            }
            savingSystemsList.add(system);
        }
    }

    private boolean systemExists(Long galaxyId, String systemName) {
        return starSystemRepository
                .existsStarSystemByOrbit_GalaxyIdAndSystemName(galaxyId, systemName);
    }

    @Transactional(readOnly = true)
    public void validatePutRequest(Long orbitId, UpdateOrbitDto orbit) {
        if (!galaxyRepository.existsById(orbit.getGalaxyId())) {
            log.warn("galaxy by id {} not found", orbit.getGalaxyId());
            throw new GalaxyNotFoundException(orbit.getGalaxyId());
        }
        if (orbitRepository.existsOrbitByGalaxyIdAndOrbitIdNotAndOrbitLevel(
                orbit.getGalaxyId(), orbitId, orbit.getOrbitLevel()
        )) {
            log.warn("orbit with orbit level {} already exists in galaxy {}", orbit.getOrbitLevel(), orbit.getGalaxyId());
            throw new OrbitCoordinatesException();
        }
    }

    @Transactional(readOnly = true)
    public void validateDeleteRequest(Long orbitId) {
        if (!orbitRepository.existsById(orbitId)) {
            log.warn("orbit by id {} not found", orbitId);
            throw new OrbitNotFoundException(orbitId);
        }
    }
}
