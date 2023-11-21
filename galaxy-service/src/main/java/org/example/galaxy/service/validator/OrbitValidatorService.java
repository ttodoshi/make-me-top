package org.example.galaxy.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.galaxy.dto.orbit.CreateOrbitWithStarSystemsDto;
import org.example.galaxy.dto.orbit.OrbitDto;
import org.example.galaxy.dto.starsystem.CreateStarSystemDto;
import org.example.galaxy.exception.classes.galaxy.GalaxyNotFoundException;
import org.example.galaxy.exception.classes.orbit.OrbitCoordinatesException;
import org.example.galaxy.exception.classes.system.SystemAlreadyExistsException;
import org.example.galaxy.repository.GalaxyRepository;
import org.example.galaxy.repository.OrbitRepository;
import org.example.galaxy.repository.StarSystemRepository;
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
        return starSystemRepository.findStarSystemsByOrbit_GalaxyId(galaxyId)
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
