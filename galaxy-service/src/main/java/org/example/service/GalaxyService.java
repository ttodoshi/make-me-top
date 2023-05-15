package org.example.service;

import org.example.config.mapper.DependencyMapper;
import org.example.config.mapper.GalaxyMapper;
import org.example.config.mapper.OrbitMapper;
import org.example.config.mapper.SystemMapper;
import org.example.exception.galaxyEX.GalacxycAlreadyExistsException;
import org.example.exception.galaxyEX.GalacxyNotFoundException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GalaxyService {
    @Autowired
    GalaxyMapper galaxyMapper;
    @Autowired
    OrbitMapper orbitMapper;
    @Autowired
    SystemMapper systemMapper;
    @Autowired
    DependencyMapper dependencyMapper;

    @Autowired
    GalaxyRepository galaxyRepository;
    @Autowired
    OrbitRepository orbitRepository;

    @Autowired
    SystemRepository systemRepository;

    @Autowired
    DependencyRepository dependencyRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    private StringBuilder QUERY_GALAXY;
    private StringBuilder QUERY_ORBIT;
    private StringBuilder QUERY_SYSTEM;
    GalaxyWithOrbitModel galaxy;
    Galaxy galaxyUp;

    public GalaxyWithOrbitModel getGalaxyById(Integer id) {
        try {
            galaxy = galaxyMapper.mapGalaxy(galaxyRepository.getReferenceById(id));

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

        QUERY_GALAXY = new StringBuilder("INSERT INTO galaxy VALUES (").append(model.getGalaxyId())
                .append(",'")
                .append(model.getGalaxyName())
                .append("');");
        try {
            jdbcTemplate.execute(QUERY_GALAXY.toString());
        } catch (RuntimeException e) {
            throw new GalacxycAlreadyExistsException();
        }
        QUERY_ORBIT = new StringBuilder("INSERT INTO orbit VALUES");
        if (model.getOrbitsList() != null) {
            for (OrbitCreateWithOutGalaxyIdModel orbit : model.getOrbitsList()) {
                QUERY_ORBIT.append("(")
                        .append(orbit.getOrbitId())
                        .append(",")
                        .append(orbit.getLevelOrbit())
                        .append(",")
                        .append(orbit.getCountSystem())
                        .append(",")
                        .append(model.getGalaxyId())
                        .append("),");
                if (orbit.getSystemsList() != null) {
                    QUERY_SYSTEM = new StringBuilder("INSERT INTO star_system VALUES");
                    for (SystemCreateModel system : orbit.getSystemsList()) {
                        QUERY_SYSTEM.append("(")
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
            QUERY_SYSTEM.replace(QUERY_SYSTEM.length() - 1, QUERY_SYSTEM.length(), ";");
            QUERY_ORBIT.replace(QUERY_ORBIT.length() - 1, QUERY_ORBIT.length(), ";");
            try {
                jdbcTemplate.execute(QUERY_ORBIT.toString());
            } catch (RuntimeException e) {
                galaxyRepository.deleteById(model.getGalaxyId());
                throw new OrbitAlreadyExistsException();

            }
            try {
                jdbcTemplate.execute(QUERY_SYSTEM.toString());
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
            galaxyUp = galaxyRepository.getReferenceById(id);
            galaxyUp.setGalaxyName(model.getGalaxyName());
        } catch (Exception e) {
            throw new GalacxyNotFoundException();
        }
        try {
            galaxyRepository.save(galaxyUp);
        } catch (RuntimeException e) {
            throw new GalacxycAlreadyExistsException();
        }
    }

    public void deleteGalaxy(Integer id) {
        try {
            galaxyRepository.deleteById(id);
        } catch (Exception e) {
            throw new GalacxyNotFoundException();
        }
    }

    public List<Galaxy> getAllGalaxy() {
        return galaxyRepository.getAllGalaxy();
    }
}
