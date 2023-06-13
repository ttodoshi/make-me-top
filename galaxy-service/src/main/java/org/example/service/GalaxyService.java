package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.config.mapper.DependencyMapper;
import org.example.config.mapper.GalaxyMapper;
import org.example.config.mapper.OrbitMapper;
import org.example.config.mapper.SystemMapper;
import org.example.exception.galaxyEX.GalacxyNotFoundException;
import org.example.exception.galaxyEX.GalacxycAlreadyExistsException;
import org.example.exception.orbitEX.OrbitAlreadyExistsException;
import org.example.exception.systemEX.SystemAlreadyExistsException;
import org.example.model.galaxyModel.CreateGalaxyModel;
import org.example.model.galaxyModel.GalaxyModel;
import org.example.model.galaxyModel.GalaxyWithOrbitModel;
import org.example.model.modelDAO.Galaxy;
import org.example.model.orbitModel.OrbitCreateWithOutGalaxyIdModel;
import org.example.model.orbitModel.OrbitWithSystemModel;
import org.example.model.systemModel.SystemCreateModel;
import org.example.model.systemModel.SystemWithDependencyModel;
import org.example.repository.DependencyRepository;
import org.example.repository.GalaxyRepository;
import org.example.repository.OrbitRepository;
import org.example.repository.SystemRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GalaxyService {
    private final GalaxyRepository galaxyRepository;
    private final OrbitRepository orbitRepository;
    private final SystemRepository systemRepository;
    private final DependencyRepository dependencyRepository;

    private final GalaxyMapper galaxyMapper;
    private final OrbitMapper orbitMapper;
    private final SystemMapper systemMapper;
    private final DependencyMapper dependencyMapper;

    private final JdbcTemplate jdbcTemplate;

    private StringBuilder systemQuery;

    public GalaxyWithOrbitModel getGalaxyById(Integer id) {
        try {
            GalaxyWithOrbitModel galaxy = galaxyMapper.mapGalaxy(galaxyRepository.getReferenceById(id));

            galaxy.setOrbitsList(orbitRepository.getOrbitsByGalacticId(id).stream().map(x -> orbitMapper.orbitToOrbitWithSystemModel(x)).collect(Collectors.toList()));
            for (OrbitWithSystemModel orbitWithSystemModel : galaxy.getOrbitsList()) {
                orbitWithSystemModel.setSystemWithDependencyModelList(systemRepository.getStarSystemByOrbitId(orbitWithSystemModel.getOrbitId()).stream().
                        map(x -> systemMapper.systemToSystemWithDependencyModel(x)).collect(Collectors.toList()));
                for (SystemWithDependencyModel systemWithDependencyModel : orbitWithSystemModel.getSystemWithDependencyModelList()) {
                    systemWithDependencyModel.setDependencyList(dependencyRepository.getListSystemDependencyParent(systemWithDependencyModel.getSystemId()
                    ).stream().map(x -> dependencyMapper.DependencyModelToDependencyParentModel(x)).collect(Collectors.toList()));
                    dependencyRepository.getListSystemDependencyChild(systemWithDependencyModel.getSystemId()).stream().filter(x -> x.getParent() != null)
                            .map(x -> dependencyMapper.DependencyModelToDependencyChildModel(x)).forEach(x -> systemWithDependencyModel.getDependencyList().add(x));
                }
            }
            return galaxy;
        } catch (Exception e) {
            throw new GalacxyNotFoundException();
        }
    }

    public void createGalaxy(CreateGalaxyModel model) {
        StringBuilder galaxyQuery = new StringBuilder("INSERT INTO galaxy VALUES (").append(model.getGalaxyId())
                .append(",'")
                .append(model.getGalaxyName())
                .append("');");
        try {
            jdbcTemplate.execute(galaxyQuery.toString());
        } catch (RuntimeException e) {
            throw new GalacxycAlreadyExistsException();
        }
        StringBuilder orbitQuery = new StringBuilder("INSERT INTO orbit VALUES");
        if (model.getOrbitsList() != null) {
            for (OrbitCreateWithOutGalaxyIdModel orbit : model.getOrbitsList()) {
                orbitQuery.append("(")
                        .append(orbit.getOrbitId())
                        .append(",")
                        .append(orbit.getLevelOrbit())
                        .append(",")
                        .append(orbit.getCountSystem())
                        .append(",")
                        .append(model.getGalaxyId())
                        .append("),");
                if (orbit.getSystemsList() != null) {
                    systemQuery = new StringBuilder("INSERT INTO star_system VALUES");
                    for (SystemCreateModel system : orbit.getSystemsList()) {
                        systemQuery.append("(")
                                .append(system.getSystemId())
                                .append(",")
                                .append(system.getPositionSystem())
                                .append(",")
                                .append(system.getSystemLevel())
                                .append(",'")
                                .append(system.getSystemName())
                                .append("',")
                                .append(orbit.getOrbitId())
                                .append("),");
                    }
                }
            }
            systemQuery.replace(systemQuery.length() - 1, systemQuery.length(), ";");
            orbitQuery.replace(orbitQuery.length() - 1, orbitQuery.length(), ";");
            try {
                jdbcTemplate.execute(orbitQuery.toString());
            } catch (RuntimeException e) {
                galaxyRepository.deleteById(model.getGalaxyId());
                throw new OrbitAlreadyExistsException();

            }
            try {
                jdbcTemplate.execute(systemQuery.toString());
            } catch (RuntimeException e) {
                for (OrbitCreateWithOutGalaxyIdModel orbit : model.getOrbitsList()) {
                    orbitRepository.deleteById(orbit.getOrbitId());
                }
                galaxyRepository.deleteById(model.getGalaxyId());
                throw new SystemAlreadyExistsException();
            }
        }
    }

    public void updateGalaxy(Integer id, GalaxyModel model) {
        try {
            Galaxy galaxyUp = galaxyRepository.getReferenceById(id);
            galaxyUp.setGalaxyName(model.getGalaxyName());
            galaxyRepository.save(galaxyUp);
        } catch (RuntimeException e) {
            throw new GalacxycAlreadyExistsException();
        } catch (Exception e) {
            throw new GalacxyNotFoundException();
        }
    }

    public void deleteGalaxy(Integer id) {
        try {
            galaxyRepository.deleteById(id);
        } catch (Exception e) {
            throw new GalacxyNotFoundException();
        }
    }

    public List<Galaxy> getAllGalaxies() {
        return galaxyRepository.getAllGalaxy();
    }
}
