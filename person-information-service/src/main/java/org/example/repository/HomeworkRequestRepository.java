package org.example.repository;

import org.example.dto.homework.HomeworkRequestDTO;
import org.example.model.homework.HomeworkRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HomeworkRequestRepository extends JpaRepository<HomeworkRequest, Integer> {
    @Query(value = "SELECT new org.example.dto.homework.HomeworkRequestDTO(\n" +
            "\thr.requestId, p.personId, p.firstName, p.lastName, p.patronymic, c.courseId, c.title as courseTitle, e.explorerId, ct.courseThemeId, ct.title as courseThemeTitle\n" +
            ")\n" +
            "FROM HomeworkRequest hr\n" +
            "JOIN Keeper k ON k.keeperId = hr.keeperId\n" +
            "JOIN Explorer e ON e.explorerId = hr.explorerId\n" +
            "JOIN Person p ON p.personId = e.personId\n" +
            "JOIN Homework h ON h.homeworkId = hr.homeworkId\n" +
            "JOIN CourseTheme ct ON ct.courseThemeId = h.courseThemeId\n" +
            "JOIN Course c ON c.courseId = ct.courseId\n" +
            "WHERE k.personId = :personId")
    List<HomeworkRequestDTO> getReviewRequestsByKeeperPersonId(@Param("personId") Integer personId);
}
