package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.dto.orbit.OrbitWithStarSystemsCreateRequest;
import org.example.dto.orbit.OrbitWithStarSystemsGetResponse;
import org.example.dto.orbit.OrbitDTO;
import org.example.dto.starsystem.StarSystemCreateRequest;
import org.example.dto.starsystem.StarSystemWithDependenciesGetResponse;
import org.example.exception.classes.galaxyEX.GalaxyNotFoundException;
import org.example.exception.classes.orbitEX.OrbitCoordinatesException;
import org.example.exception.classes.orbitEX.OrbitNotFoundException;
import org.example.exception.classes.systemEX.SystemAlreadyExistsException;
import org.example.model.Orbit;
import org.example.model.StarSystem;
import org.example.repository.GalaxyRepository;
import org.example.repository.OrbitRepository;
import org.example.repository.StarSystemRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class OrbitService {
    private final GalaxyRepository galaxyRepository;
    private final OrbitRepository orbitRepository;
    private final StarSystemRepository starSystemRepository;

    private final StarSystemService systemService;

    private final ModelMapper mapper;

    @Setter
    private String token;

    public OrbitWithStarSystemsGetResponse getOrbitWithSystemList(Integer orbitId) {
        if (!orbitRepository.existsById(orbitId))
            throw new OrbitNotFoundException(orbitId);
        OrbitWithStarSystemsGetResponse orbit = mapper.map(
                orbitRepository.getReferenceById(orbitId), OrbitWithStarSystemsGetResponse.class);
        List<StarSystemWithDependenciesGetResponse> systemWithDependenciesList = new LinkedList<>();
        starSystemRepository.findStarSystemsByOrbitId(orbitId).forEach(
                s -> systemWithDependenciesList.add(
                        systemService.getStarSystemByIdWithDependencies(s.getSystemId())
                )
        );
        orbit.setSystemWithDependenciesList(systemWithDependenciesList);
        return orbit;
    }

    public Orbit getOrbitById(Integer orbitId) {
        return orbitRepository.findById(orbitId)
                .orElseThrow(() -> new OrbitNotFoundException(orbitId));
    }

    @Transactional
    public OrbitWithStarSystemsGetResponse createOrbit(Integer galaxyId, OrbitWithStarSystemsCreateRequest orbitRequest) {
        if (!galaxyRepository.existsById(galaxyId))
            throw new GalaxyNotFoundException(galaxyId);
        if (orbitExists(galaxyId, orbitRequest.getOrbitLevel()))
            throw new OrbitCoordinatesException();
        Orbit orbit = mapper.map(orbitRequest, Orbit.class);
        orbit.setGalaxyId(galaxyId);
        Orbit savedOrbit = orbitRepository.save(orbit);
        List<StarSystemCreateRequest> savingSystemsList = new LinkedList<>();
        for (StarSystemCreateRequest system : orbitRequest.getSystemList()) {
            if (!orbitRepository.existsById(savedOrbit.getOrbitId()))
                throw new OrbitNotFoundException(savedOrbit.getOrbitId());
            if (savingSystemsList.contains(system) || systemExists(orbitRepository.getReferenceById(savedOrbit.getOrbitId()).getGalaxyId(), system.getSystemName()))
                throw new SystemAlreadyExistsException(system.getSystemName());
            savingSystemsList.add(system);
        }
        for (StarSystemCreateRequest currentSystem : orbitRequest.getSystemList()) {
            StarSystem system = mapper.map(currentSystem, StarSystem.class);
            system.setOrbitId(savedOrbit.getOrbitId());
            StarSystem savedSystem = starSystemRepository.save(system);
            systemService.setToken(token);
            systemService.createCourse(savedSystem.getSystemId(), currentSystem);
        }
        return getOrbitWithSystemList(savedOrbit.getOrbitId());
    }

    private boolean systemExists(Integer galaxyId, String systemName) {
        return starSystemRepository.findSystemsByGalaxyId(galaxyId)
                .stream().anyMatch(s -> Objects.equals(s.getSystemName(), systemName));
    }

    public Orbit updateOrbit(Integer orbitId, OrbitDTO orbit) {
        if (!galaxyRepository.existsById(orbit.getGalaxyId()))
            throw new GalaxyNotFoundException(orbit.getGalaxyId());
        if (orbitExists(orbit.getGalaxyId(), orbit.getOrbitLevel()))
            throw new OrbitCoordinatesException();
        Orbit updatedOrbit = orbitRepository.findById(orbitId).orElseThrow(() -> new OrbitNotFoundException(orbitId));
        updatedOrbit.setOrbitLevel(orbit.getOrbitLevel());
        updatedOrbit.setSystemCount(orbit.getSystemCount());
        updatedOrbit.setGalaxyId(orbit.getGalaxyId());
        return orbitRepository.save(updatedOrbit);
    }

    private boolean orbitExists(Integer galaxyId, Integer orbitLevel) {
        return orbitRepository.findOrbitsByGalaxyId(galaxyId)
                .stream().anyMatch(o -> o.getOrbitLevel().equals(orbitLevel));
    }

    public Map<String, String> deleteOrbit(Integer orbitId) {
        if (!orbitRepository.existsById(orbitId))
            throw new OrbitNotFoundException(orbitId);
        orbitRepository.deleteById(orbitId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Орбита " + orbitId + " была уничтожена неизвестным оружием инопланетной цивилизации");
        return response;
    }
}
