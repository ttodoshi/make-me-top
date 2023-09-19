package org.example.repository;

import org.example.model.Explorer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExplorerRepository extends JpaRepository<Explorer, Integer> {
    List<Explorer> findExplorersByGroup_CourseId(Integer courseId);

    Optional<Explorer> findExplorerByPersonIdAndGroup_CourseId(Integer personId, Integer groupId);

    List<Explorer> findExplorersByPersonId(Integer personId);
}
