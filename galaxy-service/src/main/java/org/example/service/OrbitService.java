package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.orbit.OrbitDTO;
import org.example.dto.orbit.OrbitWithStarSystems;
import org.example.dto.starsystem.GetStarSystemWithDependencies;
import org.example.exception.classes.galaxyEX.GalaxyNotFoundException;
import org.example.exception.classes.orbitEX.OrbitCoordinatesException;
import org.example.exception.classes.orbitEX.OrbitDeleteException;
import org.example.exception.classes.orbitEX.OrbitNotFoundException;
import org.example.model.Orbit;
import org.example.repository.GalaxyRepository;
import org.example.repository.OrbitRepository;
import org.example.repository.StarSystemRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrbitService {
    private final GalaxyRepository galaxyRepository;
    private final OrbitRepository orbitRepository;
    private final StarSystemRepository starSystemRepository;

    private final StarSystemService systemService;

    private final ModelMapper mapper;

    private final Logger logger = Logger.getLogger(GalaxyService.class.getName());

    public OrbitWithStarSystems getOrbitWithSystemList(Integer orbitId) {
        try {
            OrbitWithStarSystems orbit = mapper.map(
                    orbitRepository.getReferenceById(orbitId), OrbitWithStarSystems.class);
            orbit.setSystemWithDependenciesList(
                    starSystemRepository.getStarSystemsByOrbitId(orbitId)
                            .stream()
                            .map(system -> mapper.map(system, GetStarSystemWithDependencies.class))
                            .collect(Collectors.toList()));
            List<GetStarSystemWithDependencies> systemWithDependenciesList = new LinkedList<>();
            for (GetStarSystemWithDependencies system : orbit.getSystemWithDependenciesList())
                systemWithDependenciesList.add(systemService.getStarSystemByIdWithDependencies(system.getSystemId()));
            orbit.setSystemWithDependenciesList(systemWithDependenciesList);
            return orbit;
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new OrbitNotFoundException();
        }
    }

    public Orbit getOrbitById(Integer orbitId) {
        return orbitRepository.findById(orbitId).orElseThrow(OrbitNotFoundException::new);
    }

    public Orbit createOrbit(OrbitDTO orbitRequest) {
        if (!galaxyRepository.existsById(orbitRequest.getGalaxyId()))
            throw new GalaxyNotFoundException();
        List<Orbit> orbitList = orbitRepository.findOrbitsByGalaxyId(orbitRequest.getGalaxyId());
        for (Orbit orbit : orbitList) {
            if (Objects.equals(orbit.getOrbitLevel(), orbitRequest.getOrbitLevel())) {
                throw new OrbitCoordinatesException();
            }
        }
        return orbitRepository.save(mapper.map(orbitRequest, Orbit.class));
    }

    public Orbit updateOrbit(Integer orbitId, OrbitDTO orbit) {
        Orbit updatedOrbit = orbitRepository.findById(orbitId).orElseThrow(OrbitNotFoundException::new);
        updatedOrbit.setOrbitLevel(orbit.getOrbitLevel());
        updatedOrbit.setSystemCount(orbit.getSystemCount());
        updatedOrbit.setGalaxyId(orbit.getGalaxyId());
        return orbitRepository.save(updatedOrbit);
    }

    public Map<String, String> deleteOrbit(Integer orbitId) {
        try {
            orbitRepository.deleteById(orbitId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Орбита " + orbitId + " была уничтожена неизвестным оружием инопланетной цивилизации");
            return response;
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new OrbitDeleteException();
        }
    }
}
