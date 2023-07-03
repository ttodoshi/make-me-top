package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.config.mapper.DependencyMapper;
import org.example.dto.dependency.DependencyGetInfoModel;
import org.example.dto.starsystem.GetStarSystemWithDependencies;
import org.example.dto.starsystem.GetStarSystemWithoutDependency;
import org.example.dto.starsystem.StarSystemDTO;
import org.example.exception.classes.galaxyEX.GalaxyNotFoundException;
import org.example.exception.classes.orbitEX.OrbitNotFoundException;
import org.example.exception.classes.systemEX.SystemAlreadyExistsException;
import org.example.exception.classes.systemEX.SystemNotFoundException;
import org.example.model.StarSystem;
import org.example.repository.DependencyRepository;
import org.example.repository.GalaxyRepository;
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
public class StarSystemService {
    private final StarSystemRepository starSystemRepository;
    private final DependencyRepository dependencyRepository;
    private final GalaxyRepository galaxyRepository;
    private final OrbitRepository orbitRepository;

    private final ModelMapper mapper;
    private final DependencyMapper dependencyMapper;

    private final Logger logger = Logger.getLogger(GalaxyService.class.getName());

    public GetStarSystemWithDependencies getStarSystemByIdWithDependencies(Integer id) {
        try {
            GetStarSystemWithDependencies system = mapper.map(
                    starSystemRepository.getReferenceById(id), GetStarSystemWithDependencies.class);
            system.setDependencyList(dependencyRepository.getListSystemDependencyParent(id)
                    .stream()
                    .map(dependencyMapper::dependencyToDependencyParentModel)
                    .collect(Collectors.toList()));
            List<DependencyGetInfoModel> dependencies = dependencyRepository.getListSystemDependencyChild(id)
                    .stream()
                    .filter(x -> x.getParentId() != null)
                    .map(dependencyMapper::dependencyToDependencyChildModel)
                    .collect(Collectors.toList());
            for (DependencyGetInfoModel dependency : dependencies) {
                system.getDependencyList().add(dependency);
            }
            return system;
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new SystemNotFoundException();
        }
    }

    public GetStarSystemWithoutDependency getStarSystemById(Integer systemId) {
        StarSystem system = starSystemRepository
                .findById(systemId).orElseThrow(SystemNotFoundException::new);
        return mapper.map(system, GetStarSystemWithoutDependency.class);
    }

    public List<StarSystem> getStarSystemsByGalaxyId(Integer galaxyId) {
        List<StarSystem> systems = starSystemRepository.getStarSystemsByGalaxyId(galaxyId);
        if (systems == null)
            throw new GalaxyNotFoundException();
        return systems;
    }

    public StarSystem createSystem(StarSystemDTO starSystem, Integer galaxyId) {
        if (!galaxyRepository.existsById(galaxyId))
            throw new GalaxyNotFoundException();
        if (!orbitRepository.existsById(starSystem.getOrbitId()))
            throw new OrbitNotFoundException();

        if (starSystemRepository.getStarSystemsByGalaxyId(galaxyId).stream()
                .noneMatch(
                        x -> Objects.equals(x.getSystemName(), starSystem.getSystemName())))
            return starSystemRepository.save(mapper.map(starSystem, StarSystem.class));
        else
            throw new SystemAlreadyExistsException();
    }

    public StarSystem updateSystem(StarSystemDTO starSystem, Integer galaxyId, Integer systemId) {
        if (!starSystemRepository.existsById(systemId))
            throw new SystemNotFoundException();
        StarSystem updatedStarSystem = starSystemRepository.getReferenceById(systemId);
        if (!galaxyRepository.existsById(galaxyId))
            throw new GalaxyNotFoundException();
        if (!orbitRepository.existsById(starSystem.getOrbitId()))
            throw new OrbitNotFoundException();
        if (starSystemRepository.getStarSystemsByGalaxyId(galaxyId).stream()
                .noneMatch(
                        x -> Objects.equals(x.getSystemName(), starSystem.getSystemName()))) {
            updatedStarSystem.setSystemName(starSystem.getSystemName());
            updatedStarSystem.setSystemPosition(starSystem.getSystemPosition());
            updatedStarSystem.setOrbitId(starSystem.getOrbitId());
            updatedStarSystem.setSystemLevel(starSystem.getSystemLevel());
            return starSystemRepository.save(updatedStarSystem);
        } else {
            throw new SystemAlreadyExistsException();
        }
    }

    public Map<String, String> deleteSystem(Integer id) {
        try {
            starSystemRepository.deleteById(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Система была уничжтожена чёрной дырой");
            return response;
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new SystemNotFoundException();
        }
    }
}
