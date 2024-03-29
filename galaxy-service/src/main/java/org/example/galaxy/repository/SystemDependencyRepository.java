package org.example.galaxy.repository;

import org.example.galaxy.model.SystemDependency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SystemDependencyRepository extends JpaRepository<SystemDependency, Long> {
    @Query(value = "WITH RECURSIVE r AS (\n" +
            "   SELECT dependency_id,child_id, parent_id, is_alternative\n" +
            "   FROM system_dependency\n" +
            "   WHERE parent_id = ?1\n" +
            "\n" +
            "   UNION\n" +
            "\n" +
            "   SELECT system_dependency.dependency_id, system_dependency.child_id, system_dependency.parent_id, system_dependency.is_alternative\n" +
            "   FROM system_dependency\n" +
            "      JOIN r\n" +
            "          ON system_dependency.child_id = r.parent_id\n" +
            ")\n" +
            "\n" +
            "SELECT * FROM r\n" +
            "WHERE parent_id = ?1", nativeQuery = true)
    List<SystemDependency> getSystemChildren(Long systemId);

    @Query(value = "WITH RECURSIVE r AS (\n" +
            "   SELECT dependency_id,child_id, parent_id, is_alternative\n" +
            "   FROM system_dependency\n" +
            "   WHERE child_id = ?1\n" +
            "\n" +
            "   UNION\n" +
            "\n" +
            "   SELECT system_dependency.dependency_id, system_dependency.child_id, system_dependency.parent_id, system_dependency.is_alternative\n" +
            "   FROM system_dependency\n" +
            "      JOIN r\n" +
            "          ON system_dependency.child_id = r.parent_id\n" +
            ")\n" +
            "\n" +
            "SELECT * FROM r\n" +
            "WHERE child_id = ?1", nativeQuery = true)
    List<SystemDependency> getSystemParents(Long systemId);

    Optional<SystemDependency> findSystemDependencyByChildIdAndParentId(Long childId, Long parentId);

    Optional<SystemDependency> findSystemDependencyByChildIdAndParentNull(Long childId);

    boolean existsSystemDependencyByChildIdAndParentId(Long childId, Long parentId);

    boolean existsSystemDependencyByChildIdAndParentNull(Long childId);
}
