package org.example.repository;

import org.example.dto.explorer.ExplorerDTO;
import org.example.model.Explorer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExplorerRepository extends JpaRepository<Explorer, Integer> {
    Optional<Explorer> findExplorerByPersonIdAndCourseId(Integer personId, Integer courseId);

    @Query(value = "SELECT new org.example.dto.explorer.ExplorerDTO(p.personId, p.firstName, p.lastName, p.patronymic, e.explorerId) FROM Explorer e\n" +
            "JOIN Person p ON p.personId = e.personId\n" +
            "WHERE e.courseId = :courseId")
    List<ExplorerDTO> findExplorersByCourseId(@Param("courseId") Integer courseId);
}
