package org.example.service;

import org.aspectj.weaver.ast.Or;
import org.example.config.mapper.DependencyMapper;
import org.example.config.mapper.OrbitMapper;
import org.example.config.mapper.SystemMapper;
import org.example.exception.galaxyEX.GalacxyNotFoundException;
import org.example.exception.orbitEX.OrbitAlreadyExistsException;
import org.example.exception.orbitEX.OrbitCoordinatesException;
import org.example.exception.orbitEX.OrbitDeleteException;
import org.example.exception.orbitEX.OrbitNotFoundException;
import org.example.model.modelDAO.Galaxy;
import org.example.model.modelDAO.Orbit;
import org.example.model.orbitModel.OrbitCreateModel;
import org.example.model.orbitModel.OrbitModel;
import org.example.model.orbitModel.OrbitWithSystemModel;
import org.example.model.systemModel.SystemWithDependencyModel;
import org.example.repository.DependencyRepository;
import org.example.repository.GalaxyRepository;
import org.example.repository.OrbitRepository;
import org.example.repository.SystemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class OrbitService {
    @Autowired
    OrbitMapper orbitMapper;
    @Autowired
    SystemMapper systemMapper;
    @Autowired
    DependencyMapper dependencyMapper;
    @Autowired
    OrbitRepository orbitRepository;
    @Autowired
    SystemRepository systemRepository;
    @Autowired
    DependencyRepository dependencyRepository;

    OrbitWithSystemModel orbit;
    Orbit orbitUpdate;

    public void createOrbit(OrbitCreateModel model) {
        List<Orbit> orbitList;
        try {
            orbitList = orbitRepository.getOrbitsByGalacticId(model.getGalaxyId());
        } catch (Exception e) {
            throw new GalacxyNotFoundException();
        }
        for (Orbit orbit : orbitList) {
            if (Objects.equals(orbit.getOrbitLevel(), model.getLevelOrbit())) {
                throw new OrbitCoordinatesException();
            }
        }
        orbitRepository.save(orbitMapper.createOrbitModelToOrbit(model));
    }


    public OrbitWithSystemModel getOrbitWithSystemList(Integer id) {
        try {
            orbit = orbitMapper.orbitToOrbitWithSystemModel(orbitRepository.getReferenceById(id));
            orbit.setSystemWithDependencyModelList(systemRepository.getStarSystemByOrbitId(id).stream().map(x -> systemMapper.systemToSystemWithDependencyModel(x)).collect(Collectors.toList()));
            for (SystemWithDependencyModel systemWithDependencyModel : orbit.getSystemWithDependencyModelList()) {
                systemWithDependencyModel.setDependencyList(dependencyRepository.getListSystemDependencyParent(systemWithDependencyModel.getSystemId()
                ).stream().map(x -> dependencyMapper.DependencyModelToDependencyParentModel(x)).collect(Collectors.toList()));
                dependencyRepository.getListSystemDependencyChild(systemWithDependencyModel.getSystemId()).stream().filter(x -> x.getParent() != null)
                        .map(x -> dependencyMapper.DependencyModelToDependencyChildModel(x)).forEach(x -> systemWithDependencyModel.getDependencyList().add(x));
            }
            return orbit;
        } catch (Exception e) {
            throw new OrbitNotFoundException();
        }
    }

    public OrbitModel getOrbitById(Integer id){
        try {
          return orbitMapper.createOrbitModelToOrbitModel(orbitRepository.getReferenceById(id));

        } catch (Exception e) {
            throw new OrbitNotFoundException();
        }
    }

    public void updateOrbit(Integer id, Orbit orbit){
        try {
            orbitUpdate = orbitRepository.getReferenceById(id);
            orbitUpdate.setOrbitLevel(orbit.getOrbitLevel());
            orbitUpdate.setCountSystem(orbit.getCountSystem());
        } catch (Exception e) {
            throw new OrbitNotFoundException();
        }
        try {
            orbitRepository.save(orbitUpdate);
        } catch (Exception e) {
            throw new OrbitAlreadyExistsException();
        }
    }

    public void deleteOrbit(Integer id) {
        getOrbitById(id);
        try {
            orbitRepository.deleteById(id);
        } catch (Exception e) {
            throw new OrbitDeleteException();
        }
    }
}
