package org.example.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.dto.orbit.CreateOrbitWithStarSystemsDto;
import org.example.dto.orbit.OrbitDto;
import org.example.dto.starsystem.CreateStarSystemDto;
import org.example.exception.classes.galaxyEX.GalaxyNotFoundException;
import org.example.exception.classes.orbitEX.OrbitCoordinatesException;
import org.example.exception.classes.orbitEX.OrbitNotFoundException;
import org.example.exception.classes.systemEX.SystemAlreadyExistsException;
import org.example.repository.GalaxyRepository;
import org.example.repository.OrbitRepository;
import org.example.repository.StarSystemRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class OrbitValidatorService {
    private final GalaxyRepository galaxyRepository;
    private final OrbitRepository orbitRepository;
    private final StarSystemRepository starSystemRepository;

    @Transactional(readOnly = true)
    public void validateGetWithSystemListRequest(Integer orbitId) {
        if (!orbitRepository.existsById(orbitId))
            throw new OrbitNotFoundException(orbitId);
    }

    @Transactional(readOnly = true)
    public void validatePostRequest(Integer galaxyId, CreateOrbitWithStarSystemsDto request) {
        if (!galaxyRepository.existsById(galaxyId))
            throw new GalaxyNotFoundException(galaxyId);
        if (orbitExists(galaxyId, request.getOrbitLevel()))
            throw new OrbitCoordinatesException();
        List<CreateStarSystemDto> savingSystemsList = new ArrayList<>();
        for (CreateStarSystemDto system : request.getSystemList()) {
            if (savingSystemsList.contains(system) || systemExists(galaxyId, system.getSystemName()))
                throw new SystemAlreadyExistsException(system.getSystemName());
            savingSystemsList.add(system);
        }
    }

    private boolean systemExists(Integer galaxyId, String systemName) {
        return starSystemRepository.findSystemsByGalaxyId(galaxyId)
                .stream().anyMatch(s -> Objects.equals(s.getSystemName(), systemName));
    }

    private boolean orbitExists(Integer galaxyId, Integer orbitLevel) {
        return orbitRepository.findOrbitsByGalaxyId(galaxyId).stream()
                .anyMatch(o -> o.getOrbitLevel().equals(orbitLevel));
    }

    @Transactional(readOnly = true)
    public void validatePutRequest(Integer orbitId, OrbitDto orbit) {
        if (!galaxyRepository.existsById(orbit.getGalaxyId()))
            throw new GalaxyNotFoundException(orbit.getGalaxyId());
        if (orbitExists(orbit.getGalaxyId(), orbitId, orbit.getOrbitLevel()))
            throw new OrbitCoordinatesException();
    }

    private boolean orbitExists(Integer galaxyId, Integer orbitId, Integer orbitLevel) {
        return orbitRepository.findOrbitsByGalaxyId(galaxyId).stream()
                .anyMatch(o -> o.getOrbitLevel().equals(orbitLevel) && !o.getOrbitId().equals(orbitId));
    }
}
