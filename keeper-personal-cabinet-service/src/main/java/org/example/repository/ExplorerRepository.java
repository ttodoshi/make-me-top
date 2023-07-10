package org.example.repository;

import org.example.dto.explorer.ExplorerDTO;
import org.example.dto.explorer.ExplorerNeededFinalAssessmentDTO;
import org.example.model.Explorer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface ExplorerRepository extends JpaRepository<Explorer, Integer> {
    @Query(value = "SELECT COUNT(*) FROM course.course_registration_request\n" +
            "JOIN course.keeper ON keeper.keeper_id = course_registration_request.keeper_id\n" +
            "JOIN course.course_registration_request_status\n" +
            "ON course_registration_request_status.status_id = course_registration_request.status_id\n" +
            "WHERE keeper.person_id = ?1 AND course_registration_request_status.status = 'APPROVED'",
            nativeQuery = true)
    Integer getExplorersCountForKeeper(Integer personId);

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

    @Query(value = "SELECT new org.example.dto.explorer.ExplorerDTO(p.personId, p.firstName, p.lastName, p.patronymic, e.explorerId, c.courseId)\n" +
            "FROM Explorer e\n" +
            "JOIN Person p ON e.personId = p.personId\n" +
            "JOIN Course c ON c.courseId = e.courseId\n" +
            "JOIN Keeper k ON k.courseId = c.courseId\n" +
            "WHERE k.personId = :personId AND e.explorerId NOT IN (\n" +
            "\tSELECT cm.explorerId FROM CourseMark cm\n" +
            ")")
    List<ExplorerDTO> getStudyingPeopleByKeeperPersonId(@Param("personId") Integer personId);
}
