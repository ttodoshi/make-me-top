package org.example.repository.homework;

import org.example.dto.homework.HomeworkRequestDTO;
import org.example.model.homework.HomeworkRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HomeworkRequestRepository extends JpaRepository<HomeworkRequest, Integer> {
    @Query(value = "SELECT new org.example.dto.homework.HomeworkRequestDTO(\n" +
            "   hr.requestId, p.personId, p.firstName, p.lastName, p.patronymic, c.courseId, c.title, e.explorerId, ct.courseThemeId, ct.title, h.homeworkId\n" +
            ")\n" +
            "FROM HomeworkRequest hr\n" +
            "JOIN HomeworkRequestStatus hrs ON hrs.statusId = hr.statusId\n" +
            "JOIN Explorer e ON e.explorerId = hr.explorerId\n" +
            "JOIN Person p ON p.personId = e.personId\n" +
            "JOIN ExplorerGroup eg ON eg.groupId = e.groupId\n" +
            "JOIN Keeper k ON k.keeperId = eg.keeperId\n" +
            "JOIN Homework h ON h.homeworkId = hr.homeworkId\n" +
            "JOIN CourseTheme ct ON ct.courseThemeId = h.courseThemeId\n" +
            "JOIN Course c ON c.courseId = ct.courseId\n" +
            "WHERE k.personId = :personId AND hrs.status = 'CHECKING'")
    List<HomeworkRequestDTO> getReviewRequestsByKeeperPersonId(@Param("personId") Integer personId);
}
