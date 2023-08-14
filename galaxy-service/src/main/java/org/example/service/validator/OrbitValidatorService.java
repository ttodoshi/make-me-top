package org.example.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.dto.orbit.OrbitDTO;
import org.example.dto.orbit.OrbitWithStarSystemsCreateRequest;
import org.example.dto.starsystem.StarSystemCreateRequest;
import org.example.exception.classes.galaxyEX.GalaxyNotFoundException;
import org.example.exception.classes.orbitEX.OrbitCoordinatesException;
import org.example.exception.classes.orbitEX.OrbitNotFoundException;
import org.example.exception.classes.systemEX.SystemAlreadyExistsException;
import org.example.repository.GalaxyRepository;
import org.example.repository.OrbitRepository;
import org.example.repository.StarSystemRepository;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class OrbitValidatorService {
    private final GalaxyRepository galaxyRepository;
    private final OrbitRepository orbitRepository;
    private final StarSystemRepository starSystemRepository;

    public void validateGetWithSystemListRequest(Integer orbitId) {
        if (!orbitRepository.existsById(orbitId))
            throw new OrbitNotFoundException(orbitId);
    }

    public void validatePostRequest(Integer galaxyId, OrbitWithStarSystemsCreateRequest request) {
        if (!galaxyRepository.existsById(galaxyId))
            throw new GalaxyNotFoundException(galaxyId);
        if (orbitExists(galaxyId, request.getOrbitLevel()))
            throw new OrbitCoordinatesException();
        List<StarSystemCreateRequest> savingSystemsList = new LinkedList<>();
        for (StarSystemCreateRequest system : request.getSystemList()) {
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

    public void validatePutRequest(Integer orbitId, OrbitDTO orbit) {
        if (!galaxyRepository.existsById(orbit.getGalaxyId()))
            throw new GalaxyNotFoundException(orbit.getGalaxyId());
        if (orbitExists(orbit.getGalaxyId(), orbitId, orbit.getOrbitLevel()))
            throw new OrbitCoordinatesException();
    }

    private boolean orbitExists(Integer galaxyId, Integer orbitId, Integer orbitLevel) {
        return orbitRepository.findOrbitsByGalaxyId(galaxyId).stream()
                .anyMatch(o -> o.getOrbitLevel().equals(orbitLevel) && !o.getOrbitId().equals(orbitId));
    }

    public void validateDeleteRequest(Integer orbitId) {
        if (!orbitRepository.existsById(orbitId))
            throw new OrbitNotFoundException(orbitId);
    }
}
