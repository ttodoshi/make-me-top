package org.example.repository;

import org.example.model.Explorer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface ExplorerRepository extends JpaRepository<Explorer, Integer> {
    List<Explorer> findExplorersByGroup_CourseId(Integer courseId);

    Optional<Explorer> findExplorerByPersonIdAndGroup_CourseId(Integer personId, Integer groupId);

    List<Explorer> findExplorersByPersonId(Integer personId);

    @Override
    @Modifying
    @Query("DELETE FROM Explorer e WHERE e.explorerId = :explorerId")
    void deleteById(@NonNull @Param("explorerId") Integer explorerId);
}
