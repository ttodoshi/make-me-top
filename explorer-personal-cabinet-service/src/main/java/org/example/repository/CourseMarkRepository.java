package org.example.repository;

import org.example.dto.course.CourseWithMark;
import org.example.model.progress.CourseMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseMarkRepository extends JpaRepository<CourseMark, Integer> {
    @Query(value = "SELECT new org.example.dto.course.CourseWithMark(\n" +
            "\tc.courseId, c.title, cm.value, k.keeperId\n" +
            ")\n" +
            "FROM Explorer e\n" +
            "JOIN Course c ON c.courseId = e.courseId\n" +
            "JOIN Keeper k ON k.courseId = c.courseId\n" +
            "JOIN CourseMark cm ON cm.explorerId = e.explorerId\n" +
            "WHERE e.personId = :personId")
    List<CourseWithMark> getInvestigatedSystemsByPersonId(@Param("personId") Integer personId);
}
