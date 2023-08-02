package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.orbit.OrbitDTO;
import org.example.dto.orbit.OrbitWithStarSystemsCreateRequest;
import org.example.dto.orbit.OrbitWithStarSystemsGetResponse;
import org.example.dto.starsystem.StarSystemCreateRequest;
import org.example.dto.starsystem.StarSystemWithDependenciesGetResponse;
import org.example.exception.classes.orbitEX.OrbitNotFoundException;
import org.example.exception.classes.systemEX.SystemAlreadyExistsException;
import org.example.model.Orbit;
import org.example.model.StarSystem;
import org.example.repository.GalaxyRepository;
import org.example.repository.OrbitRepository;
import org.example.repository.StarSystemRepository;
import org.example.validator.OrbitValidator;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class OrbitService {
    private final OrbitRepository orbitRepository;
    private final StarSystemRepository starSystemRepository;

    private final OrbitValidator orbitValidator;
    private final StarSystemService systemService;

    private final ModelMapper mapper;

    public OrbitWithStarSystemsGetResponse getOrbitWithSystemList(Integer orbitId) {
        orbitValidator.validateGetWithSystemListRequest(orbitId);
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
        orbitValidator.validatePostRequest(galaxyId, orbitRequest);
        Orbit orbit = mapper.map(orbitRequest, Orbit.class);
        orbit.setGalaxyId(galaxyId);
        Orbit savedOrbit = orbitRepository.save(orbit);
        for (StarSystemCreateRequest currentSystem : orbitRequest.getSystemList()) {
            StarSystem system = mapper.map(currentSystem, StarSystem.class);
            system.setOrbitId(savedOrbit.getOrbitId());
            StarSystem savedSystem = starSystemRepository.save(system);
            systemService.createCourse(savedSystem.getSystemId(), currentSystem);
        }
        return getOrbitWithSystemList(savedOrbit.getOrbitId());
    }

    public Orbit updateOrbit(Integer orbitId, OrbitDTO orbit) {
        orbitValidator.validatePutRequest(orbitId, orbit);
        Orbit updatedOrbit = orbitRepository.findById(orbitId).orElseThrow(() -> new OrbitNotFoundException(orbitId));
        updatedOrbit.setOrbitLevel(orbit.getOrbitLevel());
        updatedOrbit.setSystemCount(orbit.getSystemCount());
        updatedOrbit.setGalaxyId(orbit.getGalaxyId());
        return orbitRepository.save(updatedOrbit);
    }

    public Map<String, String> deleteOrbit(Integer orbitId) {
        orbitValidator.validateDeleteRequest(orbitId);
        orbitRepository.deleteById(orbitId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Орбита " + orbitId + " была уничтожена неизвестным оружием инопланетной цивилизации");
        return response;
    }
}
