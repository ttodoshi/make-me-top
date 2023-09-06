package org.example.repository;

import org.example.model.Explorer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ExplorerRepository extends JpaRepository<Explorer, Integer> {
    Optional<Explorer> findExplorerByPersonIdAndGroupId(Integer personId, Integer groupId);

    @Query(value = "SELECT e FROM Explorer e\n" +
            "JOIN ExplorerGroup eg ON eg.groupId = e.groupId\n" +
            "WHERE e.personId = :personId AND eg.courseId = :courseId")
    Optional<Explorer> findExplorerByPersonIdAndCourseId(@Param("personId") Integer personId, @Param("courseId") Integer courseId);
}
