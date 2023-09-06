package org.example.repository;

import org.example.dto.explorer.ExplorerDTO;
import org.example.dto.explorer.ExplorerNeededFinalAssessment;
import org.example.model.Explorer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ExplorerRepository extends JpaRepository<Explorer, Integer> {
    @Query(value = "SELECT e FROM Explorer e\n" +
            "JOIN ExplorerGroup eg ON eg.groupId = e.groupId\n" +
            " WHERE e.personId = :personId AND eg.courseId = :courseId")
    Optional<Explorer> findExplorerByPersonIdAndCourseId(@Param("personId") Integer personId, @Param("courseId") Integer courseId);

    @Query(value = "SELECT COUNT(*) FROM ExplorerGroup eg\n" +
            "JOIN Explorer e ON e.groupId = eg.groupId\n" +
            "JOIN Keeper k ON k.keeperId = eg.keeperId\n" +
            "WHERE k.personId = :personId")
    Integer getExplorersCountForKeeper(@Param("personId") Integer personId);

    @Query(value = "SELECT new org.example.dto.explorer.ExplorerNeededFinalAssessment(\n" +
            "   p.personId, p.firstName, p.lastName, p.patronymic, c.courseId, c.title, e.explorerId\n" +
            ")\n" +
            "FROM Explorer e\n" +
            "JOIN Person p ON p.personId = e.personId\n" +
            "JOIN ExplorerGroup eg ON eg.groupId = e.groupId\n" +
            "JOIN Course c ON c.courseId = eg.courseId\n" +
            "JOIN CourseThemeCompletion ctc ON ctc.explorerId = e.explorerId\n" +
            "JOIN Keeper k ON k.courseId = c.courseId\n" +
            "WHERE (\n" +
            "   SELECT COUNT(*) FROM CourseTheme ct\n" +
            "   JOIN Course c1 ON c1.courseId = ct.courseId\n" +
            "   JOIN ExplorerGroup eg1 ON eg1.courseId = c1.courseId\n" +
            "   JOIN Explorer e1 ON e1.groupId = eg1.groupId\n" +
            "   WHERE e1.explorerId = e.explorerId\n" +
            ") = (\n" +
            "   SELECT COUNT(*) FROM CourseThemeCompletion ctp1\n" +
            "   JOIN Explorer e1 ON e1.explorerId = ctp1.explorerId\n" +
            "   WHERE e1.explorerId = e.explorerId\n" +
            ")\n" +
            "AND e.explorerId NOT IN (\n" +
            "   SELECT cm.explorerId FROM CourseMark cm\n" +
            ")\n" +
            "AND k.personId = :personId")
    Set<ExplorerNeededFinalAssessment> getExplorersNeededFinalAssessmentByKeeperPersonId(@Param("personId") Integer personId);

    @Query(value = "SELECT new org.example.dto.explorer.ExplorerDTO(p.personId, p.firstName, p.lastName, p.patronymic, e.explorerId, c.courseId)\n" +
            "FROM Explorer e\n" +
            "JOIN Person p ON e.personId = p.personId\n" +
            "JOIN ExplorerGroup eg ON eg.groupId = e.groupId\n" +
            "JOIN Course c ON c.courseId = eg.courseId\n" +
            "JOIN Keeper k ON k.courseId = c.courseId\n" +
            "WHERE k.personId = :personId AND e.explorerId NOT IN (\n" +
            "\tSELECT cm.explorerId FROM CourseMark cm\n" +
            ")")
    List<ExplorerDTO> getStudyingPeopleByKeeperPersonId(@Param("personId") Integer personId);

    @Query(value = "SELECT COUNT(*) FROM CourseMark cm\n" +
            "JOIN Explorer e ON e.explorerId = cm.explorerId\n" +
            "WHERE e.personId = :personId")
    Integer getExplorerSystemsCount(@Param("personId") Integer personId);
}
