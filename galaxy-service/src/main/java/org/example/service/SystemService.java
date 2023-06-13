package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.config.mapper.DependencyMapper;
import org.example.config.mapper.SystemMapper;
import org.example.exception.galaxyEX.GalacxyNotFoundException;
import org.example.exception.orbitEX.OrbitNotFoundException;
import org.example.exception.systemEX.SystemAlreadyExistsException;
import org.example.exception.systemEX.SystemNotFoundException;
import org.example.model.dependencyModel.DependencyGetInfoModel;
import org.example.model.modelDAO.StarSystem;
import org.example.model.systemModel.SystemCreateModel;
import org.example.model.systemModel.SystemWithDependencyModel;
import org.example.repository.DependencyRepository;
import org.example.repository.GalaxyRepository;
import org.example.repository.OrbitRepository;
import org.example.repository.SystemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemService {
    private final SystemRepository systemRepository;
    private final DependencyRepository dependencyRepository;
    private final GalaxyRepository galaxyRepository;
    private final OrbitRepository orbitRepository;

    private final SystemMapper systemMapper;
    private final DependencyMapper dependencyMapper;
    final static Logger logger = LoggerFactory.getLogger(SystemService.class);

    public SystemWithDependencyModel getStarSystemById(Integer id) {
        try {
            SystemWithDependencyModel system = systemMapper.systemToSystemWithDependencyModel(systemRepository.getReferenceById(id));
            system.setDependencyList(dependencyRepository.getListSystemDependencyParent(system.getSystemId())
                    .stream().map(x -> dependencyMapper.DependencyModelToDependencyParentModel(x)).collect(Collectors.toList()));
            List<DependencyGetInfoModel> dependencies = dependencyRepository.getListSystemDependencyChild(system.getSystemId())
                    .stream().filter(x -> x.getParent() != null).map(x -> dependencyMapper.DependencyModelToDependencyChildModel(x)).collect(Collectors.toList());
            if (dependencies != null) {
                for (DependencyGetInfoModel model : dependencies) {
                    system.getDependencyList().add(model);
                }
            }
            return system;
        } catch (Exception e) {
            throw new SystemNotFoundException();
        }
    }

    public StarSystem getStarSystemByIdWithoutDependency(Integer systemId) {
        try {
            StarSystem starSystem = systemRepository.getReferenceById(systemId);
            logger.info(starSystem.getSystemName());
            return starSystem;
        } catch (Exception e) {
            throw new SystemNotFoundException();
        }
    }

    public void createSystem(SystemCreateModel model, Integer id) {

        try {
            logger.info(galaxyRepository.getReferenceById(id).getGalaxyName());
        } catch (Exception e) {
            throw new GalacxyNotFoundException();
        }
        try {
            logger.info(orbitRepository.getReferenceById(model.getOrbitId()).getOrbitId().toString());
        } catch (Exception e) {
            throw new OrbitNotFoundException();
        }

        try {
            if (systemRepository.getStarSystemByGalaxyId(id).stream().allMatch(x -> !Objects.equals(x.getSystemName(), model.getSystemName()))) {
                if (systemRepository.checkExistsSystem(model.getSystemId()) == null) {
                    systemRepository.save(systemMapper.systemCreateModelToStarSystem(model));
                } else {
                    throw new SystemAlreadyExistsException();
                }
            } else {
                throw new SystemAlreadyExistsException();
            }

        } catch (Exception e) {
            throw new SystemAlreadyExistsException();
        }
    }

    public void updateSystem(SystemCreateModel model, Integer id) {
        StarSystem starSystem;
        try {
            orbitRepository.getReferenceById(model.getOrbitId());
        } catch (Exception e) {
            throw new OrbitNotFoundException();
        }
        try {
            starSystem = systemRepository.getReferenceById(model.getOrbitId());
        } catch (Exception e) {
            throw new SystemNotFoundException();
        }
        try {
            if (systemRepository.getStarSystemByGalaxyId(id).stream().allMatch(x -> x.getSystemName() != model.getSystemName())) {
                starSystem.setSystemName(model.getSystemName());
                starSystem.setPositionSystem(model.getPositionSystem());
                starSystem.setOrbitId(model.getOrbitId());
                starSystem.setSystemLevel(model.getSystemLevel());
                starSystem.setSystemId(model.getSystemId());
                systemRepository.save(starSystem);
            } else {
                throw new SystemAlreadyExistsException();
            }

        } catch (Exception e) {
            throw new SystemAlreadyExistsException();
        }
    }

    public void deleteSystem(Integer id) {
        try {
            systemRepository.deleteById(id);
        } catch (Exception e) {
            throw new SystemNotFoundException();
        }
    }
}
