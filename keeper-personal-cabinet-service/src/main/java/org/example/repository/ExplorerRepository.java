package org.example.repository;

import org.example.dto.explorer.ExplorerNeededFinalAssessmentDTO;
import org.example.model.Explorer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface ExplorerRepository extends JpaRepository<Explorer, Integer> {
    Optional<Explorer> findExplorerByPersonIdAndCourseId(Integer personId, Integer courseId);

    @Query(value = "SELECT new org.example.dto.explorer.ExplorerNeededFinalAssessmentDTO(p.personId, p.firstName, p.lastName, p.patronymic, c.courseId, c.title, e.explorerId)\n" +
            "FROM Explorer e\n" +
            "JOIN Person p ON p.personId = e.personId\n" +
            "JOIN Course c ON c.courseId = e.courseId\n" +
            "JOIN CourseThemeProgress ctp ON ctp.explorerId = e.explorerId\n" +
            "JOIN Keeper k ON k.courseId = c.courseId\n" +
            "WHERE (\n" +
            "\tSELECT COUNT(*) FROM CourseTheme ct\n" +
            "\tJOIN Course c1 ON c1.courseId = ct.courseId\n" +
            "\tJOIN Explorer e1 ON e1.courseId = c1.courseId\n" +
            "\tWHERE e1.explorerId = e.explorerId\n" +
            ") = (\n" +
            "\tSELECT COUNT(*) FROM CourseThemeProgress ctp1\n" +
            "\tJOIN Explorer e1 ON e1.explorerId = ctp1.explorerId\n" +
            "\tWHERE ctp1.progress = 100 AND e1.explorerId = e.explorerId\n" +
            ")\n" +
            "AND e.explorerId NOT IN (\n" +
            "\tSELECT cm.explorerId FROM CourseMark cm\n" +
            ")\n" +
            "AND k.personId = :personId")
    Set<ExplorerNeededFinalAssessmentDTO> getExplorersNeededFinalAssessmentByKeeperPersonId(@Param("personId") Integer personId);
}
