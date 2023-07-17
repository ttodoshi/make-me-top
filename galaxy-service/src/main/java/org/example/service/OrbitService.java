package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.dto.orbit.CreateOrbitWithStarSystems;
import org.example.dto.orbit.GetOrbitWithStarSystems;
import org.example.dto.orbit.OrbitDTO;
import org.example.dto.starsystem.CreateStarSystem;
import org.example.dto.starsystem.GetStarSystemWithDependencies;
import org.example.exception.classes.galaxyEX.GalaxyNotFoundException;
import org.example.exception.classes.orbitEX.OrbitCoordinatesException;
import org.example.exception.classes.orbitEX.OrbitNotFoundException;
import org.example.model.Orbit;
import org.example.repository.GalaxyRepository;
import org.example.repository.OrbitRepository;
import org.example.repository.StarSystemRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

    public GetOrbitWithStarSystems getOrbitWithSystemList(Integer orbitId) {
        if (!orbitRepository.existsById(orbitId))
            throw new OrbitNotFoundException();
        GetOrbitWithStarSystems orbit = mapper.map(
                orbitRepository.getReferenceById(orbitId), GetOrbitWithStarSystems.class);
        List<GetStarSystemWithDependencies> systemWithDependenciesList = new LinkedList<>();
        starSystemRepository.findStarSystemsByOrbitId(orbitId).forEach(
                s -> systemWithDependenciesList.add(
                        systemService.getStarSystemByIdWithDependencies(s.getSystemId())
                )
        );
        orbit.setSystemWithDependenciesList(systemWithDependenciesList);
        return orbit;
    }

    public Orbit getOrbitById(Integer orbitId) {
        return orbitRepository.findById(orbitId).orElseThrow(OrbitNotFoundException::new);
    }

    @Transactional
    public GetOrbitWithStarSystems createOrbit(Integer galaxyId, CreateOrbitWithStarSystems orbitRequest) {
        if (!galaxyRepository.existsById(galaxyId))
            throw new GalaxyNotFoundException();
        if (orbitExists(galaxyId, orbitRequest.getOrbitLevel()))
            throw new OrbitCoordinatesException();
        Orbit orbit = mapper.map(orbitRequest, Orbit.class);
        orbit.setGalaxyId(galaxyId);
        Orbit savedOrbit = orbitRepository.save(orbit);
        systemService.setToken(token);
        for (CreateStarSystem system : orbitRequest.getSystemsList()) {
            systemService.createSystem(savedOrbit.getOrbitId(), system);
        }
        return getOrbitWithSystemList(savedOrbit.getOrbitId());
    }

    public Orbit updateOrbit(Integer orbitId, OrbitDTO orbit) {
        if (!galaxyRepository.existsById(orbit.getGalaxyId()))
            throw new GalaxyNotFoundException();
        if (orbitExists(orbit.getGalaxyId(), orbit.getOrbitLevel()))
            throw new OrbitCoordinatesException();
        Orbit updatedOrbit = orbitRepository.findById(orbitId).orElseThrow(OrbitNotFoundException::new);
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
            throw new OrbitNotFoundException();
        orbitRepository.deleteById(orbitId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Орбита " + orbitId + " была уничтожена неизвестным оружием инопланетной цивилизации");
        return response;
    }
}
