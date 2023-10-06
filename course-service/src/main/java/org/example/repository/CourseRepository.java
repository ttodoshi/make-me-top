package org.example.repository;

import org.example.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    List<Course> findCoursesByCourseIdIn(List<Integer> courseIds);

    @Query("SELECT c.courseId FROM Course c\n" +
            "JOIN CourseTheme ct ON ct.courseId = c.courseId\n" +
            "WHERE ct.courseThemeId = :themeId")
    Optional<Integer> getCourseIdByThemeId(@Param("themeId") Integer themeId);
}
