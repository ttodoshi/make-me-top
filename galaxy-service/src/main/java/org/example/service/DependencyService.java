package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.dependency.DeleteDependencyRequest;
import org.example.dto.dependency.DependencyDTO;
import org.example.exception.classes.dependencyEX.DependencyAlreadyExistsException;
import org.example.exception.classes.dependencyEX.DependencyNotFoundException;
import org.example.exception.classes.systemEX.SystemNotFoundException;
import org.example.model.SystemDependency;
import org.example.repository.DependencyRepository;
import org.example.repository.StarSystemRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class DependencyService {
    private final DependencyRepository dependencyRepository;
    private final StarSystemRepository starSystemRepository;

    private final ModelMapper mapper;

    private final Logger logger = Logger.getLogger(DependencyService.class.getName());

    public List<SystemDependency> addDependency(List<DependencyDTO> systemDependency) {
        List<SystemDependency> dependencies = new LinkedList<>();
        try {
            for (DependencyDTO dependency : systemDependency) {
                if (starSystemRepository.existsById(dependency.getChildId()) ||
                        (dependency.getParentId() != null && starSystemRepository.existsById(dependency.getParentId())))
                    throw new SystemNotFoundException();
                if (dependency.getParentId() == null) {
                    if (dependencyRepository.getSystemDependencyByChildIdAndParentNull(dependency.getChildId()) != null) {
                        throw new DependencyAlreadyExistsException();
                    }
                } else {
                    if (dependencyRepository.getSystemDependencyByChildIDAndParentId(dependency.getChildId(), dependency.getParentId()) != null) {
                        throw new DependencyAlreadyExistsException();
                    }
                }
                dependencies.add(dependencyRepository.save(mapper.map(dependency, SystemDependency.class)));
            }
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
        return dependencies;
    }

    public Map<String, String> deleteDependency(DeleteDependencyRequest dependency) {
        SystemDependency systemDependency;
        if (dependency.getParentId() == null) {
            try {
                systemDependency = dependencyRepository.getSystemDependencyByChildIdAndParentNull(dependency.getChildId());
                dependencyRepository.deleteById(systemDependency.getDependencyId());
            } catch (Exception e) {
                logger.severe(e.getMessage());
                throw new DependencyNotFoundException();
            }
        } else {
            try {
                systemDependency = dependencyRepository.getSystemDependencyByChildIDAndParentId(dependency.getChildId(), dependency.getParentId());
                dependencyRepository.deleteById(systemDependency.getDependencyId());
            } catch (Exception e) {
                logger.severe(e.getMessage());
                throw new DependencyNotFoundException();
            }
        }
        Map<String, String> response = new HashMap<>();
        response.put("message", "Зависимость удалена");
        return response;
    }
}
