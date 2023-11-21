package org.example.galaxy.repository;

import org.example.galaxy.model.SystemDependency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SystemDependencyRepository extends JpaRepository<SystemDependency, Integer> {
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
    List<SystemDependency> getSystemChildren(Integer id);


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
    List<SystemDependency> getSystemParents(Integer id);

    @Query(value = "SELECT * FROM system_dependency WHERE child_id=?1 and parent_id = ?2", nativeQuery = true)
    Optional<SystemDependency> getSystemDependencyByChildIdAndParentId(Integer childId, Integer parentId);

    @Query(value = "SELECT * FROM system_dependency WHERE child_id=?1 and parent_id ISNULL", nativeQuery = true)
    Optional<SystemDependency> getSystemDependencyByChildIdAndParentNull(Integer childId);
}
