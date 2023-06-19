package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.config.mapper.DependencyMapper;
import org.example.dto.orbit.OrbitDTO;
import org.example.dto.orbit.OrbitWithStarSystems;
import org.example.dto.starsystem.StarSystemWithDependencies;
import org.example.exception.classes.galaxyEX.GalaxyNotFoundException;
import org.example.exception.classes.orbitEX.OrbitAlreadyExistsException;
import org.example.exception.classes.orbitEX.OrbitCoordinatesException;
import org.example.exception.classes.orbitEX.OrbitDeleteException;
import org.example.exception.classes.orbitEX.OrbitNotFoundException;
import org.example.model.Orbit;
import org.example.repository.DependencyRepository;
import org.example.repository.OrbitRepository;
import org.example.repository.StarSystemRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrbitService {
    private final OrbitRepository orbitRepository;
    private final StarSystemRepository starSystemRepository;
    private final DependencyRepository dependencyRepository;

    private final ModelMapper mapper;
    private final DependencyMapper dependencyMapper;

    private final Logger logger = Logger.getLogger(GalaxyService.class.getName());

    public OrbitWithStarSystems getOrbitWithSystemList(Integer id) {
        try {
            OrbitWithStarSystems orbit = mapper.map(
                    orbitRepository.getReferenceById(id), OrbitWithStarSystems.class);
            orbit.setSystemWithDependenciesList(
                    starSystemRepository.getStarSystemByOrbitId(id)
                            .stream()
                            .map(system -> mapper.map(system, StarSystemWithDependencies.class))
                            .collect(Collectors.toList()));
            for (StarSystemWithDependencies system : orbit.getSystemWithDependenciesList()) {
                system.setDependencyList(
                        dependencyRepository.getListSystemDependencyParent(system.getSystemId())
                        .stream()
                        .map(dependencyMapper::dependencyToDependencyParentModel)
                        .collect(Collectors.toList()));
                dependencyRepository.getListSystemDependencyChild(system.getSystemId())
                        .stream()
                        .filter(x -> x.getParentId() != null)
                        .map(dependencyMapper::dependencyToDependencyChildModel)
                        .forEach(x -> system.getDependencyList().add(x));
            }
            return orbit;
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new OrbitNotFoundException();
        }
    }

    public OrbitDTO getOrbitById(Integer id) {
        try {
            return mapper.map(orbitRepository.getReferenceById(id), OrbitDTO.class);
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new OrbitNotFoundException();
        }
    }

    public Orbit createOrbit(OrbitDTO orbitRequest) {
        List<Orbit> orbitList;
        try {
            orbitList = orbitRepository.getOrbitsByGalaxyId(orbitRequest.getGalaxyId());
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new GalaxyNotFoundException();
        }
        try {
            for (Orbit orbit : orbitList) {
                if (Objects.equals(orbit.getOrbitLevel(), orbitRequest.getOrbitLevel())) {
                    throw new OrbitCoordinatesException();
                }
            }
            return orbitRepository.save(mapper.map(orbitRequest, Orbit.class));
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new OrbitCoordinatesException();
        }
    }

    public OrbitDTO updateOrbit(Integer id, OrbitDTO orbit) {
        try {
            Orbit updatedOrbit = orbitRepository.getReferenceById(id);
            updatedOrbit.setOrbitLevel(orbit.getOrbitLevel());
            updatedOrbit.setSystemCount(orbit.getSystemCount());
            return mapper.map(orbitRepository.save(updatedOrbit), OrbitDTO.class);
        } catch (RuntimeException e) {
            logger.severe(e.getMessage());
            throw new OrbitAlreadyExistsException();
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new OrbitNotFoundException();
        }
    }

    public Map<String, String> deleteOrbit(Integer id) {
        getOrbitById(id);
        try {
            orbitRepository.deleteById(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Орбита успешно удалена");
            return response;
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new OrbitDeleteException();
        }
    }
}
