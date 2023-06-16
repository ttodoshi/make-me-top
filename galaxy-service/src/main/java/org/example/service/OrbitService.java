package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.config.mapper.DependencyMapper;
import org.example.config.mapper.OrbitMapper;
import org.example.config.mapper.SystemMapper;
import org.example.exception.galaxyEX.GalaxyNotFoundException;
import org.example.exception.orbitEX.OrbitAlreadyExistsException;
import org.example.exception.orbitEX.OrbitCoordinatesException;
import org.example.exception.orbitEX.OrbitDeleteException;
import org.example.exception.orbitEX.OrbitNotFoundException;
import org.example.model.modelDAO.Orbit;
import org.example.model.orbitModel.OrbitCreateModel;
import org.example.model.orbitModel.OrbitModel;
import org.example.model.orbitModel.OrbitWithSystemModel;
import org.example.model.systemModel.SystemWithDependencyModel;
import org.example.repository.DependencyRepository;
import org.example.repository.OrbitRepository;
import org.example.repository.SystemRepository;
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
    private final SystemRepository systemRepository;
    private final DependencyRepository dependencyRepository;

    private final OrbitMapper orbitMapper;
    private final SystemMapper systemMapper;
    private final DependencyMapper dependencyMapper;

    private final Logger logger = Logger.getLogger(GalaxyService.class.getName());

    public Orbit createOrbit(OrbitCreateModel model) {
        List<Orbit> orbitList;
        try {
            orbitList = orbitRepository.getOrbitsByGalaxyId(model.getGalaxyId());
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new GalaxyNotFoundException();
        }
        try {
            for (Orbit orbit : orbitList) {
                if (Objects.equals(orbit.getOrbitLevel(), model.getLevelOrbit())) {
                    throw new OrbitCoordinatesException();
                }
            }
            return orbitRepository.save(orbitMapper.createOrbitModelToOrbit(model));
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new OrbitCoordinatesException();
        }
    }


    public OrbitWithSystemModel getOrbitWithSystemList(Integer id) {
        try {
            OrbitWithSystemModel orbit = orbitMapper.orbitToOrbitWithSystemModel(orbitRepository.getReferenceById(id));
            orbit.setSystemWithDependencyModelList(systemRepository.getStarSystemByOrbitId(id).stream().map(x -> systemMapper.systemToSystemWithDependencyModel(x)).collect(Collectors.toList()));
            for (SystemWithDependencyModel systemWithDependencyModel : orbit.getSystemWithDependencyModelList()) {
                systemWithDependencyModel.setDependencyList(dependencyRepository.getListSystemDependencyParent(systemWithDependencyModel.getSystemId()
                ).stream().map(x -> dependencyMapper.DependencyModelToDependencyParentModel(x)).collect(Collectors.toList()));
                dependencyRepository.getListSystemDependencyChild(systemWithDependencyModel.getSystemId()).stream().filter(x -> x.getParent() != null)
                        .map(x -> dependencyMapper.DependencyModelToDependencyChildModel(x)).forEach(x -> systemWithDependencyModel.getDependencyList().add(x));
            }
            return orbit;
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new OrbitNotFoundException();
        }
    }

    public OrbitModel getOrbitById(Integer id) {
        try {
            return orbitMapper.createOrbitModelToOrbitModel(orbitRepository.getReferenceById(id));

        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new OrbitNotFoundException();
        }
    }

    public Orbit updateOrbit(Integer id, Orbit orbit) {
        try {
            Orbit updatedOrbit = orbitRepository.getReferenceById(id);
            updatedOrbit.setOrbitLevel(orbit.getOrbitLevel());
            updatedOrbit.setCountSystem(orbit.getCountSystem());
            return orbitRepository.save(updatedOrbit);
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
