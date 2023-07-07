package org.example.repository;

import org.example.dto.course.CourseWithRatingDTO;
import org.example.model.CourseMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseMarkRepository extends JpaRepository<CourseMark, Integer> {
    // TODO rating
    @Query(value = "SELECT new org.example.dto.course.CourseWithRatingDTO(\n" +
            "\tc.courseId, c.title, 5.0 as rating\n" +
            ")\n" +
            "FROM Explorer e\n" +
            "JOIN Course c ON c.courseId = e.courseId\n" +
            "JOIN CourseMark cm ON cm.explorerId = e.explorerId\n" +
            "WHERE e.personId = :personId")
    List<CourseWithRatingDTO> getInvestigatedSystemsByPersonId(@Param("personId") Integer personId);
}
