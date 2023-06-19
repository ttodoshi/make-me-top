package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.config.mapper.DependencyMapper;
import org.example.dto.dependency.DependencyGetInfoModel;
import org.example.dto.starsystem.StarSystemDTO;
import org.example.dto.starsystem.StarSystemWithDependencies;
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

    public StarSystemWithDependencies getStarSystemById(Integer id) {
        try {
            StarSystemWithDependencies system = mapper.map(
                    starSystemRepository.getReferenceById(id), StarSystemWithDependencies.class);
            system.setDependencyList(dependencyRepository.getListSystemDependencyParent(id)
                    .stream()
                    .map(dependencyMapper::dependencyToDependencyParentModel)
                    .collect(Collectors.toList()));
            List<DependencyGetInfoModel> dependencies = dependencyRepository.getListSystemDependencyChild(id)
                    .stream()
                    .filter(x -> x.getParent() != null)
                    .map(dependencyMapper::dependencyToDependencyChildModel)
                    .collect(Collectors.toList());
            if (dependencies != null) {
                for (DependencyGetInfoModel dependency : dependencies) {
                    system.getDependencyList().add(dependency);
                }
            }
            return system;
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new SystemNotFoundException();
        }
    }

    public StarSystemDTO getStarSystemByIdWithoutDependency(Integer systemId) {
        try {
            return mapper.map(starSystemRepository.getReferenceById(systemId), StarSystemDTO.class);
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new SystemNotFoundException();
        }
    }

    public StarSystem createSystem(StarSystemDTO starSystem, Integer id) {
        try {
            logger.info(galaxyRepository.getReferenceById(id).getGalaxyName());
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new GalaxyNotFoundException();
        }

        try {
            logger.info(orbitRepository.getReferenceById(starSystem.getOrbitId()).getOrbitId().toString());
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new OrbitNotFoundException();
        }

        try {
            if (starSystemRepository.getStarSystemByGalaxyId(id).stream().noneMatch(
                    x -> Objects.equals(x.getSystemName(), starSystem.getSystemName()))) {
                return starSystemRepository.save(
                        mapper.map(starSystem, StarSystem.class));
            } else {
                throw new SystemAlreadyExistsException();
            }
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new SystemAlreadyExistsException();
        }
    }

    public StarSystemDTO updateSystem(StarSystemDTO starSystem, Integer galaxyId, Integer systemId) {
        StarSystem updatedStarSystem;
        try {
            updatedStarSystem = starSystemRepository.getReferenceById(systemId);
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new SystemNotFoundException();
        }
        try {
            orbitRepository.getReferenceById(starSystem.getOrbitId());
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new OrbitNotFoundException();
        }
        try {
            if (starSystemRepository.getStarSystemByGalaxyId(galaxyId).stream()
                    .noneMatch(
                            x -> Objects.equals(x.getSystemName(), starSystem.getSystemName()))) {
                updatedStarSystem.setSystemName(starSystem.getSystemName());
                updatedStarSystem.setSystemPosition(starSystem.getSystemPosition());
                updatedStarSystem.setOrbitId(starSystem.getOrbitId());
                updatedStarSystem.setSystemLevel(starSystem.getSystemLevel());
                return mapper.map(starSystemRepository.save(updatedStarSystem), StarSystemDTO.class);
            } else {
                throw new SystemAlreadyExistsException();
            }
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new SystemAlreadyExistsException();
        }
    }

    public Map<String, String> deleteSystem(Integer id) {
        try {
            starSystemRepository.deleteById(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Система успешно удалена");
            return response;
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new SystemNotFoundException();
        }
    }
}
