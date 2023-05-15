package org.example.service;

import org.example.exception.dependencyEX.DependencyAlreadyExistsException;
import org.example.exception.dependencyEX.DependencyNotFound;
import org.example.exception.systemEX.SystemNotFoundException;
import org.example.model.dependencyModel.CreateDependencyModel;
import org.example.model.dependencyModel.DeleteDependencyModel;
import org.example.model.modelDAO.SystemDependency;
import org.example.repository.DependencyRepository;
import org.example.repository.SystemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DependencyService {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    DependencyRepository dependencyRepository;
    @Autowired
    SystemRepository systemRepository;
    private StringBuilder DEPENDENCY_QUERY;
    SystemDependency systemDependency;

    public void addDependency(List<CreateDependencyModel> systemDependency) {
        DEPENDENCY_QUERY = new StringBuilder("INSERT INTO system_dependency (child_id, parent_id, is_alternative)VALUES");

        for (CreateDependencyModel dependency : systemDependency) {
            if (systemRepository.checkExistsSystem(dependency.getChildId()) == null ||
                    (dependency.getParentId() != null && systemRepository.checkExistsSystem(dependency.getParentId()) == null)) {
                throw new SystemNotFoundException();
            }
            if (dependency.getParentId() == null) {
                if (dependencyRepository.getSystemDependencyByChildIdAndParentNull(dependency.getChildId()) != null) {
                    throw new DependencyAlreadyExistsException();
                }
            } else {
                if (dependencyRepository.getSystemDependencyByChildIDAndParentId(dependency.getChildId(), dependency.getParentId()) != null) {
                    throw new DependencyAlreadyExistsException();
                }
            }
            DEPENDENCY_QUERY
                    .append("(").append(dependency.getChildId())
                    .append(",").append(dependency.getParentId())
                    .append(",")
                    .append(dependency.getIsAlternative())
                    .append("),");
        }
        DEPENDENCY_QUERY.replace(DEPENDENCY_QUERY.length() - 1, DEPENDENCY_QUERY.length(), "");
        jdbcTemplate.execute(DEPENDENCY_QUERY.toString());
    }

    public void deleteDependency(DeleteDependencyModel dependency) {
        if (dependency.getParentId() == null) {
            try {
                systemDependency = dependencyRepository.getSystemDependencyByChildIdAndParentNull(dependency.getChildId());
                dependencyRepository.deleteById(systemDependency.getId());
            } catch (Exception e) {
                throw new DependencyNotFound();
            }

        } else {
            try {
                systemDependency = dependencyRepository.getSystemDependencyByChildIDAndParentId(dependency.getChildId(), dependency.getParentId());
                dependencyRepository.deleteById(systemDependency.getId());
            } catch (Exception e) {
                throw new DependencyNotFound();
            }
        }

    }
}
