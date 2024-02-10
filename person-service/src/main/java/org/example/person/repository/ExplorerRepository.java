package org.example.person.repository;

import org.example.person.model.Explorer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface ExplorerRepository extends JpaRepository<Explorer, Long> {
    List<Explorer> findExplorersByGroup_CourseId(Long courseId);

    Optional<Explorer> findExplorerByPersonIdAndGroup_CourseId(Long personId, Long courseId);

    List<Explorer> findExplorersByPersonId(Long personId);

    List<Explorer> findExplorersByPersonIdIn(List<Long> personIds);

    List<Explorer> findExplorersByExplorerIdIn(List<Long> explorerIds);

    List<Explorer> findExplorersByGroup_CourseIdIn(List<Long> courseIds);

    List<Explorer> findExplorersByPersonIdAndGroup_CourseIdIn(Long personId, List<Long> courseIds);

    boolean existsExplorerByPersonIdAndGroup_CourseId(Long personId, Long courseId);

    @Override
    @Modifying
    @Query("DELETE FROM Explorer e WHERE e.explorerId = :explorerId")
    void deleteById(@NonNull @Param("explorerId") Long explorerId);
}
